package jmetal.operators.populationGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
import jmetal.metaheuristics.singleObjective.geneticAlgorithm.DynamicBPGA;
import jmetal.problems.DynBugPrioritization;
import jmetal.util.JMException;

public class PopGenWithSimplePropag extends PopulationGenerator{

	int populationSize;
	
	SimplePopulationGenerator spg;
	
	public PopGenWithSimplePropag(HashMap<String, Object> parameters) {
		super(parameters);
		populationSize = (Integer)parameters.get("populationSize");
		spg = new SimplePopulationGenerator(parameters);
	}

	@Override
	public Object execute(Object object) throws JMException {
		HashMap hm = (HashMap)object;
		DynBugPrioritization problem_ = (DynBugPrioritization)hm.get("problem");
		PriorityQueue<Integer> indexes = (PriorityQueue<Integer>)hm.get("removedBugsIndexes");
		SolutionSet prevPopulation = (SolutionSet)hm.get("previousPopulation");
		HashMap result = new HashMap();
		if(prevPopulation == null) {
			result.put("population", spg.execute(problem_));
			result.put("changeImpact", 0.0);
			return result;
		}

		double changeImpact = 0;
		
		SolutionSet population = new SolutionSet(populationSize);
		for (int i = 0; i < prevPopulation.size(); i++) {
			Solution solution = null;
			
			try {
				solution = new Solution(problem_);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			Variable [] oldVars = prevPopulation.get(i).getDecisionVariables();
			Variable [] newVars = solution.getDecisionVariables();
			
			ArrayList<Integer> suffledIndexes = new ArrayList<Integer>();
			for (int j = 0; j < problem_.getBugs().size(); j++) {
				suffledIndexes.add(j);
			}
			
			Collections.shuffle(suffledIndexes);

			int counter = 0;
			
			for (int j = 0; j < newVars.length; j++) { // replacement of removed bugs
				newVars[j] = oldVars[j].deepCopy();
				
				if(indexes.contains((int)newVars[j].getValue())
						|| (int)newVars[j].getValue() >= problem_.getBugs().size()) {
					
					while(true) {
						int replacement = suffledIndexes.get(counter++);
						if(replacement == (int)newVars[j].getValue())
							break;
						boolean isInSolution = isInSolution(replacement, newVars);
						
						if(!isInSolution) {
							newVars[j].setValue(replacement);
							break;
						} // if
					} // while
					
				} // if
				
			} //for j
			problem_.evaluate(solution);
			changeImpact += 
					Math.abs(
							(solution.getObjective(0)
									- prevPopulation.get(i).
									getObjective(0))
							/prevPopulation.get(i).
							getObjective(0));
			
			population.add(solution);
		} //for i
		
		
		
		changeImpact = changeImpact/populationSize;

		double lowerLimit = (Double)hm.get("propag_Li");
		double upperLimit = (Double)hm.get("propag_Ls");
		
		int numberOfNewSol = populationSize - (int) (populationSize*((upperLimit - lowerLimit)*changeImpact + lowerLimit));
		
		for (int j = populationSize-1; j >= populationSize - numberOfNewSol; j--) {
			try {
				population.add(j, new Solution(problem_));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		result.put("population", population);
		result.put("changeImpact", changeImpact);
		
		return result;
	}
	
	public boolean isInSolution(int index, Variable [] vars) throws JMException {
		for (int k = 0; k < vars.length; k++) {
			if(index == (int)vars[k].getValue() ) {
				return  true;
			}
		} // for k
		return false;
	}

}

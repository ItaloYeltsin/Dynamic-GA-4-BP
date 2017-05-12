package jmetal.operators.populationGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
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
		Problem problem_ = (Problem)hm.get("problem");
		PriorityQueue<Integer> indexes = (PriorityQueue<Integer>)hm.get("removedBugsIndexes");
		SolutionSet prevPopulation = (SolutionSet)hm.get("previousPopulation");
		if(prevPopulation == null)
			return (SolutionSet)spg.execute(problem_);
		
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
			for (int j = 0; j < newVars.length; j++) {
				suffledIndexes.add(j);
			}
			
			Collections.shuffle(suffledIndexes);

			int counter = 0;
			
			for (int j = 0; j < newVars.length; j++) { // replacement of removed bugs
				newVars[j] = oldVars[j].deepCopy();
				
				if(indexes.contains((int)newVars[j].getValue())) {
					
					while(true) {
						int replacement = suffledIndexes.get(counter++);
						if(replacement == (int)newVars[j].getValue())
							break;
						boolean isInSolution = false;
						for (int k = 0; k < newVars.length; k++) {
							if(replacement == (int)newVars[k].getValue() ) {
								isInSolution = true;
								break;
							}
						} // for k

						if(!isInSolution) {
							newVars[j].setValue(replacement);
							break;
						} // if
					} // while
					
				} // if
			} //for j
			problem_.evaluate(solution);
			changeImpact += (solution.getObjective(0) - prevPopulation.get(i).getObjective(0))/prevPopulation.get(i).getObjective(0);
			population.add(solution);
		} //for i
		
		changeImpact = changeImpact/populationSize;
		
		HashMap result = new HashMap();
		result.put("population", population);
		result.put("changeImpact", changeImpact);
		
		return result;
	}

}

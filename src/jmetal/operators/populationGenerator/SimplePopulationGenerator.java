package jmetal.operators.populationGenerator;

import java.util.HashMap;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;

public class SimplePopulationGenerator extends PopulationGenerator{

	int populationSize;
	
	public SimplePopulationGenerator(HashMap<String, Object> parameters) {
		super(parameters);
		populationSize = (Integer)parameters.get("populationSize");
	}

	@Override
	public Object execute(Object object) throws JMException {
		Problem problem_ = (Problem)object;
		SolutionSet population = new SolutionSet(populationSize);
		
		for (int i = 0; i < populationSize; i++) {
			try {
				population.add(new Solution(problem_));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			problem_.evaluate(population.get(i));
		}
		return population;
	}

}

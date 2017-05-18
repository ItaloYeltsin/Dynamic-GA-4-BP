package jmetal.metaheuristics.singleObjective.geneticAlgorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
import jmetal.operators.populationGenerator.PopGenWithSimplePropag;
import jmetal.operators.populationGenerator.SimplePopulationGenerator;
import jmetal.operators.selection.BestSolutionSelection;
import jmetal.problems.DynBugPrioritization;
import jmetal.util.JMException;
import jmetal.util.Neighborhood;
import jmetal.util.comparators.ObjectiveComparator;
import jmetal.util.dga4nrp.Bug;
import jmetal.util.dga4nrp.GaussianGenerator;

public class DynamicBPGA extends Algorithm {

	private PriorityQueue<Integer> removedBugsIndex;
	private ArrayList<Bug> newBugs;
	private ArrayList<Bug> bugsBeforeTheLastChange;
	private SolutionSet prevPopulation;
	
	public DynamicBPGA(Problem problem) {
		super(problem);
	}

	public void setProblem(Problem problem_) {
		super.problem_ = problem_;
	}

	@Override
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		int populationSize ;
	    int maxEvaluations ;
	    int evaluations    ;

	    SolutionSet population          ;
	    SolutionSet offspringPopulation ;

	    Operator    mutationOperator  ;
	    Operator    crossoverOperator ;
	    Operator    selectionOperator ;
	    Operator	popGenerator	  ;
	    
	    Comparator  comparator        ;
	    comparator = new ObjectiveComparator(0) ; // Single objective comparator
	    
	    
	    // Read the params
	    populationSize = ((Integer)this.getInputParameter("populationSize")).intValue();
	    long execTime = (long)this.getInputParameter("execTime");
	    double mutationUpperLimit = (Double)this.getInputParameter("mutation_Ls"); //Mutation Lower Limit
	    double mutationLowerLimit = (Double)this.getInputParameter("mutation_Li"); // Mutation Upper Limit
	    boolean isAGMA = (Boolean)this.getInputParameter("isAGMA");
	    double propagUpperLimit = (Double)this.getInputParameter("propag_Ls");
	    double propagLowerLimit = (Double)this.getInputParameter("propag_Li");
		   
	    double genDecay = 4;
	    double decayRate = 0.0002;
	    double changeImpact;
	    double mutationProbability = 0.05;
	    
	    
	    // Initialize the variables
	    population          = new SolutionSet(populationSize) ;   
	    offspringPopulation = new SolutionSet(populationSize) ;
	    evaluations  = 0;                

	    // Read the operators
	    mutationOperator  = this.operators_.get("mutation");
	    crossoverOperator = this.operators_.get("crossover");
	    selectionOperator = this.operators_.get("selection");  
	    popGenerator	  =	this.operators_.get("populationGenerator");
	    
	    // Create the initial population
	    Class popGenClass = popGenerator.getClass();
	    if(popGenClass == SimplePopulationGenerator.class) {
	    	population = (SolutionSet)popGenerator.execute(problem_);
	    }else if (popGenClass == PopGenWithSimplePropag.class) {
	    	HashMap parameters = new HashMap();
	    	parameters.put("removedBugsIndexes", getRemovedBugIndexes());
	    	parameters.put("problem", problem_);
	    	parameters.put("previousPopulation", prevPopulation);
	    	parameters.put("propag_Ls", propagUpperLimit);
	    	parameters.put("propag_Li", propagLowerLimit);
	    	
	    	HashMap result = (HashMap)popGenerator.execute(parameters);
	    	population = (SolutionSet)result.get("population");
	    	changeImpact = (Double)result.get("changeImpact");
	    	if(isAGMA)
	    		mutationUpperLimit = changeImpact*mutationUpperLimit;
	    	
	    }else {
	    	throw new RuntimeException("No compatible Population Generator found");
	    }
	    
	    GaussianGenerator mutationGG = 
	    		new GaussianGenerator(mutationUpperLimit, mutationLowerLimit, genDecay, decayRate);
	    
	    
	    // Sort population
	    population.sort(comparator) ;
	    long init = System.currentTimeMillis();
	    
	    double sumBestFitness = -population.get(0).getObjective(0);
	    int nOfGenerations = 0;
	    double firstBestFitness = -population.get(0).getObjective(0);
	    double tR = 0;
	    
	    while (System.currentTimeMillis()-init < execTime) {
	      nOfGenerations++;
	      // Copy the best two individuals to the offspring population
	      offspringPopulation.add(new Solution(population.get(0))) ;	
	      offspringPopulation.add(new Solution(population.get(1))) ;	
	        
	      // Reproductive cycle
	      for (int i = 0 ; i < populationSize-2 ; i ++) {
	        // Selection
	        Solution [] parents = new Solution[2];

	        parents[0] = (Solution)selectionOperator.execute(population);
	        parents[1] = (Solution)selectionOperator.execute(population);
	 
	        // Crossover
	        Solution offspring = (Solution) crossoverOperator.execute(parents);                
	          
	        // Mutation
	        mutationOperator.setParameter("probability", mutationGG.nextValue());
	        mutationOperator.execute(offspring);
	        // Evaluation of the new individual
	        problem_.evaluate(offspring);            
	          
	        evaluations +=1;
	    
	        // Replacement: the two new individuals are inserted in the offspring
	        //                population
	        offspringPopulation.add(offspring) ;
	      } // for
	      
	      // The offspring population becomes the new current population
	      population.clear();
	      for (int i = 0; i < populationSize; i++) {
	        population.add(offspringPopulation.get(i)) ;
	      }
	      offspringPopulation.clear();
	      population.sort(comparator) ;
	      sumBestFitness += -population.get(0).getObjective(0);
	      tR += -population.get(0).getObjective(0) - firstBestFitness;
	    } // while
	    
	    tR = tR/((-population.get(0).getObjective(0) - firstBestFitness)*nOfGenerations);
	    double average = sumBestFitness/nOfGenerations;
	    population.get(0).setObjective(1, average); // Stores Best Fitness Average of all the evolutive cycles
	    population.get(0).setObjective(2, tR);
	    // Return a population with the best individual
	    SolutionSet resultPopulation = new SolutionSet(1) ;
	    resultPopulation.add(population.get(0)) ;
	    prevPopulation = population; //Back-up population for next change
	    bugsBeforeTheLastChange = ((DynBugPrioritization)problem_).getBugs();
	    return resultPopulation ;
	} // execute

	
	private boolean bugIsInSet(Bug bug, List<Bug> list) {
		for (Bug bug1 : list) {
			if(bug1.equals(bug))
				return true;
		}
		return false;
	}
	
	private  PriorityQueue<Integer> getRemovedBugIndexes() {
		
		if(bugsBeforeTheLastChange == null)
			return null;
	
		ArrayList<Bug> currentBugs = ((DynBugPrioritization)problem_).getBugs();
		removedBugsIndex = new PriorityQueue<Integer>();

		for (int i = 0; i < bugsBeforeTheLastChange.size(); i++) {
			
			if(!bugIsInSet(bugsBeforeTheLastChange.get(i), currentBugs)) {
				removedBugsIndex.add(i);
			}
		}
		
		return removedBugsIndex;
	}
	
}

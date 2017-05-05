package jmetal.metaheuristics.singleObjective.geneticAlgorithm;

import java.util.Comparator;
import java.util.HashMap;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.operators.selection.BestSolutionSelection;
import jmetal.util.JMException;
import jmetal.util.Neighborhood;
import jmetal.util.comparators.ObjectiveComparator;

public class bpGA extends Algorithm {

	public bpGA(Problem problem) {
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
	    
	    Comparator  comparator        ;
	    comparator = new ObjectiveComparator(0) ; // Single objective comparator
	    
	    // Read the params
	    populationSize = ((Integer)this.getInputParameter("populationSize")).intValue();
	    maxEvaluations = ((Integer)this.getInputParameter("maxEvaluations")).intValue();                
	    long execTime = (long)this.getInputParameter("execTime");
	    // Initialize the variables
	    population          = new SolutionSet(populationSize) ;   
	    offspringPopulation = new SolutionSet(populationSize) ;
	    
	    evaluations  = 0;                

	    // Read the operators
	    mutationOperator  = this.operators_.get("mutation");
	    crossoverOperator = this.operators_.get("crossover");
	    selectionOperator = this.operators_.get("selection");  
	    
	    // Create the initial population
	    Solution newIndividual;
	    for (int i = 0; i < populationSize; i++) {
	      newIndividual = new Solution(problem_);                    
	      problem_.evaluate(newIndividual);            
	      evaluations++;
	      population.add(newIndividual);
	    } //for       
	     
	    // Sort population
	    population.sort(comparator) ;
	    long init = System.currentTimeMillis();
	    
	    double sumBestFitness = -population.get(0).getObjective(0);
	    int nOfGenerations = 1;
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
	    
	    tR = tR/(-population.get(0).getObjective(0) - firstBestFitness);
	    double average = sumBestFitness/nOfGenerations;
	    population.get(0).setObjective(1, average); // Stores Best Fitness Average of all the evolutive cycles
	    population.get(0).setObjective(2, tR);
	    // Return a population with the best individual
	    SolutionSet resultPopulation = new SolutionSet(1) ;
	    resultPopulation.add(population.get(0)) ;
	   
	    
	    return resultPopulation ;
	} // execute

}

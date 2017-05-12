//  SwapMutation.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.operators.mutation;

import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.PermutationSolutionType;
import jmetal.encodings.solutionType.RankPermutationSolutionType;
import jmetal.encodings.variable.Permutation;
import jmetal.problems.DynBugPrioritization;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class implements a swap mutation. The solution type of the solution
 * must be Permutation.
 */
public class RankSwapMutation extends Mutation{
  /**
   * Valid solution types to apply this operator 
   */
  private static final List VALID_TYPES = Arrays.asList(RankPermutationSolutionType.class) ;
  
  private Double mutationProbability_ = null ;

  /** 
   * Constructor
   */
  public RankSwapMutation(HashMap<String, Object> parameters) {    
  	super(parameters) ;
  	
  	if (parameters.get("probability") != null)
  		mutationProbability_ = (Double) parameters.get("probability") ;  		
  } // Constructor


  /**
   * Constructor
   */
  //public SwapMutation(Properties properties) {
  //  this();
  //} // Constructor

  /**
   * Performs the operation
   * @param probability Mutation probability
   * @param solution The solution to mutate
   * @throws JMException 
   */
  public void doMutation(double probability, Solution solution) throws JMException {   
    
	    if (solution.getType().getClass() == RankPermutationSolutionType.class) {
	      
	    	DynBugPrioritization problem = (DynBugPrioritization)solution.getProblem();
	    	ArrayList<Integer> bugPosition = new ArrayList<Integer>((int)problem.getUpperLimit(0));
	    	
	    	Variable [] vars = solution.getDecisionVariables();
	    	
	      if (PseudoRandom.randDouble() < probability) {
	    	  int pos1 = PseudoRandom.randInt(0, vars.length-1);
	    	  int bug1 = (int)vars[pos1].getValue(); // Randomly pick a bug inside solution
	    	  int bug2 = PseudoRandom.randInt(0, vars.length); // A bug in or out of the solution
	        while (bug1 == bug2) {
	        	bug2 = PseudoRandom.randInt(0, (int)problem.getUpperLimit(0));
	        } // while
	        // swap
	        boolean bug2IsInSolution = false;
	        for (int i = 0; i < vars.length; i++) {
				if(bug2 == (int)vars[i].getValue()) {
					bug2IsInSolution = true;
					//sawp
					vars[i].setValue(bug1);
					vars[pos1].setValue(bug2);
				}
			}
	        
	        if(!bug2IsInSolution) {
	        	vars[pos1].setValue(bug2);
	        }
	      } // if
	    } // if
	    else  {
	      Configuration.logger_.severe("RankSwapMutation.doMutation: invalid type. " +
	          ""+ solution.getDecisionVariables()[0].getVariableType());

	      Class cls = java.lang.String.class;
	      String name = cls.getName(); 
	      throw new JMException("Exception in " + name + ".doMutation()") ;
	    }
  } // doMutation

  /**
   * Executes the operation
   * @param object An object containing the solution to mutate
   * @return an object containing the mutated solution
   * @throws JMException 
   */
  public Object execute(Object object) throws JMException {
    Solution solution = (Solution)object;
    
		if (!VALID_TYPES.contains(solution.getType().getClass())) {
			Configuration.logger_.severe("SwapMutation.execute: the solution " +
					"is not of the right type. The type should be 'Binary', " +
					"'BinaryReal' or 'Int', but " + solution.getType() + " is obtained");

			Class cls = java.lang.String.class;
			String name = cls.getName();
			throw new JMException("Exception in " + name + ".execute()");
		} // if 

	mutationProbability_ = (Double) parameters_.get("probability") ;  	
    this.doMutation(mutationProbability_, solution);
    return solution;
  } // execute  
} // SwapMutation

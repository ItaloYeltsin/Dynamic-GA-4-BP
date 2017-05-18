//  SinglePointCrossover.java
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

package jmetal.operators.crossover;

import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.encodings.solutionType.PermutationSolutionType;
import jmetal.encodings.solutionType.RankPermutationSolutionType;
import jmetal.encodings.variable.Binary;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This Class is a JMetal implementation of Order 1 Crossover
 * 
 * @author Italo Yeltsin
 * @since 24/04/2017
 */
public class OrderOneCrossover extends Crossover {
  /**
   * Valid solution types to apply this operator 
   */
  private static final List VALID_TYPES = Arrays.asList(RankPermutationSolutionType.class, PermutationSolutionType.class) ;

  private Double crossoverProbability_ = null;

  /**
   * Constructor
   * Creates a new instance of the single point crossover operator
   */
  public OrderOneCrossover(HashMap<String, Object> parameters) {
  	super(parameters) ;
  	if (parameters.get("probability") != null)
  		crossoverProbability_ = (Double) parameters.get("probability") ;  		
  } // SinglePointCrossover


  /**
   * Constructor
   * Creates a new instance of the single point crossover operator
   */
  //public SinglePointCrossover(Properties properties) {
  //    this();
  //} // SinglePointCrossover

  /**
   * Perform the crossover operation.
   * @param probability Crossover probability
   * @param parent1 The first parent
   * @param parent2 The second parent   
   * @return An array containig the two offsprings
   * @throws JMException
   */
  public Solution[] doCrossover(double probability,
          Solution parent1,
          Solution parent2) throws JMException {
    Solution [] offSpring = new Solution[2];
    offSpring[0] = new Solution(parent1);

      if (PseudoRandom.randDouble() < probability) {  
    	  Variable [] vars = offSpring[0].getDecisionVariables();
    	  int interval1 = PseudoRandom.randInt(0, vars.length-1);
    	  int interval2 = PseudoRandom.randInt(interval1, vars.length-1);
    	  
    	  ArrayList<Double> swathe = new ArrayList<Double>();
    	  
    	  for (int i = interval1; i <= interval2; i++) {
    		  swathe.add(offSpring[0].getDecisionVariables()[i].getValue());
    	  }
    	  int position = 0;
    	  for (int i = 0; i < vars.length && position < vars.length; i++) {
			if(position == interval1){
				position = interval2+1;
			}
			boolean isMarked = false;
			for (int j = 0; j < swathe.size(); j++) {
				if(parent2.getDecisionVariables()[i].getValue() == swathe.get(j)) {
					isMarked = true;
					break;
				}
			}
			
			if(isMarked) {
				continue;
			} else { // 
				if(position < vars.length)
					offSpring[0].getDecisionVariables()[position] = parent2.getDecisionVariables()[i].deepCopy();
				position++;
			}
		}
      }
      
      return offSpring;
  } // doCrossover

  /**
   * Executes the operation
   * @param object An object containing an array of two solutions
   * @return An object containing one offSpring
   * @throws JMException
   */
  public Object execute(Object object) throws JMException {
    Solution[] parents = (Solution[]) object;

    if (!(VALID_TYPES.contains(parents[0].getType().getClass())  &&
        VALID_TYPES.contains(parents[1].getType().getClass())) ) {

      Configuration.logger_.severe("OrderOneCrossover.execute: the solutions " +
              "are not of the right type. The type should be 'Int', but " +
              parents[0].getType() + " and " +
              parents[1].getType() + " are obtained");

      Class cls = java.lang.String.class;
      String name = cls.getName();
      throw new JMException("Exception in " + name + ".execute()");
    } // if

    if (parents.length < 2) {
      Configuration.logger_.severe("OrderOneCrossover.execute: operator " +
              "needs two parents");
      Class cls = java.lang.String.class;
      String name = cls.getName();
      throw new JMException("Exception in " + name + ".execute()");
    } 
    
    Solution[] offSpring;
    offSpring = doCrossover(crossoverProbability_,
            parents[0],
            parents[1]);

    
    return offSpring[0];
  } // execute
  
} // OrderOneCrossover

//  GA_main.java
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

package jmetal.metaheuristics.singleObjective.geneticAlgorithm;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.populationGenerator.PopGenWithSimplePropag;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.DynBugPrioritization;
import jmetal.problems.singleObjective.OneMax;
import jmetal.util.JMException;
import jmetal.util.dga4nrp.Instance;
import jmetal.util.dga4nrp.InstanceReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class runs a single-objective genetic algorithm (GA). The GA can be 
 * a steady-state GA (class ssGA), a generational GA (class gGA), a synchronous
 * cGA (class scGA) or an asynchronous cGA (class acGA). The OneMax
 * problem is used to test the algorithms.
 */
public class GA_main {

	private final static long EXEC_TIME = 5000; //ms
	private final static int MMF = 1;
	private final static int TR = 2;
	
  public static void main(String [] args) throws JMException, ClassNotFoundException, IOException {
    Problem   problem   ;         // The problem to solve
    Algorithm algorithm ;         // The algorithm to use
    Operator  crossover ;         // Crossover operator
    Operator  mutation  ;         // Mutation operator
    Operator  selection ;         // Selection operator
            
    //int bits ; // Length of bit string in the OneMax problem
    HashMap  parameters ; // Operator parameters

    InstanceReader ir = new InstanceReader("kate.csv");
	Instance icc = ir.load();
    problem = new DynBugPrioritization(icc, 20);

    
    algorithm = new bpGA(problem) ; // GA
   
    /* Algorithm parameters*/
    algorithm.setInputParameter("populationSize",100);
    algorithm.setInputParameter("maxEvaluations", 25000000);
    algorithm.setInputParameter("execTime", EXEC_TIME);
    
    
    // Mutation and Crossover for Binary codification 
    
    parameters = new HashMap() ;
    parameters.put("probability", 0.9) ;
    crossover = CrossoverFactory.getCrossoverOperator("OrderOneCrossover", parameters);                   

    parameters = new HashMap() ;
    parameters.put("probability", 0.1) ;
    mutation = MutationFactory.getMutationOperator("RankSwapMutation", parameters);                    
    
    /* Selection Operator */
    parameters = null ;
   
    selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters) ;                            
    
    
    
    /* Add the operators to the algorithm*/
    algorithm.addOperator("crossover",crossover);
    algorithm.addOperator("mutation",mutation);
    algorithm.addOperator("selection",selection);
    parameters = new HashMap();
    parameters.put("populationSize", 100);
    algorithm.addOperator("populationGenerator", new PopGenWithSimplePropag(parameters));
    /* Execute the Algorithm */

    String [] fileNames = {
    		"kate-1.csv", "kate-2.csv", "kate-3.csv",
    		"kate-4.csv", "kate-5.csv", "kate-6.csv",
    		"kate-7.csv", "kate-8.csv",
    		"kate-9.csv", "kate-10.csv"};
    
    
    
    String [] changeLevel = {"low", "medium", "high"};
    
    ArrayList<ArrayList<Instance>> instances = new ArrayList<ArrayList<Instance>>(3);
    
    //Load All instances
    for (int i = 0; i < changeLevel.length; i++) {
    	ArrayList<Instance> aux = new ArrayList<Instance>();
    	instances.add(aux);
    	for (int j = 0; j < fileNames.length; j++) {
    		aux.add(new InstanceReader(changeLevel[i]+File.separator+fileNames[j]).load());
    	}
	}
    
    int evaluations = 30;
    int rankSize = 20;
    
    //CANONIC GA
    	for (int i = 0; i < instances.size(); i++) { //
    		ArrayList<Instance> aux = instances.get(i);
    		File f = new File("results"+File.separator+"static_ga"+File.separator+changeLevel[i]+".csv");
    		FileWriter fw = new FileWriter(f);
    		fw.write("MMF;TR"+System.lineSeparator());
			
    		for (int j = 0; j < evaluations; j++) { // Evaluations
				double mMF = 0;
				double tR = 0;
				double counter = 1;
				for (int k = 0; k < aux.size(); k++) { // changes
					Instance instance = aux.get(k);
					((bpGA)algorithm).setProblem(new DynBugPrioritization(instance, rankSize));
					SolutionSet s = algorithm.execute();
					
					mMF += s.get(0).getObjective(MMF);
					tR += s.get(0).getObjective(TR);
					
					counter++;
				}
				
				tR = tR/counter;
				mMF = mMF/counter;
				System.out.println(i+":"+j+":"+tR+" "+mMF);
				fw.write(mMF+";"+tR+System.lineSeparator());
			}
    		fw.close();
		}
  } //main
} // GA_main

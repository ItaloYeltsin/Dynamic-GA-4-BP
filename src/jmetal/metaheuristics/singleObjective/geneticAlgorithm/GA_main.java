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
	private final static int POPULATION_SIZE = 100;
	
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

    
    
    String [] fileNames = {
    		"kate-1.csv", "kate-2.csv", "kate-3.csv",
    		"kate-4.csv", "kate-5.csv"/*, "kate-6.csv",
    		"kate-7.csv", "kate-8.csv",
    		"kate-9.csv", "kate-10.csv"*/};
    
    
    
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
    
    int evaluations = 1;
    int rankSize = 20;
    
    // GAHP Parameters
    HashMap gAHParams = new HashMap();
    double [] lowerMutLimitRates = {0.01, 0.01, 0.05, 0.1, 0.1};
    gAHParams.put("mutation_Li", lowerMutLimitRates); // Mutation Li rates
    double [] upperMutLimitRates = {0.05, 0.1, 0.1, 0.3, 0.5};
    gAHParams.put("mutation_Ls", upperMutLimitRates);
    double [] lowerPropagLimitRates = {1.0};
    double [] upperPropagLimitRates = {1.0};
    String name = "GAHP";
    gAHParams.put("propag_Li", lowerPropagLimitRates);
    gAHParams.put("propag_Ls", upperPropagLimitRates);
    gAHParams.put("isAGMA", false);
    gAHParams.put("name", name);
    
    // AGMA Parameters
    HashMap aGMAParams = new HashMap();
    double [] lowerMutLimitRates2 = {0.01, 0.01, 0.05, 0.1, 0.1};
    aGMAParams.put("mutation_Li", lowerMutLimitRates2); // Mutation Li rates
    double [] upperMutLimitRates2 = {0.05, 0.1, 0.1, 0.3, 0.5};
    aGMAParams.put("mutation_Ls", upperMutLimitRates2);
    double [] lowerPropagLimitRates2 = {1.0};
    double [] upperPropagLimitRates2 = {1.0};
    name = "AGMA";
    aGMAParams.put("propag_Li", lowerPropagLimitRates2);
    aGMAParams.put("propag_Ls", upperPropagLimitRates2);
    aGMAParams.put("isAGMA", true);
    aGMAParams.put("name", name);
    
    // AGPS Parameters
    HashMap aGPSParams = new HashMap();
    double [] lowerMutLimitRates3 = {0.05};
    aGPSParams.put("mutation_Li", lowerMutLimitRates3); // Mutation Li rates
    double [] upperMutLimitRates3 = {0.05};
    aGPSParams.put("mutation_Ls", upperMutLimitRates3);
    double [] lowerPropagLimitRates3 = {0.1, 0.3, 0.6, 0.9};
    double [] upperPropagLimitRates3 = {0.1, 0.3, 0.6, 0.9};
    name = "AGPS";
    aGPSParams.put("propag_Li", lowerPropagLimitRates3);
    aGPSParams.put("propag_Ls", upperPropagLimitRates3);
    aGPSParams.put("isAGMA", false);
    aGPSParams.put("name", name);
    
    // AGPA Parameters
    HashMap aGPAParams = new HashMap();
    double [] lowerMutLimitRates4 = {0.05};
    aGPAParams.put("mutation_Li", lowerMutLimitRates4); // Mutation Li rates
    double [] upperMutLimitRates4 = {0.05};
    aGPAParams.put("mutation_Ls", upperMutLimitRates4);
    double [] lowerPropagLimitRates4 = {0.1, 0.1, 0.1};
    double [] upperPropagLimitRates4 = {0.3, 0.9, 1.0};
    name = "AGPA";
    aGPAParams.put("propag_Li", lowerPropagLimitRates4);
    aGPAParams.put("propag_Ls", upperPropagLimitRates4);
    aGPAParams.put("isAGMA", false);
    aGPAParams.put("name", name);
    
    // Static AG Parameters
    HashMap staticGAParams = new HashMap();
    double [] lowerMutLimitRates5 = {0.05};
    staticGAParams.put("mutation_Li", lowerMutLimitRates5); // Mutation Li rates
    double [] upperMutLimitRates5 = {0.05};
    staticGAParams.put("mutation_Ls", upperMutLimitRates5);
    double [] lowerPropagLimitRates5 = {0.0};
    double [] upperPropagLimitRates5 = {0.0};
    name = "static_ga";
    staticGAParams.put("propag_Li", lowerPropagLimitRates5);
    staticGAParams.put("propag_Ls", upperPropagLimitRates5);
    staticGAParams.put("isAGMA", false);
    staticGAParams.put("name", name);
    
    ArrayList<HashMap> algs = new ArrayList<HashMap>();
    
    algs.add(staticGAParams);
    algs.add(gAHParams);
    algs.add(aGPAParams);
    algs.add(aGMAParams);
    algs.add(aGPSParams);
    //CANONIC GA
    	for (HashMap params : algs) {
    		double [] mutation_Li = (double [])params.get("mutation_Li");
    		double [] mutation_Ls = (double [])params.get("mutation_Ls");
    		double [] propag_Li = (double [])params.get("propag_Li");
    		double [] propag_Ls = (double [])params.get("propag_Ls");
    		boolean isAGMA = (boolean)params.get("isAGMA");
    		String name1 = (String)params.get("name");
    		
     		for (int l = 0; l < propag_Ls.length; l++) {
				for (int m = 0; m < mutation_Ls.length; m++) {
					for (int i = 0; i < instances.size(); i++) { //
		        		ArrayList<Instance> aux = instances.get(i);
		        		
		        		File dir = new File("results"+File.separator+name1);
		        		dir.mkdirs();
		        		File f = new File("results"+File.separator+name1
		        				+File.separator+changeLevel[i]+
		        				"_"+propag_Li[l]+"-"+propag_Ls[l]+
		        				"_"+mutation_Li[m]+"-"+mutation_Ls[m]+".csv");
		        		FileWriter fw = new FileWriter(f);
		        		fw.write("MMF;TR"+System.lineSeparator());
		    			
		        		for (int j = 0; j < evaluations; j++) { // Evaluations
		    				double mMF = 0;
		    				double tR = 0;
		    				double counter = 0;
		    				algorithm = configGA(POPULATION_SIZE, mutation_Li[m],
		    						mutation_Ls[m], propag_Li[l], propag_Ls[l], isAGMA,
		    						new DynBugPrioritization(aux.get(0), rankSize));
		    				
		    				for (int k = 0; k < aux.size(); k++) { // changes
		    					Instance instance = aux.get(k);
		    					((DynamicBPGA)algorithm).setProblem(new DynBugPrioritization(instance, rankSize));
		    					SolutionSet s = algorithm.execute();
		    					
		    					mMF += s.get(0).getObjective(MMF);
		    					tR += s.get(0).getObjective(TR);
		    					
		    					counter++;
		    				}
		    				
		    				tR = tR/counter;
		    				mMF = mMF/counter;
		    				System.out.println(name1+" "+changeLevel[i]+" exec["+j+"]:"+" "+mMF+" "+tR);
		    				fw.write(mMF+";"+tR+System.lineSeparator());
		    			}
		        		fw.close();
		    		}
				}
			}
		}
  } //main
  
  public static Algorithm configGA(int popSize, double mutationLowerLimit, double mutationUpperLimit,
		  double propagLowerLimit, double propagUpperLimit, boolean isAGMA, Problem problem) throws JMException {
	  	
	  	Algorithm algorithm = new DynamicBPGA(problem) ; // GA
	    Operator  crossover ;         // Crossover operator
	    Operator  mutation  ;         // Mutation operator
	    Operator  selection ;   
	    
	    /* Algorithm parameters*/
	    algorithm.setInputParameter("populationSize", popSize);
	    algorithm.setInputParameter("execTime", EXEC_TIME);
	    algorithm.setInputParameter("mutation_Ls", mutationUpperLimit);
	    algorithm.setInputParameter("mutation_Li", mutationLowerLimit);
	    algorithm.setInputParameter("propag_Ls", propagUpperLimit);
	    algorithm.setInputParameter("propag_Li", propagLowerLimit);
	    algorithm.setInputParameter("isAGMA", isAGMA);	    // Mutation and Crossover for Binary codification 
	    
	    HashMap parameters = new HashMap() ;
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
	    parameters.put("populationSize", popSize);
	    algorithm.addOperator("populationGenerator", new PopGenWithSimplePropag(parameters));

	    return algorithm;
  }
  
} // GA_main

package jmetal.problems;

import java.util.ArrayList;
import java.util.HashMap;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.util.JMException;
import jmetal.util.dga4nrp.Bug;
import jmetal.util.dga4nrp.Instance;

public class DynBugPrioritization extends Problem {

	private ArrayList<Bug> bugs;
	private HashMap<Bug, ArrayList<Integer>> precedencies;
	private int rankSize;
	
	private final int ALPHA = 1, BETA = 1, GAMMA = 1; 
	
	DynBugPrioritization(Instance instance, int rankSize) {
		bugs = instance.getBugs();
		precedencies = instance.getPrecedences();
		this.rankSize = rankSize;
	}
	
	@Override
	public void evaluate(Solution solution) throws JMException {
		Variable [] vars = solution.getDecisionVariables();
		
		double importance = 0;
		double severity = 0;
		double relevance = 0;
		
		for (int i = 0; i < vars.length; i++) {
			int pos = i+1;
			Bug bug = bugs.get((int)vars[i].getValue());
			
			relevance += bug.getVotes()*(pos);
			importance += bug.getPriority()*(rankSize - pos+1); 
			severity += bug.getSerity()*pos;
		}
		
		double fitness = ALPHA*relevance + BETA*importance - GAMMA*severity;
		
		solution.setObjective(0, fitness/evaluatePrecedences(solution));
		
		
	}
	
	private int evaluatePrecedences(Solution solution) throws JMException {
		Variable [] vars = solution.getDecisionVariables();
		int nOfBrokenPrecs = 0;
		for (int i = 0; i < vars.length; i++) {
			Bug bug = bugs.get((int)vars[i].getValue());
			ArrayList<Integer> precs = (ArrayList<Integer>)precedencies.get(bug);
			if (precs != null) {
				for (Integer id : precs) {
					
					boolean precFound = false;
					
					for (int j = 0; j < vars.length; j++) {
						Bug bug2 = bugs.get((int)vars[j].getValue());
						if(bug2.getId() == id) {
							if(i < j) { // Bug i has to be in a position greater than Bug j
								nOfBrokenPrecs++;
							}
							precFound = true;
							break;
						}
						
					}
					
					if(!precFound) { // Precedence wasnt found.
						nOfBrokenPrecs++;
					}
				}
			}
		}
		
		return nOfBrokenPrecs;
	}
}

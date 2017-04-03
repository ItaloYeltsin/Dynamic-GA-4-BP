package jmetal.util.dga4nrp;

import java.util.ArrayList;
import java.util.Map;

public class Instance {	
	
	ArrayList<Bug> bugs;
	Map<Bug, ArrayList<Integer>> precedences; //maps precedences of each bug

	public Instance(ArrayList<Bug> bugs, Map<Bug, ArrayList<Integer>> precedences) {
		this.bugs = bugs;
		this.precedences = precedences;
	}
	
	public ArrayList<Bug> getBugs() {
		return bugs;
	}

	public Map<Bug, ArrayList<Integer>> getPrecedences() {
		return precedences;
	}

	public void setPrecedences(Map<Bug, ArrayList<Integer>> precedences) {
		this.precedences = precedences;
	}

	public void setBugs(ArrayList<Bug> bugs) {
		this.bugs = bugs;
	}
	
	
	
}

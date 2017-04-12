package jmetal.util.dga4nrp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Instance {	
	
	ArrayList<Bug> bugs;
	HashMap<Bug, ArrayList<Integer>> precedences; //maps precedences of each bug

	public Instance(ArrayList<Bug> bugs, HashMap<Bug, ArrayList<Integer>> precedences) {
		this.bugs = bugs;
		this.precedences = precedences;
	}
	
	public ArrayList<Bug> getBugs() {
		return bugs;
	}

	public HashMap<Bug, ArrayList<Integer>> getPrecedences() {
		return precedences;
	}

	public void setPrecedences(HashMap<Bug, ArrayList<Integer>> precedences) {
		this.precedences = precedences;
	}

	public void setBugs(ArrayList<Bug> bugs) {
		this.bugs = bugs;
	}
	
	
	
}

package jmetal.util.dga4nrp;

import java.util.ArrayList;
import java.util.Map;

public class Instance {	
	
	ArrayList<Bug> bugs;
	Map<Bug, ArrayList<Bug>> precedences; //maps precedences of each bug
}

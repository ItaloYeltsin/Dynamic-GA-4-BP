package jmetal.util.dga4nrp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InstanceMutator {
	private Instance seed;
	private Random random;
	
	private final int BUG_SEVERITY = 1;
	private final double [] BUG_SEVERITY_VALUES = {0.2, 0.4, 0.6, 0.8, 1.0};
	private final int BUG_VOTES = 2;
	private final int BUG_PRIORITY = 3;
	private final double [] BUG_PRIORITY_VALUES = {0.1, 0.25, 0.4, 0.55, 0.70, 0.85, 1.0};
	private final int NUMBER_OF_ATTRIBUTS = 3;
	private final int VARIATION_SIZE = 50;
	public InstanceMutator(Instance seed) {
		this.seed = seed;
		random = new Random();
	}

	public void mutate(double rate, int nOfInstances, String path) {
		if(!(rate >= 0 && rate <= 1)) {
			throw new IllegalArgumentException("rate must be greater than 0 and smaller than 1.");
		}
		
		ArrayList<Bug> bugs = seed.getBugs();
		HashMap prec = seed.getPrecedences();
		ArrayList<Bug> added = (ArrayList<Bug>) bugs.clone();
		ArrayList<Bug> removed = new ArrayList<Bug>();
		
		for (int i = 1; i <= nOfInstances; i++) {
			HashMap hm = copyMap(prec);
			execDimenChange(added, removed, hm, (int)(rate*seed.getBugs().size()/2));
			execNonDimensionalChange(added, removed, (int)(rate*seed.getBugs().size()/2));
			new InstanceWriter(path.replace(".csv", "-"+i+".csv")).write(new Instance(added, hm));
		}
		
	}
	
	private void execDimenChange(ArrayList<Bug> added, ArrayList<Bug> removed, HashMap hm, int size){
		
		if(removed.size() == 0) {
			Collections.shuffle(added);
			for (int i = 0; i < size; i++) {
				int index = added.size()-1;
				removed.add(added.get(index));
				added.remove(index);
			}
			
		} 
		else if(added.size() == 0) {
			Collections.shuffle(removed);
			for (int i = 0; i < size; i++) {
				int index = removed.size()-1;
				added.add(removed.get(index));
				removed.remove(index);
			}
		}
		else {
			
			int nOfInsertions;
			int nOfWithDrawals;
			
			do {
				if(removed.size() > 1) {
					nOfInsertions = random.nextInt(removed.size());
				} else {
					nOfInsertions = 1;
				}
				nOfWithDrawals = size - nOfInsertions;				
			}while(nOfInsertions > size || nOfInsertions > removed.size() || nOfWithDrawals >= added.size());
		
			Collections.shuffle(removed);
			Collections.shuffle(added);
			for (int i = 0; i < nOfInsertions && i < removed.size(); i++) {
				System.out.println(i);
				added.add(removed.get(i));
				removed.remove(i);
			}
			
			for (int i = 0; i < nOfWithDrawals && i < added.size(); i++) {
				removed.add(added.get(i));
				added.remove(i);
			}		
			
		}
		
		for (Object o : hm.keySet()) {
			Bug b = (Bug)o;
			boolean isIn = false;
			for (Bug bug : added) {
				if(bug.id == b.id)
					isIn = true;
			}
			if(!isIn) {
				hm.remove(b);
			}
		}
		
	}
	
	private HashMap copyMap(HashMap hm){
		HashMap copy = new HashMap();
		for (Object o : hm.keySet()) {
			copy.put(o, hm.get(o));
		}
		return copy;
	}
	
	private void execNonDimensionalChange(ArrayList<Bug> added, ArrayList<Bug> removed, int size) {
		
		Collections.shuffle(added);
		int attr = random.nextInt(NUMBER_OF_ATTRIBUTS);
		
		for (int i = 0; i < size && i< added.size(); i++) {
			Bug bug = added.get(i);
			
			if (attr == BUG_PRIORITY) {
				int index = random.nextInt(BUG_PRIORITY_VALUES.length);
				bug.setPriority(BUG_PRIORITY_VALUES[index]);
			} else if (attr == BUG_SEVERITY) {
				int index = random.nextInt(BUG_SEVERITY_VALUES.length);
				bug.setSerity(BUG_SEVERITY_VALUES[index]);
			} else if (attr == BUG_VOTES) {
				bug.setVotes(bug.getVotes()+ random.nextInt(VARIATION_SIZE)+1);
			} else {
				
			}
		}
	}
	
}

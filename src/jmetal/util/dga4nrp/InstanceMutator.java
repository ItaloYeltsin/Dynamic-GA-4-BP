package jmetal.util.dga4nrp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InstanceMutator {
	private Instance seed;
	private Random random;
	public InstanceMutator(Instance seed) {
		this.seed = seed;
		random = new Random();
	}

	public Instance[] mutate(double rate, int nOfInstances) {
		if(!(rate >= 0 && rate <= 1)) {
			throw new IllegalArgumentException("rate must be greater than 0 and smaller than 1.");
		}
		
		ArrayList<Bug> bugs = seed.getBugs();
		Map prec = seed.getPrecedences();
		ArrayList<Bug> added = (ArrayList<Bug>) bugs.clone();
		ArrayList<Bug> removed = new ArrayList<Bug>();
		boolean [] isIn = new boolean[bugs.size()];
		
		Instance [] mutants = new Instance[nOfInstances];
		
		for (int i = 0; i < mutants.length; i++) {
			
		}
		
		return mutants;
		
	}
	
	private void execDimenChange(ArrayList<Bug> added, ArrayList<Bug> removed, int size){
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
				nOfInsertions = random.nextInt(removed.size()+1);
				nOfWithDrawals = size - nOfInsertions;				
			}while(nOfInsertions > size && nOfWithDrawals <= added.size());
		
			Collections.shuffle(removed);
			Collections.shuffle(added);
			for (int i = 0; i < nOfInsertions; i++) {
				added.add(removed.get(i));
				removed.remove(i);
			}
			
			for (int i = 0; i < nOfWithDrawals; i++) {
				removed.add(added.get(i));
				added.remove(i);
			}
		}
	}
	
}

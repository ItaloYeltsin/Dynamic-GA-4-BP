package jmetal.util.dga4nrp;

import java.io.File;

public class Teste {
	public static void main(String [] args) {
		InstanceReader ir = new InstanceReader("kate.csv");
		Instance icc = ir.load();
		InstanceMutator im = new InstanceMutator(icc);
		double [] changeLevel = {0.3, 0.6, 0.9};
		String [] changeLevelString = {"low", "medium", "high"};
		for (int i = 0; i < changeLevel.length; i++) {
			im.mutate(changeLevel[i], 10, changeLevelString[i]+File.separator+"kate.csv");
		}
		
	}
}

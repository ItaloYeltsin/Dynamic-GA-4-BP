package jmetal.util.dga4nrp;

public class Teste {
	public static void main(String [] args) {
		InstanceReader ir = new InstanceReader("kate.csv");
		Instance icc = ir.load();
		InstanceMutator im = new InstanceMutator(icc);
		im.mutate(0.3, 10, ir.getPath());
		
	}
}

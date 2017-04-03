package jmetal.util.dga4nrp;

public class Teste {
	public static void main(String [] args) {
		InstanceReader ir = new InstanceReader("kate.csv");
		Instance icc = ir.load();
		InstanceWriter iw = new InstanceWriter(ir.getPath().replace(".csv", "-copy.csv"));
		iw.write(icc);
	}
}

package jmetal.util.dga4nrp;

public class GaussianGenerator {
	
	private double init, end, genDecay, decayRate; 
	private int counter = 0;
	public GaussianGenerator(double init, double end, double genDecay, double decayRate) {
		this.init = init;
		this.end = end;
		this.genDecay = genDecay;
		this.decayRate = decayRate;
	}

	public double nextValue() {
		return gaussianFunction(counter++);
	}
	
	public double gaussianFunction(int generation) {
		double numerator = (Math.pow(Math.max(0, generation - genDecay), 2));
		return end + (init - end) * Math.exp(-numerator*decayRate/2);
	}
}

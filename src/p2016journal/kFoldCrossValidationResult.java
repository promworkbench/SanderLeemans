package p2016journal;

import org.processmining.plugins.InductiveMiner.Triple;

public class kFoldCrossValidationResult {
	private final int k;
	private final double[] fitness;
	private final double[] precision;
	private final double[] simplicity;

	public kFoldCrossValidationResult(int k) {
		this.k = k;
		fitness = new double[k];
		precision = new double[k];
		simplicity = new double[k];
		for (int i = 0; i < k; i++) {
			fitness[i] = -1;
			precision[i] = -1;
			simplicity[i] = -1;
		}
	}

	public void set(Triple<Double, Double, Double> fitnessPrecisionSimplicity, int bucketNr) {
		fitness[bucketNr] = fitnessPrecisionSimplicity.getA();
		precision[bucketNr] = fitnessPrecisionSimplicity.getB();
		simplicity[bucketNr] = fitnessPrecisionSimplicity.getC();
	}

	public int getK() {
		return k;
	}

	public double getFitness(int... x) {
		return get(fitness, x);
	}

	public double getPrecision(int... x) {
		return get(precision, x);
	}

	public double getSimplicity(int... x) {
		return get(simplicity, x);
	}

	private double get(double[] array, int... x) {
		if (x.length >= 1) {
			return array[x[0]];
		}
		double sum = 0;
		int count = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] >= -0.01) {
				sum += array[i];
				count++;
			}
		}
		if (count != 0) {
			return sum / count;
		} else {
			return -1;
		}
	}
}

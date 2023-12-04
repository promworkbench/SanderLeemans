package crc;

import java.util.Arrays;

import org.jfree.data.xy.XYSeries;
import org.math.plot.utils.Array;

public class Bins {

	public int[] binNumberOfValues;
	public double[] binMaxThreshold;

	public Bins(int numberOfBins, double maximumValue) {
		binNumberOfValues = new int[numberOfBins];
		binMaxThreshold = new double[numberOfBins];
		double max = 0;
		for (int i = 0; i < numberOfBins; i++) {
			max += maximumValue / (numberOfBins);
			binMaxThreshold[i] = max;
		}
	}

	public void add(double value) {
		int pos = Arrays.binarySearch(binMaxThreshold, value);
		if (pos >= 0) {
			binNumberOfValues[pos]++;
		} else {
			binNumberOfValues[~pos]++;
		}
	}

	public XYSeries toXYSeries(String title) {
		XYSeries result = new XYSeries(title);
		result.add(0, binNumberOfValues[0]);
		result.add(binMaxThreshold[0], binNumberOfValues[0]);
		for (int bin = 1; bin < binMaxThreshold.length; bin++) {
			result.add(binMaxThreshold[bin - 1], binNumberOfValues[bin]);
			result.add(binMaxThreshold[bin], binNumberOfValues[bin]);
		}
		return result;
	}

	public double getMaxNumberPerBin() {
		return Array.max(binNumberOfValues);
	}

}

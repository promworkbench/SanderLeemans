package crc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

public class DataOut {

	public double[] outCoarse;
	public double[] outMiddle;
	public double[] outFine;

	public double[] windowOutCoarse;
	public double[] windowOutMiddle;
	public double[] windowOutFine;

	public double averageOutCoarse;
	public double averageOutMiddle;
	public double averageOutFine;

	public DataOut(File file, ExperimentParameters parameters) {
		BufferedReader br = null;
		String line;
		String cvsSplitBy = ",";
		TDoubleList outCoarse2 = new TDoubleArrayList();
		TDoubleList outMiddle2 = new TDoubleArrayList();
		TDoubleList outFine2 = new TDoubleArrayList();

		TDoubleList windowOutCoarse2 = new TDoubleArrayList();
		TDoubleList windowOutMiddle2 = new TDoubleArrayList();
		TDoubleList windowOutFine2 = new TDoubleArrayList();

		averageOutCoarse = 0;
		averageOutMiddle = 0;
		averageOutFine = 0;

		try {
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] row = line.split(cvsSplitBy);

				try {
					double coarse = Double.parseDouble(row[parameters.getFieldOutCoarse()]);
					double middle = Double.parseDouble(row[parameters.getFieldOutMiddle()]);
					double fine = Double.parseDouble(row[parameters.getFieldOutFine()]);

					double sum = coarse + middle + fine;
					if (sum <= 0) {
						continue;
					}

					outCoarse2.add(coarse / sum);
					outMiddle2.add(middle / sum);
					outFine2.add(fine / sum);

					windowOutCoarse2.add(average(outCoarse2, parameters));
					windowOutMiddle2.add(average(outMiddle2, parameters));
					windowOutFine2.add(average(outFine2, parameters));

					averageOutCoarse += coarse / sum;
					averageOutMiddle += middle / sum;
					averageOutFine += fine / sum;

				} catch (NumberFormatException e) {
					continue;
				}
			}

			outCoarse = outCoarse2.toArray();
			outMiddle = outMiddle2.toArray();
			outFine = outFine2.toArray();
			windowOutCoarse = windowOutCoarse2.toArray();
			windowOutMiddle = windowOutMiddle2.toArray();
			windowOutFine = windowOutFine2.toArray();
			averageOutCoarse /= outCoarse.length;
			averageOutMiddle /= outMiddle.length;
			averageOutFine /= outFine.length;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private double average(TDoubleList list, ExperimentParameters parameters) {
		return list.subList(Math.max(0, list.size() - parameters.getWindowSize()), list.size()).sum()
				/ Math.min(list.size(), parameters.getWindowSize());
	}

}

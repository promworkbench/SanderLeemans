package svn27emscpartialordersbounds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;

public class Perform5Gather {

	private static final String n = "\n";
	private static final DecimalFormat format = new DecimalFormat("#.000");

	public static void main(String... args) throws Exception {

		ExperimentParameters parameters = new ExperimentParameters();

		//write body
		File[] files = parameters.getLogDirectory().listFiles();
		for (Iterator<File> itL = Arrays.asList(files).iterator(); itL.hasNext();) {
			File logFile = itL.next();

			System.out.println(logFile.getName());

			for (Iterator<Algorithm> itA = parameters.getAlgorithms().iterator(); itA.hasNext();) {
				Algorithm algorithm = itA.next();

				Call call = new Call(logFile.getName(), algorithm, parameters);

				if (call.isAttempted(call.getDiscoveredModelFile()) && !call.isError(call.getDiscoveredModelFile())) {

					for (Measure measure : parameters.getMeasures()) {
						for (int measureIndex = 0; measureIndex < measure.getNumberOfMeasures(); measureIndex++) {
							call.getOutputFile(measure, measureIndex).getParentFile().mkdirs();
							PrintWriter writer = new PrintWriter(call.getOutputFile(measure, measureIndex));

							for (int q : parameters.getQs()) {
								for (int r : parameters.getRs()) {
									if (!call.isMeasureDone(measure, q, r)) {
										System.out.println("missing " + measure.getClass() + " q=" + q + " r=" + r);
										writer.write(q + " " + r + " " + "0");
									} else {
										//read the value
										BufferedReader reader = new BufferedReader(
												new FileReader(call.getMeasureFile(measure, q, r)));
										String line = null;
										for (int i = 0; i <= measureIndex; i++) {
											line = reader.readLine();
										}
										double value = Double.valueOf(line.substring(0, line.indexOf(' ')));
										reader.close();

										writer.write(q + " " + r + " " + value);
									}
									writer.write(n);
								}
								writer.write(n);
							}
							
							System.out.println();

							writer.close();
						}
					}
				}
			}
		}
	}

	public static String format(int value) {
		return String.format("%,d", value).replace(",", "\\,");
	}

	public static String format(double value) {
		if (value >= 0.001) {
			return format.format(value);
		} else if (value < 0.00000000000000000000001) {
			return "0.000";
		} else {
			int exponent = getExponent(value);
			return "$\\smash{10^{" + exponent + "}}$";
		}
	}

	public static String formatTime(long value) {
		if (value < 10000) {
			return value + "";
		} else {
			int log = (int) Math.round(Math.log10(value));
			return "$\\smash{10^{" + log + "}}$";
		}
	}

	//from https://stackoverflow.com/questions/52993177/how-to-get-the-exponent-of-a-double-in-a-power-of-10-in-java
	public static int getExponent(double number) {
		String str = String.valueOf(number);
		int pointIndex = str.indexOf('.');
		if (pointIndex == -1 || pointIndex == str.length() - 1)
			return 0;

		while (str.endsWith("0")) {
			str = str.substring(0, str.length() - 1);
		}

		pointIndex = str.indexOf('.');
		if (pointIndex == str.length() - 1)
			return 0;

		String[] splitted = str.split("\\.");
		return -splitted[1].length();
	}
}
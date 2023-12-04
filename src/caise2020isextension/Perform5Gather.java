package caise2020isextension;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;

import org.processmining.framework.plugin.PluginContext;

import thesis.helperClasses.FakeContext;

public class Perform5Gather {

	private static final String s = "&";
	private static final String n = "\\\\\n";
	private static final DecimalFormat format = new DecimalFormat("#.000");

	private static final String logDist = "1.5mm";

	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getOutputfile().getParentFile().mkdirs();

		PrintWriter writer = new PrintWriter(parameters.getOutputfile());

		//write header
		writer.write("\\begin{tabular}{" + "ll");
		for (Measure measure : parameters.getMeasures()) {
			for (int i = 0; i < measure.getNumberOfMeasures() + (measure.printTime() ? 1 : 0); i++) {
				writer.write("r");
			}
		}
		writer.write("}\n");
		writer.write("\\toprule\n");
		writer.write("log" + s + "algorithm");
		for (Measure measure : parameters.getMeasures()) {
			writer.write(s + "\\multicolumn{" + (measure.getNumberOfMeasures() + (measure.printTime() ? 1 : 0))
					+ "}{l}{" + measure.getLatexTitle() + "}");
		}
		writer.write(n);
		{
			int x = 3;
			for (Measure measure : parameters.getMeasures()) {
				int c = measure.getNumberOfMeasures() + (measure.printTime() ? 1 : 0);
				writer.write("\\cmidrule(lr){" + x + "-" + (x + c - 1) + "}");
				x += c;
			}
		}

		writer.write("&");
		for (Measure measure : parameters.getMeasures()) {
			for (String name : measure.getMeasureLatexNames()) {
				writer.write(s + name);
			}
			if (measure.printTime()) {
				writer.write(s + "time");
			}
		}
		writer.write(n + "\\midrule\n");

		//write body
		File[] files = parameters.getLogDirectory().listFiles();
		for (Iterator<File> itL = Arrays.asList(files).iterator(); itL.hasNext();) {
			File logFile = itL.next();

			System.out.println(logFile.getName());

			for (Iterator<Algorithm> itA = parameters.getAlgorithms().iterator(); itA.hasNext();) {
				Algorithm algorithm = itA.next();
				writer.write(parameters.getLog2abbreviation().get(logFile.getName()) + s);
				writer.write(algorithm.getLatexName());

				Call call = new Call(logFile.getName(), algorithm, parameters);
				if (!call.isAttempted(call.getDiscoveredModelFile())) {
					writer.write(s + "\\multicolumn{4}{l}{waiting for model}");
				} else if (call.isAttempted(call.getDiscoveredModelFile())
						&& call.isError(call.getDiscoveredModelFile())) {
					writer.write(s + "\\multicolumn{4}{l}{error in discovery}");
				} else {
					//discovery done

					int measureIndex = 0;
					for (Measure measure : parameters.getMeasures()) {
						if (!call.isMeasureDone(measure)) {
							for (int i = 0; i < measure.getNumberOfMeasures() + (measure.printTime() ? 1 : 0); i++) {
								writer.write(s + "-");
							}
						} else if (call.isError(call.getMeasureFile(measure))) {
							for (int i = 0; i < measure.getNumberOfMeasures() + (measure.printTime() ? 1 : 0); i++) {
								writer.write(s + "!");
							}
						} else {
							//measure done
							{
								BufferedReader reader = new BufferedReader(
										new FileReader(call.getMeasureFile(measure)));
								for (int i = 0; i < measure.getNumberOfMeasures(); i++) {
									String line = reader.readLine();
									double value = Double.valueOf(line.substring(0, line.indexOf(' ')));

									//if (measureIndex > 0 && (measureIndex < 3 || i == 1)) {
									if (measure.printTime()) {
										writer.write(s + format(value));
									} else {
										writer.write(s + format((int) value));
									}
									//} else {
									//	writer.write(s);
									//}
								}
								reader.close();
							}

							//time
							if (measure.printTime()) {
								BufferedReader reader = new BufferedReader(
										new FileReader(call.getMeasureTimeFile(measure)));
								String line = reader.readLine();
								long value = Long.valueOf(line);
								writer.write(s + formatTime(value));
								reader.close();
							}
						}

						measureIndex++;
					}

				}

				if (itA.hasNext() || !itL.hasNext()) {
					writer.write(n);
				}
			}

			if (itL.hasNext()) {
				writer.write("\\\\[" + logDist + "]\n");
			}
		}

		writer.write("\\bottomrule\n");
		writer.write("\\end{tabular}");

		writer.close();
	}

	public static String format(int value) {
		return value + "";
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

	public static int getExponent(double number) {
		String str = String.valueOf(number);

		int indexofE = str.indexOf('E');

		String exponent = str.substring(indexofE + 1);

		return Integer.valueOf(exponent);

		//		int pointIndex = str.indexOf('.');
		//		if (pointIndex == -1 || pointIndex == str.length() - 1)
		//			return 0;
		//
		//		while (str.endsWith("0")) {
		//			str = str.substring(0, str.length() - 1);
		//		}
		//
		//		pointIndex = str.indexOf('.');
		//		if (pointIndex == str.length() - 1)
		//			return 0;
		//
		//		String[] splitted = str.split("\\.");
		//		return -splitted[1].length();
	}
}

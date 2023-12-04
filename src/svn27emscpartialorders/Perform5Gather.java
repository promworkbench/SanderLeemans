package svn27emscpartialorders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
	private static final String measureDist = "5mm";

	public static void main(String... args) throws Exception {
		writeLogMeasures();

		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getOutputfile().getParentFile().mkdirs();

		int measureColumns = 0;

		PrintWriter writer = new PrintWriter(parameters.getOutputfile());

		//write header
		writer.write("\\newcommand{\\STAB}[1]{\\begin{tabular}{@{}c@{}}#1\\end{tabular}}");
		writer.write("\\begin{tabular}{" + "ll");
		{
			Iterator<Measure> it = parameters.getMeasures().iterator();
			while (it.hasNext()) {
				Measure measure = it.next();
				for (int i = 0; i < measure.getNumberOfMeasures() + (measure.printTime() ? 1 : 0); i++) {
					writer.write("r");
					measureColumns++;
				}

				if (it.hasNext()) {
					writer.write("@{\\hspace{" + measureDist + "}}");
				}
			}
		}
		writer.write("}\n");
		writer.write("\\toprule\n");
		writer.write("\\multirow{3}{*}{\\STAB{\\rotatebox[origin=c]{90}{Log}}}" + s);
		writer.write("\\multirow{3}{*}{\\STAB{\\rotatebox[origin=c]{90}{alg.}}}");
		for (Measure measure : parameters.getMeasures()) {
			writer.write(s + "\\multicolumn{" + (measure.getNumberOfMeasures() + (measure.printTime() ? 1 : 0))
					+ "}{l}{" + measure.getLatexTitle() + "}");
		}
		writer.write(n);
		{
			int x = 3;
			Iterator<Measure> it = parameters.getMeasures().iterator();
			while (it.hasNext()) {
				Measure measure = it.next();
				int c = measure.getNumberOfMeasures() + (measure.printTime() ? 1 : 0);
				String d = it.hasNext() ? "(r{" + measureDist + "})" : "";
				writer.write("\\cmidrule" + d + "{" + x + "-" + (x + c - 1) + "}");
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

			int algorithmIndex = 0;
			for (Iterator<Algorithm> itA = parameters.getAlgorithms().iterator(); itA.hasNext();) {
				Algorithm algorithm = itA.next();

				if (algorithmIndex == 0) {
					String logS = parameters.getLog2abbreviation().get(logFile.getName());
					writer.write("\\multirow{" + parameters.getAlgorithms().size()
							+ "}{*}{\\STAB{\\rotatebox[origin=c]{90}{" + logS + "}}}");
				}

				writer.write(s);
				writer.write(algorithm.getLatexName());

				Call call = new Call(logFile.getName(), algorithm, parameters);
				if (!call.isAttempted(call.getDiscoveredModelFile())) {
					writer.write(s + "\\multicolumn{" + measureColumns + "}{l}{waiting for model}");
				} else if (call.isAttempted(call.getDiscoveredModelFile())
						&& call.isError(call.getDiscoveredModelFile())) {
					writer.write(s + "\\multicolumn{" + measureColumns + "}{l}{error in discovery}");
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
										writer.write(s + formatLong(value));
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

				algorithmIndex++;
			}

			if (itL.hasNext()) {
				writer.write("\\\\[" + logDist + "]\n");
			}
		}

		writer.write("\\bottomrule\n");
		writer.write("\\end{tabular}");

		writer.close();
	}

	public static String formatNoExponent(long value) {
		return String.format("%,d", value).replace(",", "\\,");
	}

	public static String formatLong(double value) {
		if (value >= 100000) {
			int log = (int) Math.round(Math.log10(value));
			return "$\\smash{10^{" + log + "}}$";
		}
		return String.format("%,d", (long) value).replace(",", "\\,");
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
		if (value < 100000) {
			return String.format("%,d", value).replace(",", "\\,");
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

	public static void writeLogMeasures() throws IOException {
		ExperimentParameters parameters = new ExperimentParameters();
		parameters.getLogOutputfile().getParentFile().mkdirs();

		PrintWriter writer = new PrintWriter(parameters.getLogOutputfile());

		//write header
		writer.write("\\begin{tabular}{lrrrr}");
		writer.write("\\toprule\n");
		writer.write("Log");
		writer.write(s + "traces");
		writer.write(s + "events");
		writer.write(s + "activities");
		writer.write(s + "consecutive events with the same timestamp");
		writer.write(n + "\\midrule\n");

		File[] files = parameters.getLogDirectory().listFiles();
		for (File logFile : files) {
			writer.write(parameters.getLog2abbreviation().get(logFile.getName()));

			Call call = new Call(logFile.getName(), null, parameters);

			File logMeasuresFile = call.getLogMeasuresFile();

			if (!call.isAttempted(logMeasuresFile)) {
				writer.write(s + "-");
				writer.write(s + "-");
				writer.write(s + "-");
				writer.write(s + "-");
			} else {
				BufferedReader reader = new BufferedReader(new FileReader(logMeasuresFile));

				{
					String line = reader.readLine();
					int traceCount = Integer.valueOf(line);
					writer.write(s + formatNoExponent(traceCount));
				}

				{
					String line = reader.readLine();
					int eventCount = Integer.valueOf(line);
					writer.write(s + formatNoExponent(eventCount));
				}
				{
					String line = reader.readLine();
					int activities = Integer.valueOf(line);
					writer.write(s + formatNoExponent(activities));
				}
				{
					String line = reader.readLine();
					int sameTimeStamp = Integer.valueOf(line);
					writer.write(s + "\\multicolumn{1}{r}{" + formatNoExponent(sameTimeStamp) + "}");
				}

				reader.close();

				writer.write(n);
			}
		}

		writer.write("\\bottomrule\n");
		writer.write("\\end{tabular}");

		writer.close();
	}
}

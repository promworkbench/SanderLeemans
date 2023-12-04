package svn45crimes;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

import org.processmining.plugins.InductiveMiner.MultiSet;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.Triple;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.hash.THashSet;

public class tkde2021crimes4Gather {
	public static void main(String... args) throws IOException, Exception {
		ExperimentParameters parameters = new ExperimentParameters();

		//find logs that are not complete
		Set<String> incompleteLogs = new THashSet<>();
		MultiSet<String> errorLogs = new MultiSet<>();
		{
			for (Call call : parameters.getCalls()) {
				for (Measure measure : parameters.getMeasures()) {
					if (Call.isAttempted(call.getMeasureFile(measure)) && Call.isError(call.getMeasureFile(measure))) {
						System.out.println(call.getMeasureFile(measure));
						//						call.getDiscoveredModelFile().delete();
						//call.getMeasureFile(measure).delete();
						errorLogs.add(call.getLogFile().getAbsolutePath());
					}
					//if (!Call.isAttempted(call.getMeasureFile(measure)) || Call.isError(call.getMeasureFile(measure))) {
					if (!Call.isAttempted(call.getMeasureFile(measure))) {
						incompleteLogs.add(call.getLogFile().getName());
					}
				}
			}

			System.out.println(incompleteLogs.size() + " incomplete logs " + incompleteLogs);
			System.out.println("in total " + errorLogs.setSize() + " error logs");
			System.out.println(errorLogs.size() + " errors " + errorLogs);
		}

		//initialise
		THashMap<Triple<Algorithm, Measure, Integer>, List<Pair<Double, Double>>> results = new THashMap<>();
		//read values
		{
			for (Algorithm algorithm : parameters.getAlgorithms()) {
				for (Measure measure : parameters.getMeasures()) {
					for (int meas = 0; meas < measure.getNumberOfMeasures(); meas++) {
						results.put(Triple.of(algorithm, measure, meas), new ArrayList<>());
					}
				}
			}
			for (Call call : parameters.getCalls()) {
				for (Measure measure : parameters.getMeasures()) {
					if (Call.isAttempted(call.getMeasureFile(measure)) && !Call.isError(call.getMeasureFile(measure))) {

						if (true || !incompleteLogs.contains(call.getLogFile().getName())) {
							BufferedReader reader = new BufferedReader(new FileReader(call.getMeasureFile(measure)));
							for (int meas = 0; meas < measure.getNumberOfMeasures(); meas++) {
								Triple<Algorithm, Measure, Integer> key = Triple.of(call.getAlgorithm(), measure, meas);

								String line = reader.readLine();
								double value = Double.valueOf(line.substring(0, line.indexOf(' ')));

								results.get(key).add(Pair.of(call.getNoiseAmount(), value));
								if (call.isReducedLog()) {
									results.get(key).add(Pair.of(call.getNoiseAmount(), value));
									results.get(key).add(Pair.of(call.getNoiseAmount(), value));
									results.get(key).add(Pair.of(call.getNoiseAmount(), value));
									results.get(key).add(Pair.of(call.getNoiseAmount(), value));
									results.get(key).add(Pair.of(call.getNoiseAmount(), value));
									results.get(key).add(Pair.of(call.getNoiseAmount(), value));
									results.get(key).add(Pair.of(call.getNoiseAmount(), value));
									results.get(key).add(Pair.of(call.getNoiseAmount(), value));
									results.get(key).add(Pair.of(call.getNoiseAmount(), value));
								}
							}
							reader.close();
						}
					}
				}
			}
		}

		//export
		{
			parameters.getResultsDirectory().mkdirs();
			for (Algorithm algorithm : parameters.getAlgorithms()) {
				for (Measure measure : parameters.getMeasures()) {
					for (int meas = 0; meas < measure.getNumberOfMeasures(); meas++) {
						List<Pair<Double, Double>> values = results.get(Triple.of(algorithm, measure, meas));

						File outputFile = new File(parameters.getResultsDirectory(),
								"result-" + algorithm.getAbbreviation() + "-" + measure.getTitle() + "-"
										+ measure.getMeasureNames()[meas] + ".txt");
						PrintWriter w = new PrintWriter(outputFile);

						for (Pair<Double, Double> p : values) {
							w.write(p.getA() + " " + p.getB() + "\n");
						}

						w.close();
					}
				}
			}
		}

		//figure export
		{
			parameters.getResultsDirectory().mkdirs();
			for (Algorithm algorithm : parameters.getAlgorithms()) {
				for (Measure measure : parameters.getMeasures()) {
					for (int meas = 0; meas < measure.getNumberOfMeasures(); meas++) {
						List<Pair<Double, Double>> values = results.get(Triple.of(algorithm, measure, meas));

						File outputFile = new File(parameters.getResultsDirectory(),
								"result-" + algorithm.getAbbreviation() + "-" + measure.getTitle() + "-"
										+ measure.getMeasureNames()[meas] + ".png");

						if (values.size() > 0) {
							//compute linear model
							Pair<BigDecimal, BigDecimal> model = SimpleLinearRegression.regress(values);

							createCorrelationPlot(measure.getMeasureNames()[meas], values, outputFile, model);
						}
					}
				}
			}
		}

		//models
		{
			for (Algorithm algorithm : parameters.getAlgorithms()) {
				for (Measure measure : parameters.getMeasures()) {
					for (int meas = 0; meas < measure.getNumberOfMeasures(); meas++) {
						List<Pair<Double, Double>> values = results.get(Triple.of(algorithm, measure, meas));
						Pair<BigDecimal, BigDecimal> model = SimpleLinearRegression.regress(values);

						File outputFile = new File(parameters.getResultsDirectory(),
								"result-" + algorithm.getAbbreviation() + "-" + measure.getTitle() + "-"
										+ measure.getMeasureNames()[meas] + "-model.txt");
						PrintWriter w = new PrintWriter(outputFile);
						if (model != null) {
							w.write("\\def\\graphA{" + model.getA().doubleValue() + "}\n");
							w.write("\\def\\graphB{" + model.getB().doubleValue() + "}\n");
						} else {
							w.write("\\def\\graphA{0}\n");
							w.write("\\def\\graphB{0}\n");
						}
						w.close();
					}
				}
			}
		}

		//test
		{
			test(results, parameters);
		}

		//print output
		{
			List<Call> calls = parameters.getCalls();

			{
				int noiseLogsErrors = 0;
				int noiseLogsDone = 0;
				for (Call call : calls) {
					if (Call.isAttempted(call.getNoiseLogFile())) {
						noiseLogsDone++;
						if (call.isError(call.getNoiseLogFile())) {
							noiseLogsErrors++;
						}
					}
				}

				System.out.println(
						"noise logs: done " + noiseLogsDone + ", errors " + noiseLogsErrors + ", of " + calls.size());
			}

			{
				int discoveryErrors = 0;
				int discoveryDone = 0;
				long discoveryTime = 0;
				for (Call call : calls) {
					if (Call.isAttempted(call.getDiscoveredModelFile())) {
						discoveryDone++;
						if (call.isError(call.getDiscoveredModelFile())) {
							discoveryErrors++;
						} else {
							BufferedReader reader = new BufferedReader(
									new FileReader(call.getDiscoveredModelTimeFile()));
							String line = reader.readLine();
							discoveryTime += Long.valueOf(line);
							reader.close();
						}
					}
				}

				System.out.println("discovery: done " + discoveryDone + ", errors " + discoveryErrors + ", of "
						+ calls.size() + ", time " + discoveryTime);
			}

			{
				int measuresDone = 0;
				int measuresErrors = 0;
				long measuresTime = 0;
				for (Call call : calls) {
					for (Measure measure : parameters.getMeasures()) {
						if (Call.isAttempted(call.getMeasureFile(measure))) {
							measuresDone++;
							if (call.isError(call.getMeasureFile(measure))) {
								measuresErrors++;
							} else {
								if (!call.getMeasureTimeFile(measure).exists()) {
									System.out.println(
											"svn remove \"" + call.getMeasureFile(measure).getAbsolutePath() + "\"");
								} else {
									BufferedReader reader = new BufferedReader(
											new FileReader(call.getMeasureTimeFile(measure)));
									String line = reader.readLine();
									measuresTime += Long.valueOf(line);
									reader.close();
								}
							}
						}
					}
				}

				System.out.println("measures: done " + measuresDone + ", errors " + measuresErrors + ", of "
						+ (calls.size() * parameters.getMeasures().size()) + ", time " + measuresTime);
			}
		}
	}

	public static void test(THashMap<Triple<Algorithm, Measure, Integer>, List<Pair<Double, Double>>> results,
			ExperimentParameters parameters) throws FileNotFoundException {

		File outputFile = parameters.getTestsResultsFile();
		PrintWriter w = new PrintWriter(outputFile);

		for (Measure measure : parameters.getMeasures()) {
			for (int meas = 0; meas < measure.getNumberOfMeasures(); meas++) {
				TObjectIntMap<Algorithm> wins = bootstrap(results, parameters, measure, meas);
				for (Algorithm algorithm : parameters.getAlgorithms()) {
					System.out.println(measure.getTitle() + " " + measure.getMeasureNames()[meas] + ": "
							+ algorithm.getAbbreviation() + " wins " + wins.get(algorithm));
					w.println(measure.getTitle() + " " + measure.getMeasureNames()[meas] + ": "
							+ algorithm.getAbbreviation() + " wins " + wins.get(algorithm));
				}
				System.out.println(wins);
			}
		}

		w.close();
	}

	public static TObjectIntMap<Algorithm> bootstrap(
			THashMap<Triple<Algorithm, Measure, Integer>, List<Pair<Double, Double>>> results,
			ExperimentParameters parameters, Measure measure, int meas) {
		TObjectIntMap<Algorithm> wins = new TObjectIntHashMap<>(10, 0.5f, 0);
		int n = 10000;
		int c = 0;

		for (int i = 0; i < n; i++) {
			System.out.println(i);
			Algorithm winner = null;
			BigDecimal lowestAbsSlope = null;

			for (Algorithm algorithm : parameters.getAlgorithms()) {
				List<Pair<Double, Double>> values = results.get(Triple.of(algorithm, measure, meas));

				BigDecimal slope = bootstrapSlope(values);

				//System.out.println("   slope " + algorithm.getAbbreviation() + ": " + slope);

				if (slope != null) {
					BigDecimal absSlope = slope.abs();

					if (lowestAbsSlope == null || absSlope.compareTo(lowestAbsSlope) <= 0) {
						lowestAbsSlope = absSlope;
						winner = algorithm;
					}
				}
			}

			if (winner != null) {
				//System.out.println("  winner " + winner.getAbbreviation());
				wins.adjustOrPutValue(winner, 1, 1);
				c++;
			}
		}

		if (c == 0) {
			return null;
		}
		return wins;
	}

	public static BigDecimal bootstrapSlope(List<Pair<Double, Double>> values) {
		List<Pair<Double, Double>> bootstrapResample = resample(values);
		Pair<BigDecimal, BigDecimal> linearModel = SimpleLinearRegression.regress(bootstrapResample);
		if (linearModel == null) {
			return null;
		}
		return linearModel.getB();
	}

	public static <X> List<X> resample(List<X> sample) {
		List<X> result = new ArrayList<>();
		Random random = new Random();

		for (int i = 0; i < sample.size(); i++) {
			result.add(sample.get(random.nextInt(sample.size())));
		}

		return result;
	}

	public static void createCorrelationPlot(String name, List<Pair<Double, Double>> values, File outputImageFile,
			Pair<BigDecimal, BigDecimal> linearModel) throws IOException {
		CorrelationPlot plot = new CorrelationPlot();

		outputImageFile.mkdirs();

		double[] x = new double[values.size()];
		double[] y = new double[values.size()];
		for (int i = 0; i < values.size(); i++) {
			x[i] = values.get(i).getA();
			y[i] = values.get(i).getB();
		}

		BufferedImage image = plot.create("noise", x, name, y, linearModel.getA().doubleValue(),
				linearModel.getB().doubleValue());
		ImageIO.write(image, "png", outputImageFile);
	}
}
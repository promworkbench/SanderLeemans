package svn41statistics.logcategoricaltest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import svn41statistics.Diagonal;

public class svn41lct2Gather {

	private static final String n = "\n";

	public static void main(String... args) throws Exception {

		svn41lctExperimentParameters parameters = new svn41lctExperimentParameters();

		for (File logFile : parameters.getLogDirectory().listFiles()) {
			System.out.println(logFile.getName());

			double[][] values = getMeasures(logFile, parameters);

			if (values == null) {
				System.out.println("not ready yet");
			} else {

				long maxTime = getMaxTime(logFile, parameters);
				System.out.println(" max time " + maxTime);

				PrintWriter writer;
				{
					svn41lctCall call = new svn41lctCall(logFile.getName(), 0, 0, parameters,
							parameters.log2attribute.get(logFile.getName()));

					call.getResultsFile().getParentFile().mkdirs();

					writer = new PrintWriter(call.getResultsFile());
				}

				//write header
				writer.write("numberOfSamples,sampleSize,pValue" + n);

				{
					//write measures
					for (int i = 0; i < values.length; i++) {
						for (int j = 0; j < values[i].length; j++) {
							writer.write(parameters.getNumbersOfSamples().get(i) + ","
									+ parameters.getSampleSizes().get(j) + "," + values[i][j] + n);
						}
						writer.write(n);
					}
				}

				writer.close();

				System.out.println(" written " + values.length);
			}
		}
	}

	public static long getMaxTime(File logFile, svn41lctExperimentParameters parameters) throws IOException {
		return new Diagonal() {

			public File getFile(int numberOfSamples, int sampleSize) {
				return new svn41lctCall(logFile.getName(), numberOfSamples, sampleSize, parameters,
						parameters.log2attribute.get(logFile.getName())).getAssociationTimeFile();
			}

		}.getTimesMax(parameters.getNumbersOfSamples(), parameters.getSampleSizes());
	}

	public static double[][] getMeasures(File logFile, svn41lctExperimentParameters parameters) throws IOException {
		return new Diagonal() {

			public File getFile(int numberOfSamples, int sampleSize) {
				return new svn41lctCall(logFile.getName(), numberOfSamples, sampleSize, parameters,
						parameters.log2attribute.get(logFile.getName())).getAssociationFile();
			}
		}.getMeasures(parameters.getNumbersOfSamples(), parameters.getSampleSizes());
	}
}
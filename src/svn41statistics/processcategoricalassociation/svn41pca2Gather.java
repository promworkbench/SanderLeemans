package svn41statistics.processcategoricalassociation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import svn41statistics.Diagonal;

public class svn41pca2Gather {

	private static final String n = "\n";

	public static void main(String... args) throws Exception {

		svn41pcaExperimentParameters parameters = new svn41pcaExperimentParameters();

		for (File logFile : parameters.getLogDirectory().listFiles()) {
			System.out.println(logFile.getName());

			double[][] values = getMeasures(logFile, parameters);

			if (values == null) {
				System.out.println("not ready yet");
			} else {

				PrintWriter writer;
				{
					svn41pcaCall call = new svn41pcaCall(logFile.getName(), 0, 0, parameters,
							parameters.log2attribute.get(logFile.getName()));

					call.getResultsFile().getParentFile().mkdirs();

					writer = new PrintWriter(call.getResultsFile());
				}

				//write header
				writer.write("numberOfSamples,sampleSize,correlation" + n);

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

				System.out.println("written, size " + values.length);
			}
		}
	}

	public static double[][] getMeasures(File logFile, svn41pcaExperimentParameters parameters) throws IOException {
		return new Diagonal() {
			public File getFile(int numberOfSamples, int sampleSize) {
				svn41pcaCall call = new svn41pcaCall(logFile.getName(), numberOfSamples, sampleSize, parameters,
						parameters.log2attribute.get(logFile.getName()));
				return call.getAssociationFile();
			}
		}.getMeasures(parameters.getNumbersOfSamples(), parameters.getSampleSizes());
	}
}
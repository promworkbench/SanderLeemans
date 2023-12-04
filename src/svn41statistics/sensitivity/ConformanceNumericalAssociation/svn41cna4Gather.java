package svn41statistics.sensitivity.ConformanceNumericalAssociation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

public class svn41cna4Gather {

	private static final String s = ",";
	private static final String n = "\n";

	public static void main(String... args) throws Exception {

		svn41cnaExperimentParameters parameters = new svn41cnaExperimentParameters();

		for (File logFile : parameters.getLogDirectory().listFiles()) {
			System.out.println(logFile.getName());
			boolean complete = true;

			for (Algorithm algorithm : parameters.getAlgorithms()) {

				PrintWriter writer;
				{
					svn41cnaCall call = new svn41cnaCall(logFile.getName(), algorithm, 0, parameters,
							parameters.log2attribute.get(logFile.getName()));

					call.getResultsFile().getParentFile().mkdirs();

					writer = new PrintWriter(call.getResultsFile());
				}

				//write header
				writer.write("numberOfSamples,correlation,time" + n);

				//write body
				for (int numberOfSamples : parameters.getSamples()) {

					svn41cnaCall sCall = new svn41cnaCall(logFile.getName(), algorithm, numberOfSamples, parameters,
							parameters.log2attribute.get(logFile.getName()));

					if (!sCall.isAttempted(sCall.getDiscoveredModelFile())) {
						writer.write(numberOfSamples + s + -1 + s + 0 + n);
						complete = false;
					} else if (sCall.isAttempted(sCall.getDiscoveredModelFile())
							&& sCall.isError(sCall.getDiscoveredModelFile())) {
						writer.write(numberOfSamples + s + -1 + s + 0 + n);
						complete = false;
					} else if (sCall.isAttempted(sCall.getAssociationFile())
							&& !sCall.isError(sCall.getAssociationFile())) {
						//write measure
						double value;
						{
							BufferedReader reader = new BufferedReader(new FileReader(sCall.getAssociationFile()));
							String line = reader.readLine();
							value = Double.valueOf(line);
							reader.close();
						}

						long time;
						{
							BufferedReader reader = new BufferedReader(new FileReader(sCall.getAssociationTimeFile()));
							String line = reader.readLine();
							time = Long.valueOf(line);
							reader.close();
						}

						writer.write(numberOfSamples + s + value + s + time + n);
					} else {
						//writer.write(numberOfSamples + s + -1 + s + 0 + n);
						complete = false;
					}
				}
				writer.close();
			}
			
			if (!complete) {
				System.out.println(" incomplete");
			}
		}
	}
}
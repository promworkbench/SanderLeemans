package bpm2020cohortanalysis;

import java.io.File;
import java.io.PrintWriter;

import org.deckfour.xes.model.XLog;
import org.processmining.cohortanalysis.feature.AttributeFeatureMap;
import org.processmining.cohortanalysis.feature.set.FeatureSetIteratorImpl;
import org.processmining.cohortanalysis.parameters.CohortAnalysisParametersDefault;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import thesis.helperClasses.FakeContext;

public class Perform2CountFeatureSets {

	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getFeatureSetsDirectory().mkdirs();

		File[] files = parameters.getLogDirectory().listFiles();
		for (int featureSetSize : parameters.getFeatureSetSizes()) {
			for (File logFile : files) {
				Call call = new Call(logFile.getName(), featureSetSize, parameters);

				File featureSetFile = call.getFeatureSetsFile();

				if (!call.isDone(featureSetFile)) {
					System.out.println(call);
					try {
						XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, logFile);

						System.out.println(" gather attributes");

						AttributeFeatureMap attributeMap = CohortAnalysisParametersDefault.defaultFeatureFactory
								.getFeatures(log, CohortAnalysisParametersDefault.defaultExcluceTraceAttributes,
										CohortAnalysisParametersDefault.defaultSizeThreshold, new ProMCanceller() {
											public boolean isCancelled() {
												return false;
											}
										});

						System.out.println(" start counting");

						FeatureSetIteratorImpl it = new FeatureSetIteratorImpl(attributeMap, featureSetSize);
						long count = 0;
						while (it.hasNext()) {
							it.next();
							count++;
						}

						System.out.println(" done");

						//write result
						PrintWriter writer = new PrintWriter(featureSetFile);
						writer.write(count + "");
						writer.flush();
						writer.close();

					} catch (Exception e) {
						PrintWriter writer = new PrintWriter(featureSetFile);
						writer.write("error\n");
						e.printStackTrace(writer);
						writer.flush();
						writer.close();
					}
				}
			}
		}
	}
}
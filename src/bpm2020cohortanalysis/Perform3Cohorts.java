package bpm2020cohortanalysis;

import java.io.File;
import java.io.PrintWriter;

import org.deckfour.xes.model.XLog;
import org.processmining.cohortanalysis.cohort.Cohorts;
import org.processmining.cohortanalysis.cohort.Cohorts2File;
import org.processmining.cohortanalysis.parameters.CohortAnalysisParametersAbstract;
import org.processmining.cohortanalysis.parameters.CohortAnalysisParametersDefault;
import org.processmining.cohortanalysis.plugins.CohortAnalysisPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import thesis.helperClasses.FakeContext;

public class Perform3Cohorts {

	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getCohortsDirectory().mkdirs();

		File[] files = parameters.getLogDirectory().listFiles();
		for (File logFile : files) {

			if (!logFile.getName().contains("2018")) {

				for (int featureSetSize : parameters.getFeatureSetSizes()) {
					Call call = new Call(logFile.getName(), featureSetSize, parameters);

					File cohortsFile = call.getCohortsFile();

					try {

						if (!Call.isAttempted(cohortsFile)) {
							System.out.println("getting measures from " + call);

							XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, logFile);

							CohortAnalysisParametersAbstract cParameters = new CohortAnalysisParametersDefault();
							cParameters.setDebug(true);
							cParameters.setMaxFeatureSetSize(featureSetSize);

							long start = System.currentTimeMillis();
							Cohorts cohorts = CohortAnalysisPlugin.measure(log, cParameters, new ProMCanceller() {
								public boolean isCancelled() {
									return false;
								}
							});
							long duration = System.currentTimeMillis() - start;

							//write result
							{
								PrintWriter writer = new PrintWriter(cohortsFile);
								writer.write(Cohorts2File.convert(cohorts));
								writer.flush();
								writer.close();
							}

							{
								PrintWriter writer = new PrintWriter(call.getCohortsTimeFile());
								writer.write(duration + " ms");
								writer.flush();
								writer.close();
							}
						}
					} catch (Exception e) {
						PrintWriter writer = new PrintWriter(cohortsFile);
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
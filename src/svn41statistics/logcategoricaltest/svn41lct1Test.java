package svn41statistics.logcategoricaltest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.statisticaltests.logcategoricaltest.LogCategoricalTest;
import org.processmining.statisticaltests.logcategoricaltest.LogCategoricalTestParametersAbstract;
import org.processmining.statisticaltests.logcategoricaltest.LogCategoricalTestParametersDefault;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import thesis.helperClasses.FakeContext;

public class svn41lct1Test {
	public static void main(String... args) throws Exception {
		svn41lctExperimentParameters parameters = new svn41lctExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getAssociationDirectory().mkdirs();

		List<svn41lctCall> calls = parameters.getCalls();
		//Collections.shuffle(calls);

		for (svn41lctCall call : calls) {

			if (!call.getAssociationFile().exists()) {
				System.out.println(call);

				XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, call.getLogFile());

				ProMCanceller canceller = new ProMCanceller() {
					public boolean isCancelled() {
						return false;
					}
				};
				LogCategoricalTestParametersAbstract testParameters = new LogCategoricalTestParametersDefault(
						call.getAttribute());
				testParameters.setNumberOfSamples(call.getNumberOfSamples());
				testParameters.setSampleSize(call.getSampleSize());
				testParameters.setDebug(true);
				testParameters.setSeed(1);

				//perform the sampling
				long start = System.currentTimeMillis();

				double p = new LogCategoricalTest().test(log, testParameters, canceller, null);

				long duration = System.currentTimeMillis() - start;

				//write the results to a file
				{
					PrintWriter writer = new PrintWriter(call.getAssociationFile());
					writer.println(p);
					writer.close();
				}

				//write the time to a file
				{
					PrintWriter writer = new PrintWriter(call.getAssociationTimeFile());
					writer.println(duration);
					writer.close();
				}
			}
		}

		System.out.println("done");
	}

	public static boolean isError(File file) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(file));
		String firstLine = r.readLine();
		r.close();
		return firstLine.startsWith("error");
	}
}
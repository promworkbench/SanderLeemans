package svn41statistics.processcategoricalassociation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.statisticaltests.association.AssociationProcessCategorical;
import org.processmining.statisticaltests.association.AssociationsParametersAbstract;
import org.processmining.statisticaltests.association.AssociationsParametersDefault;
import org.processmining.statisticaltests.helperclasses.Correlation;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import thesis.helperClasses.FakeContext;

public class svn41pca1Correlation {
	public static void main(String... args) throws Exception {
		svn41pcaExperimentParameters parameters = new svn41pcaExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getAssociationDirectory().mkdirs();

		List<svn41pcaCall> calls = parameters.getCalls();
		//Collections.shuffle(calls);

		for (svn41pcaCall call : calls) {

			if (!call.getAssociationFile().exists()) {
				System.out.println(call);

				XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, call.getLogFile());

				Attribute attribute = call.getAttribute();
				ProMCanceller canceller = new ProMCanceller() {
					public boolean isCancelled() {
						return false;
					}
				};
				AssociationsParametersAbstract associationsParameters = new AssociationsParametersDefault();
				associationsParameters.setNumberOfSamples(call.getNumberOfSamples());
				associationsParameters.setSampleSize(call.getSampleSize());
				associationsParameters.setDebug(true);
				associationsParameters.setSeed(1);

				//perform the sampling
				long start = System.currentTimeMillis();

				double[][] result = AssociationProcessCategorical.compute(attribute, associationsParameters, log,
						canceller);

				long duration = System.currentTimeMillis() - start;

				//compute correlation
				double[] x = result[0];
				double[] y = result[1];
				BigDecimal meanY = Correlation.mean(y);
				double standardDeviationYd = Correlation.standardDeviation(y, meanY);
				double correlation = Correlation.correlation(x, y, meanY, standardDeviationYd).doubleValue();

				//write the results to a file
				{
					PrintWriter writer = new PrintWriter(call.getAssociationFile());
					writer.println(correlation);
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
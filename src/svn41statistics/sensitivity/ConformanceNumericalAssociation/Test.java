package svn41statistics.sensitivity.ConformanceNumericalAssociation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.traceattributes.Correlation;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.statisticaltests.association.AssociationProcessNumerical;
import org.processmining.statisticaltests.association.AssociationsParametersAbstract;
import org.processmining.statisticaltests.association.AssociationsParametersDefault;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import thesis.helperClasses.FakeContext;

public class Test {
	public static File parentFolder = new File("/home/sander/Documents/svn/41 - stochastic statistics/experiments/");
	public static File resultsFolder = new File(parentFolder, "07 - conformance numerical association sensitivity");
	public static File logFolder = new File(parentFolder, "00 - base logs");

	public static void main(String[] args) {
		resultsFolder.mkdirs();

	}

	private static void multipleCorrelation(File inputLog, File outputCsv, int step, int maxNumberOfSamples,
			Attribute attribute) throws Exception {
		outputCsv.getParentFile().mkdirs();
		int startNumberOfSamples;
		BufferedWriter output;
		if (!outputCsv.exists()) {
			outputCsv.createNewFile();
			startNumberOfSamples = step;
			output = new BufferedWriter(new FileWriter(outputCsv, false));
			output.write("numberOfSamples,correlation,time\n");
		} else {

			//count the number of lines in the file
			BufferedReader reader = new BufferedReader(new FileReader(outputCsv));
			startNumberOfSamples = 0;
			while (reader.readLine() != null) {
				startNumberOfSamples += step;
			}
			reader.close();

			output = new BufferedWriter(new FileWriter(outputCsv, true));
		}

		PluginContext context = new FakeContext();
		XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, inputLog);

		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};

		AssociationsParametersAbstract parameters = new AssociationsParametersDefault();

		for (int numberOfSamples = startNumberOfSamples; numberOfSamples <= maxNumberOfSamples; numberOfSamples += step) {
			parameters.setNumberOfSamples(numberOfSamples);

			long startTime = System.currentTimeMillis();
			double[][] result = AssociationProcessNumerical.compute(attribute, parameters, log, canceller);
			long time = System.currentTimeMillis() - startTime;

			double[] x = result[0];
			double[] y = result[1];

			BigDecimal meanY = Correlation.mean(y);
			double standardDeviationYd = Correlation.standardDeviation(y, meanY);
			double correlation = Correlation.correlation(x, y, meanY, standardDeviationYd).doubleValue();

			output.write(numberOfSamples + "," + correlation + "," + time + "\n");
			output.flush();
		}
		output.close();
	}
}
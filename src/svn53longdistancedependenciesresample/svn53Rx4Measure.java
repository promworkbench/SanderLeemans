package svn53longdistancedependenciesresample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.longdistancedependencies.plugins.StochasticLabelledPetriNetAdjustmentWeightsImportPlugin;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;
import org.processmining.stochasticlabelledpetrinets.plugins.StochasticLabelledPetriNetImportPlugin;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import thesis.helperClasses.FakeContext;

public class svn53Rx4Measure {
	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getMeasuresDirectory().mkdirs();

		List<Call> calls = parameters.getCalls();
		Collections.shuffle(calls);
		for (Call call : calls) {
			File discoveredStochasticModelFile = call.getDiscoveredStochasticModelFile();
			File measureFile = call.getMeasureFile();
			measureFile.getParentFile().mkdirs();

			if (Call.isAttempted(discoveredStochasticModelFile) && !Call.isAttempted(measureFile)
					&& (false || discoveredStochasticModelFile.getName().contains("Parcel"))) {
				if (Call.isError(discoveredStochasticModelFile)) {

				} else {
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
					LocalDateTime now = LocalDateTime.now();
					System.out.println("measuring to " + measureFile + " @" + dtf.format(now));

					XLog testLog = (XLog) new OpenLogFileLiteImplPlugin().importFile(context,
							call.getSplitLogTestFile());

					StochasticLabelledPetriNet net = loadNet(discoveredStochasticModelFile);

					long start = System.currentTimeMillis();

					double[] results;
					try {
						results = call.getMeasure().compute(testLog, net, call);

						//measure time
						{
							long end = System.currentTimeMillis();
							PrintWriter writer = new PrintWriter(call.getMeasureTimeFile());
							writer.write((end - start) + "\n");
							writer.write("ms\n");
							writer.close();
						}

						//write the results to a file
						{
							PrintWriter writer = new PrintWriter(call.getMeasureFile());
							for (int i = 0; i < call.getMeasure().getNumberOfMeasures(); i++) {
								writer.println(results[i] + " " + call.getMeasure().getMeasureNames()[i]);
							}
							writer.close();
						}

					} catch (Exception e) {
						Call.setError(measureFile, e);
					}
				}
			}
		}
		System.out.println("done");
	}

	public static StochasticLabelledPetriNet loadNet(File stochasticModelFile)
			throws NumberFormatException, FileNotFoundException, IOException {
		FileInputStream stream = new FileInputStream(stochasticModelFile);
		if (stochasticModelFile.getName().endsWith(".slpna")) {
			//long-distance dependencies
			return StochasticLabelledPetriNetAdjustmentWeightsImportPlugin.read(stream);
		} else {
			return StochasticLabelledPetriNetImportPlugin.read(stream);
		}
	}
}
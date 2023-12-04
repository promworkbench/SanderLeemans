package svn55datastochastics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.stochasticlabelleddatapetrinet.plugins.SLDPN;
import org.processmining.stochasticlabelleddatapetrinet.plugins.SLDPNImportPlugin;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSimpleWeights;
import org.processmining.stochasticlabelledpetrinets.plugins.StochasticLabelledPetriNetImportPlugin;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import thesis.helperClasses.FakeContext;

public class svn55R4Measure {
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
					&& (false || discoveredStochasticModelFile.getName().contains("Interna")
							&& !discoveredStochasticModelFile.getName().contains("SERV")
							&& !discoveredStochasticModelFile.getName().contains("TRANS"))) {
				if (Call.isError(discoveredStochasticModelFile)) {

				} else {
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
					LocalDateTime now = LocalDateTime.now();
					System.out.println("measuring to " + measureFile + " @" + dtf.format(now));

					XLog testLog = (XLog) new OpenLogFileLiteImplPlugin().importFile(context,
							call.getSplitLogTestFile());

					long start;

					double[] results;
					try {
						if (call.getStochasticAlgorithm().createsDataModels()) {

							start = System.currentTimeMillis();
							SLDPN net = loadDataNet(discoveredStochasticModelFile);
							results = call.getMeasure().compute(testLog, net, call);

						} else {
							StochasticLabelledPetriNetSimpleWeights net = loadNet(discoveredStochasticModelFile);

							start = System.currentTimeMillis();
							results = call.getMeasure().compute(testLog, net, call);
						}

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

						System.exit(0);
					} catch (Exception e) {
						Call.setError(measureFile, e);
					}
				}
			}
		}
		System.out.println("done");
	}

	public static StochasticLabelledPetriNetSimpleWeights loadNet(File stochasticModelFile)
			throws NumberFormatException, FileNotFoundException, IOException {
		FileInputStream stream = new FileInputStream(stochasticModelFile);
		return StochasticLabelledPetriNetImportPlugin.read(stream);
	}

	public static SLDPN loadDataNet(File stochasticModelFile)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, FileNotFoundException, IOException, ClassNotFoundException {
		return SLDPNImportPlugin.read(new FileInputStream(stochasticModelFile));
	}
}
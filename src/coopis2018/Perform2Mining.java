package coopis2018;

import java.io.File;
import java.io.PrintWriter;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import thesis.helperClasses.FakeContext;

public class Perform2Mining {

	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getModelsDirectory().mkdirs();
		parameters.getMeasuresDirectory().mkdirs();

		File[] files = parameters.getLogDirectory().listFiles();
		//ArrayUtils.reverse(files);
		for (File logFile : files) {
			for (Algorithm algorithm : parameters.getAlgorithms()) {
				for (int runNr : parameters.getRuns()) {
					for (int foldNr : parameters.getFolds()) {
						Call call = new Call(parameters, algorithm, logFile, runNr, foldNr);
						if (!call.isModelDone() && logFile.getName().contains("BPIC15_4")) {
						//if (!call.isModelDone()) {
							System.out.println(call);

							Perform3Measures.deleteMeasures(call, parameters);

							XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context,
									call.getSplitLogDiscoveryFile());
							long start = System.currentTimeMillis();
							try {
								algorithm.run(logFile, log, call.getModelFile());

								long end = System.currentTimeMillis();
								PrintWriter writer = new PrintWriter(call.getTimeFile());
								writer.write((end - start) + "");
								writer.close();

							} catch (Exception e) {
								PrintWriter writer = new PrintWriter(call.getModelFile());
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
	}
}

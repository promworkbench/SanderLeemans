package is2020;

import java.io.File;
import java.io.PrintWriter;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import thesis.helperClasses.FakeContext;

public class Perform1Mining {

	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getDiscoveredModelsDirectory().mkdirs();

		parameters.getLogDirectory().mkdirs(); //for some reason, this is necessary on Ubuntu

		File[] files = parameters.getLogDirectory().listFiles();
		for (File logFile : files) {

			for (Algorithm algorithm : parameters.getAlgorithms()) {
				Call call = new Call(logFile, algorithm, parameters, 0);

				File discoveredModelFile = call.getDiscoveredModelFile();

				if (!Call.isAttempted(discoveredModelFile)) {
					System.out.println("discovering model to " + discoveredModelFile);

					XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, logFile);
					long start = System.currentTimeMillis();
					try {
						algorithm.run(logFile, log, discoveredModelFile);

						//measure time
						long end = System.currentTimeMillis();
						PrintWriter writer = new PrintWriter(call.getDiscoveredModelTimeFile());
						writer.write((end - start) + "\n");
						writer.write("ms\n");
						writer.close();

					} catch (Exception e) {
						PrintWriter writer = new PrintWriter(discoveredModelFile);
						writer.write("error\n");
						e.printStackTrace(writer);
						writer.flush();
						writer.close();
					}
				}
			}
		}
		System.out.println("done");
	}
}

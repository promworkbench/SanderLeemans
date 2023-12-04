package svn41statistics.processprocesslogtest;

import java.io.File;
import java.io.PrintWriter;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import thesis.helperClasses.FakeContext;

public class svn41pplt1Mining {

	public static void main(String... args) throws Exception {
		svn41ppltExperimentParameters parameters = new svn41ppltExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getDiscoveredModelsDirectory().mkdirs();

		File[] files = parameters.getLogDirectory().listFiles();
		for (File logFile : files) {

			for (Algorithm algorithm : parameters.getAlgorithms()) {
				svn41ppltCall call = new svn41ppltCall(logFile.getName(), 0, 0, parameters);

				File discoveredModelFile = call.getDiscoveredModelFile(algorithm);

				if (!svn41ppltCall.isAttempted(discoveredModelFile)) {
					System.out.println("discovering model to " + discoveredModelFile);

					XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, logFile);
					try {
						algorithm.run(logFile, log, discoveredModelFile);
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
	}
}

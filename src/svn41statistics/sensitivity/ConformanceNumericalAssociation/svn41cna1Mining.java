package svn41statistics.sensitivity.ConformanceNumericalAssociation;

import java.io.File;
import java.io.PrintWriter;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import thesis.helperClasses.FakeContext;

public class svn41cna1Mining {

	public static void main(String... args) throws Exception {
		svn41cnaExperimentParameters parameters = new svn41cnaExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getDiscoveredModelsDirectory().mkdirs();

		File[] files = parameters.getLogDirectory().listFiles();
		for (File logFile : files) {

			for (Algorithm algorithm : parameters.getAlgorithms()) {
				svn41cnaCall call = new svn41cnaCall(logFile.getName(), algorithm, 0, parameters, null);

				File discoveredModelFile = call.getDiscoveredModelFile();

				if (!svn41cnaCall.isAttempted(discoveredModelFile)) {
					System.out.println("discovering model from " + discoveredModelFile);

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

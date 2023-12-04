package svn45crimes;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import thesis.helperClasses.FakeContext;

public class tkde2021crimes2Mining {

	public static boolean svn = false;

	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getDiscoveredModelsDirectory().mkdirs();

		int i = 0;

		List<Call> calls = parameters.getCalls();
		Collections.shuffle(calls);
		for (Call call : calls) {
			File discoveredModelFile = call.getDiscoveredModelFile();
			discoveredModelFile.getParentFile().mkdirs();

			if (Call.isAttempted(call.getNoiseLogFile()) && !Call.isAttempted(discoveredModelFile)) {
				System.out.println("discovering model to " + discoveredModelFile);

				XLog noiseLog = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, call.getNoiseLogFile());
				long start = System.currentTimeMillis();
				try {
					call.getAlgorithm().run(call.getNoiseLogFile(), noiseLog, discoveredModelFile);

					//measure time
					long end = System.currentTimeMillis();
					PrintWriter writer = new PrintWriter(call.getDiscoveredModelTimeFile());
					writer.write((end - start) + "\n");
					writer.write("ms\n");
					writer.close();

					if (svn) {
						ProcessBuilder p = new ProcessBuilder("svn", "add",
								call.getDiscoveredModelTimeFile().getAbsolutePath());
						p.inheritIO();
						p.start().waitFor();
					}

				} catch (Exception e) {
					Call.setError(discoveredModelFile, e);
				}

				if (svn) {
					ProcessBuilder p = new ProcessBuilder("svn", "add", discoveredModelFile.getAbsolutePath());
					p.inheritIO();
					p.start().waitFor();

					i++;
					if (i > 10 && ExperimentParameters.svn.tryAcquire()) {
						ProcessBuilder p2 = new ProcessBuilder("svn", "commit", "-m", "m",
								parameters.getDiscoveredModelsDirectory().getAbsolutePath());
						p2.inheritIO();
						p2.start().waitFor();
						System.out.println("done ...");

						i = 0;

						ExperimentParameters.svn.release();
					}
				}
			}
		}
	}
}

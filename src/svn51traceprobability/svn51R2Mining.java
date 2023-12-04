package svn51traceprobability;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import thesis.helperClasses.FakeContext;

public class svn51R2Mining {

	public static boolean svn = false;

	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getDiscoveredModelsDirectory().mkdirs();

		List<Call> calls = parameters.getCalls();
		Collections.shuffle(calls);
		for (Call call : calls) {
			File discoveredModelFile = call.getDiscoveredModelFile();
			discoveredModelFile.getParentFile().mkdirs();

			if (!Call.isAttempted(discoveredModelFile)) {
				System.out.println("discovering model to " + discoveredModelFile);

				XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, call.getLogFile());

				try {
					call.getAlgorithm().run(call.getLogFile(), log, discoveredModelFile);
				} catch (Exception e) {
					Call.setError(discoveredModelFile, e);
				}
			}
		}
		System.out.println("done");
	}
}
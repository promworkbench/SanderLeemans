package svn53longdistancedependencies;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import thesis.helperClasses.FakeContext;

public class svn53x3StochasticMining {

	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getDiscoveredModelsDirectory().mkdirs();

		List<Call> calls = parameters.getCalls();
		Collections.shuffle(calls);
		for (Call call : calls) {
			File discoveredStochasticModelFile = call.getDiscoveredStochasticModelFile();
			discoveredStochasticModelFile.getParentFile().mkdirs();

			if (true || discoveredStochasticModelFile.getName().contains("Parcel") && discoveredStochasticModelFile.getName().contains("7")) {
				if (!Call.isAttempted(discoveredStochasticModelFile)) {

					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
					LocalDateTime now = LocalDateTime.now();
					System.out
							.println("discovering model to " + discoveredStochasticModelFile + " @" + dtf.format(now));

					XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, call.getSplitLogFile());

					AcceptingPetriNet aNet = AcceptingPetriNetFactory.createAcceptingPetriNet();
					aNet.importFromStream(new FakeContext(), new FileInputStream(call.getDiscoveredModelFile()));

					long start = System.currentTimeMillis();

					try {
						call.getStochasticAlgorithm().run(call.getSplitLogFile(), log, aNet,
								discoveredStochasticModelFile);

						//measure time
						{
							long end = System.currentTimeMillis();
							PrintWriter writer = new PrintWriter(call.getDiscoveredStochasticModelTimeFile());
							writer.write((end - start) + "\n");
							writer.write("ms\n");
							writer.close();//
						}
					} catch (Exception e) {
						Call.setError(discoveredStochasticModelFile, e);
					}

				}
			}
		}
	}
}
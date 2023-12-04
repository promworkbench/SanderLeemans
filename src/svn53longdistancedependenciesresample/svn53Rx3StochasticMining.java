package svn53longdistancedependenciesresample;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import thesis.helperClasses.FakeContext;

public class svn53Rx3StochasticMining {

	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		parameters.getDiscoveredModelsDirectory().mkdirs();

		List<Call> calls = parameters.getCalls();
		Collections.shuffle(calls);
		AtomicInteger next = new AtomicInteger(0);

		int threads = 1;
		for (int thread = 0; thread < threads; thread++) {
			new Thread(new Runnable() {
				public void run() {
					int callNr = next.getAndIncrement();
					while (callNr < calls.size()) {
						Call call = calls.get(callNr);

						try {
							processCall(call);
						} catch (Exception e) {
							e.printStackTrace();
							return;
						}

						callNr = next.getAndIncrement();
					}
				}
			}, "thread " + thread).start();
		}
	}

	private static void processCall(Call call) throws Exception {
		PluginContext context = new FakeContext();

		File discoveredStochasticModelFile = call.getDiscoveredStochasticModelFile();
		discoveredStochasticModelFile.getParentFile().mkdirs();

		if (false || discoveredStochasticModelFile.getName().contains("Seps")) {
			if (!Call.isAttempted(discoveredStochasticModelFile)) {

				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				LocalDateTime now = LocalDateTime.now();
				System.out.println("discovering model to " + discoveredStochasticModelFile + " @" + dtf.format(now));

				XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, call.getSplitLogFile());

				AcceptingPetriNet aNet = AcceptingPetriNetFactory.createAcceptingPetriNet();
				aNet.importFromStream(new FakeContext(), new FileInputStream(call.getDiscoveredModelFile()));

				long start = System.currentTimeMillis();

				try {
					call.getStochasticAlgorithm().run(call.getSplitLogFile(), log, aNet, discoveredStochasticModelFile);

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
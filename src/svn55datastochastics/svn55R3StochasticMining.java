package svn55datastochastics;

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

public class svn55R3StochasticMining {

	public static void main(String... args) throws Exception {

		//after a timeout, record failure and start over

		ExperimentParameters parameters = new ExperimentParameters();
		parameters.getDiscoveredModelsDirectory().mkdirs();

		List<Call> calls = parameters.getCalls();
		Collections.shuffle(calls);

		for (Call call : calls) {
			processCallTimeout(call);
		}

		System.out.println("done");
	}

	@SuppressWarnings("deprecation")
	private static void processCallTimeout(final Call call) {
		ExperimentParameters parameters = new ExperimentParameters();
		long timeout = parameters.getStochasticDiscoveryTimeout();

		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					processCall(call);
				} catch (Exception e) {
					//it only throws an exception if it's not the fault of the measure; so do not record an error
					e.printStackTrace();
				}
			}
		});

		thread.start();

		try {
			thread.join(timeout);
			if (thread.isAlive()) {
				thread.stop();

				//report timeout failure
				Call.setError(call.getDiscoveredStochasticModelFile(), "timeout " + timeout);

				//commit suicide
				System.exit(0);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void processCall(Call call) throws Exception {
		PluginContext context = new FakeContext();

		File discoveredStochasticModelFile = call.getDiscoveredStochasticModelFile();
		discoveredStochasticModelFile.getParentFile().mkdirs();

		if (true || discoveredStochasticModelFile.getName().contains("Sample")
				&& !discoveredStochasticModelFile.getName().contains("15")) {
			if (!Call.isAttempted(discoveredStochasticModelFile)) {

				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				LocalDateTime now = LocalDateTime.now();
				System.out.println("discovering model to " + discoveredStochasticModelFile + " @" + dtf.format(now));

				XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, call.getSplitLogFile());

				assert !call.getStochasticAlgorithm().createsDataModels();

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
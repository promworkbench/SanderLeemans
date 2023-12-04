package svn45crimes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.processmining.framework.plugin.PluginContext;

import thesis.helperClasses.FakeContext;

public class tkde2021crimes5CheckModels {
	private static Measure measure = new MeasureArya();

	public static void main(String... args) throws IOException, Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getMeasuresDirectory().mkdirs();

		AtomicInteger nulls = new AtomicInteger();
		AtomicInteger notnulls = new AtomicInteger();

		List<Call> calls = parameters.getCalls();
		Collections.shuffle(calls);
		int c = 0;
		for (Call call : calls) {
			processCall(context, call, nulls, notnulls);
			c++;

			if (c % 100 == 0) {
				System.out.println(c + " of " + calls.size());
			}
		}

		System.out.println("nulls " + nulls);
		System.out.println("notnulls " + notnulls);
	}

	@SuppressWarnings("deprecation")
	private static void processCall(PluginContext context, Call call, AtomicInteger nulls, AtomicInteger notnulls)
			throws IOException, FileNotFoundException, Exception {
		File discoveredModelFile = call.getDiscoveredModelFile();

		if (Call.isAttempted(discoveredModelFile)) {

			if (!call.isError(discoveredModelFile)) {

				if (call.getAlgorithm().getFileExtension().equals(".pnml")) {
//					//accepting petri nets
//					AcceptingPetriNet aNet = AcceptingPetriNetFactory.createAcceptingPetriNet();
//					aNet.importFromStream(new FakeContext(), new FileInputStream(discoveredModelFile));
//
//					if (aNet.getInitialMarking() == null || aNet.getInitialMarking().size() == 0) {
//						System.out.println(call);
//						System.out.println(call.getDiscoveredModelFile());
//						System.out.println(" no initial marking");
//					}
//
//					if (aNet.getFinalMarkings() == null || aNet.getFinalMarkings().size() == 0) {
//						System.out.println(call);
//						System.out.println(" no final markings");
//					}

					if (Call.isAttempted(call.getMeasureFile(measure)) && !Call.isError(call.getMeasureFile(measure))) {
						BufferedReader reader = new BufferedReader(new FileReader(call.getMeasureFile(measure)));
						for (int i = 0; i < measure.getNumberOfMeasures(); i++) {
							String line = reader.readLine();
							double value = Double.valueOf(line.substring(0, line.indexOf(' ')));
							if (value == 0) {
								nulls.incrementAndGet();
							} else {
								notnulls.incrementAndGet();
							}
						}
						reader.close();
					}
				}
			}
		}
	}
}
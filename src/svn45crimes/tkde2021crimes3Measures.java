package svn45crimes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeImportPlugin;
import org.processmining.plugins.InductiveMiner.reduceacceptingpetrinet.ReduceAcceptingPetriNetKeepLanguage;
import org.processmining.plugins.bpmn.Bpmn;
import org.processmining.plugins.bpmn.plugins.BpmnSelectDiagramPlugin;
import org.processmining.plugins.converters.bpmn2pn.BPMN2PetriNetConverter;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import gnu.trove.set.hash.THashSet;
import sosym2020.BPMN;
import thesis.helperClasses.FakeContext;

public class tkde2021crimes3Measures {

	private static boolean svn = false;

	public static void main(String... args) throws IOException, Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getMeasuresDirectory().mkdirs();

		List<Call> calls = parameters.getCalls();
		Collections.shuffle(calls);
		for (Call call : calls) {
			String n = call.getDiscoveredModelFile().getName();
			if (true || n.contains("Hosp")) {
				processCall(parameters, context, call);
			}
		}
	}

	private static AtomicInteger i = new AtomicInteger();

	private static void processCall(ExperimentParameters parameters, PluginContext context, Call call)
			throws IOException, Exception, FileNotFoundException {
		File discoveredModelFile = call.getDiscoveredModelFile();

		if (Call.isAttempted(discoveredModelFile)) {

			if (call.isError(discoveredModelFile)) {
				//propagate discovery errors
				for (Measure measure : parameters.getMeasures()) {
					if (!call.isMeasureDone(measure)) {
						System.out.println("propagate error");
						call.getMeasureFile(measure).getParentFile().mkdirs();
						call.setError(call.getMeasureFile(measure), null);

						if (svn) {
							ProcessBuilder p = new ProcessBuilder("svn", "add",
									call.getMeasureFile(measure).getAbsolutePath());
							p.inheritIO();
							p.start().waitFor();
						}
					}
				}
			} else {
				for (Measure measure : parameters.getMeasures()) {

					if (!call.isMeasureDone(measure)) {
						//we can perform a measure
						call.getMeasureFile(measure).getParentFile().mkdirs();

						System.out.println("measuring to " + call.getMeasureFile(measure));
						System.out.println(LocalDateTime.now());

						File logFile = call.getLogFile();
						XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, logFile);

						double[] results;
						long start;
						try {
							if (measure.isSupportsTrees() && call.getAlgorithm().getFileExtension().equals(".tree")) {
								//tree
								EfficientTree tree = EfficientTreeImportPlugin.importFromFile(discoveredModelFile);

								start = System.currentTimeMillis();
								results = measure.compute(logFile, log, tree, call);
							} else {
								//accepting petri nets
								AcceptingPetriNet aNet = readModel(call);

								start = System.currentTimeMillis();
								results = measure.compute(logFile, log, aNet, call);
							}
						} catch (Exception e) {
							e.printStackTrace();
							call.setError(call.getMeasureFile(measure), e);
							continue;
						}

						//measure time
						{
							long end = System.currentTimeMillis();
							PrintWriter writer = new PrintWriter(call.getMeasureTimeFile(measure));
							writer.write((end - start) + "\n");
							writer.write("ms\n");
							writer.close();//
						}

						//write the results to a file
						{
							PrintWriter writer = new PrintWriter(call.getMeasureFile(measure));
							for (int i = 0; i < measure.getNumberOfMeasures(); i++) {
								writer.println(results[i] + " " + measure.getMeasureNames()[i]);
							}
							writer.close();
						}

						if (svn) {
							ProcessBuilder p = new ProcessBuilder("svn", "add",
									call.getMeasureFile(measure).getAbsolutePath());
							p.inheritIO();
							p.start().waitFor();

							ProcessBuilder p3 = new ProcessBuilder("svn", "add",
									call.getMeasureTimeFile(measure).getAbsolutePath());
							p3.inheritIO();
							p3.start().waitFor();

							if (i.incrementAndGet() > 10 && ExperimentParameters.svn.tryAcquire()) {
								ProcessBuilder p2 = new ProcessBuilder("svn", "commit", "-m", "m",
										parameters.getMeasuresDirectory().getAbsolutePath());
								p2.inheritIO();
								p2.start().waitFor();
								System.out.println("done ...");

								i.set(0);
								ExperimentParameters.svn.release();
							}
						}
					}
				}
			}
		}
	}

	public static boolean isError(File file) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(file));
		String firstLine = r.readLine();
		r.close();
		return firstLine.startsWith("error");
	}

	public static void fixFinalMarking(final AcceptingPetriNet net) {
		Set<Marking> newMarkings = new THashSet<>();

		Marking newMarking = new Marking(FluentIterable.from(net.getNet().getPlaces()).filter(new Predicate<Place>() {
			public boolean apply(Place place) {
				return net.getNet().getOutEdges(place).isEmpty();
			}
		}).toSet());
		newMarkings.add(newMarking);
		net.getFinalMarkings().add(newMarking);
	}

	public static AcceptingPetriNet readModel(Call call) throws FileNotFoundException, Exception {
		if (call.getAlgorithm().getFileExtension() == ".pnml") {
			AcceptingPetriNet aNet = AcceptingPetriNetFactory.createAcceptingPetriNet();
			aNet.importFromStream(new FakeContext(), new FileInputStream(call.getDiscoveredModelFile()));
			return aNet;
		} else if (call.getAlgorithm().getFileExtension() == ".tree") {
			EfficientTree tree = EfficientTreeImportPlugin.importFromFile(call.getDiscoveredModelFile());

			String[] a = tree.getInt2activity();
			for (int i = 0; i < a.length; i++) {
				if (a[i].toLowerCase().endsWith("+complete")) {
					a[i] = a[i].substring(0, a[i].lastIndexOf("+"));
				}
			}

			AcceptingPetriNet aNet = EfficientTree2AcceptingPetriNet.convert(tree);

			Canceller canceller = new Canceller() {
				public boolean isCancelled() {
					return false;
				}
			};
			ReduceAcceptingPetriNetKeepLanguage.reduce(aNet, canceller);
			return aNet;
		} else if (call.getAlgorithm().getFileExtension() == ".bpmn") {
			Bpmn model = BPMN.importFile(call.getDiscoveredModelFile());
			BPMNDiagram diagram = new BpmnSelectDiagramPlugin().selectDefault(new FakeContext(), model);
			//			Object[] r = BPMNToPetriNetConverter.convert(diagram);
			//			AcceptingPetriNet aNet = AcceptingPetriNetFactory.createAcceptingPetriNet((Petrinet) r[0], (Marking) r[1],
			//					(Marking) r[2]);

			BPMN2PetriNetConverter c = new BPMN2PetriNetConverter(diagram);
			c.convert();
			AcceptingPetriNet aNet = AcceptingPetriNetFactory.createAcceptingPetriNet(c.getPetriNet(), c.getMarking(),
					new Marking(c.getFinalPlaces()));

			//these algorithms give a final marking; fix it just to be sure
			fixFinalMarking(aNet);
			return aNet;
		}
		assert (false);
		return null;
	}
}

package sosym2020;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeImportPlugin;
import org.processmining.plugins.InductiveMiner.reduceacceptingpetrinet.ReduceAcceptingPetriNetKeepLanguage;
import org.processmining.plugins.bpmn.Bpmn;
import org.processmining.plugins.bpmn.plugins.BpmnSelectDiagramPlugin;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.raffaeleconforti.conversion.bpmn.BPMNToPetriNetConverter;

import gnu.trove.set.hash.THashSet;
import thesis.helperClasses.FakeContext;

public class Perform3Measures {
	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getMeasuresDirectory().mkdirs();

		File[] files = parameters.getLogDirectory().listFiles();
		//ArrayUtils.reverse(files);
		for (File logFile : files) {
			for (Algorithm algorithm : parameters.getAlgorithms()) {
				for (int runNr : parameters.getRuns()) {
					for (int foldNr : parameters.getFolds()) {

						Call call = new Call(parameters, algorithm, logFile, runNr, foldNr);

						//if (call.getModelFile().exists() && !logFile.getName().contains("BPIC15")
						//		&& logFile.getName().contains("Payment") ) {
						if (call.getModelFile().exists()) {
							for (Measure measure : parameters.getMeasures()) {

								if (!call.isMeasureDone(measure)) {
									if (!isError(call.getModelFile())) {
										//we can perform a measure

										System.out.println(call + ", " + measure.getTitle());

										XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context,
												call.getSplitLogMeasureFile());

										double[] results;
										if (measure.isSupportsTrees() && algorithm.getFileExtension() == ".tree") {
											//measure as tree
											EfficientTree tree = EfficientTreeImportPlugin
													.importFromFile(call.getModelFile());
											results = measure.compute(log, tree);
										} else {
											//measure as accepting Petri net
											AcceptingPetriNet aNet = readModel(call);

											//File x = new File(call.getMeasureFile(measure).getParent(),
											//		call.getMeasureFile(measure).getName() + ".apnml");
											//aNet.exportToFile(context, x);

											results = measure.compute(log, aNet);
										}

										//write the results to a file
										PrintWriter writer = new PrintWriter(call.getMeasureFile(measure));
										for (int i = 0; i < measure.getNumberOfMeasures(); i++) {
											writer.println(results[i] + " " + measure.getMeasureNames()[i]);
										}
										writer.close();

									} else {
										//the discovery had an error; write the measure file as having an error.
										System.out.println(call + " === error");
										PrintWriter p = new PrintWriter(call.getMeasureFile(measure));
										p.write("error");
										p.close();
									}
								}
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

	public static AcceptingPetriNet readModel(Call call) throws FileNotFoundException, Exception {
		if (call.getAlgorithm().getFileExtension() == ".tree") {
			EfficientTree tree = EfficientTreeImportPlugin.importFromFile(call.getModelFile());

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
			Bpmn model = BPMN.importFile(call.getModelFile());
			BPMNDiagram diagram = new BpmnSelectDiagramPlugin().selectDefault(new FakeContext(), model);
			Object[] r = BPMNToPetriNetConverter.convert(diagram);
			AcceptingPetriNet aNet = AcceptingPetriNetFactory.createAcceptingPetriNet((Petrinet) r[0], (Marking) r[1],
					(Marking) r[2]);

			//these algorithms give a final marking; fix it just to be sure
			fixFinalMarking(aNet);
			return aNet;
		}
		assert (false);
		return null;
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

	public static void deleteMeasures(Call call, ExperimentParameters parameters) {
		for (Measure measure : parameters.getAllMeasures()) {
			if (call.getMeasureFile(measure).exists()) {
				call.getMeasureFile(measure).delete();
			}
		}
	}
}

package svn41statistics.sensitivity.ConformanceNumericalAssociation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
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
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.statisticaltests.association.AssociationConformanceNumerical;
import org.processmining.statisticaltests.association.AssociationsParametersAbstract;
import org.processmining.statisticaltests.association.AssociationsParametersDefault;
import org.processmining.statisticaltests.helperclasses.Correlation;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import gnu.trove.set.hash.THashSet;
import sosym2020.BPMN;
import svn41statistics.CreateCorrelationPlot;
import thesis.helperClasses.FakeContext;

public class svn41cna2Correlation {
	public static void main(String... args) throws Exception {
		svn41cnaExperimentParameters parameters = new svn41cnaExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getAssociationDirectory().mkdirs();

		List<svn41cnaCall> calls = parameters.getCalls();
		//Collections.shuffle(calls);

		for (svn41cnaCall call : calls) {

			if (call.getDiscoveredModelFile().exists() && call.getDiscoveredModelFile().getName().contains("c11")) {
				if (!isError(call.getDiscoveredModelFile())) {
					//we can perform a measure

					if (!call.getAssociationFile().exists()) {
						System.out.println(call);

						XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, call.getLogFile());

						AcceptingPetriNet net = readModel(call);

						Attribute attribute = call.getAttribute();
						ProMCanceller canceller = new ProMCanceller() {
							public boolean isCancelled() {
								return false;
							}
						};
						AssociationsParametersAbstract associationsParameters = new AssociationsParametersDefault();
						associationsParameters.setNumberOfSamples(call.getNumberOfSamples());

						//perform the sampling
						long start = System.currentTimeMillis();

						double[][] result = AssociationConformanceNumerical.compute(attribute, associationsParameters,
								log, net, canceller);

						long duration = System.currentTimeMillis() - start;

						//compute correlation
						double[] x = result[0];
						double[] y = result[1];
						BigDecimal meanY = Correlation.mean(y);
						double standardDeviationYd = Correlation.standardDeviation(y, meanY);
						double correlation = Correlation.correlation(x, y, meanY, standardDeviationYd).doubleValue();

						//write the results to a file
						{
							PrintWriter writer = new PrintWriter(call.getAssociationFile());
							writer.println(correlation);
							writer.close();
						}

						//write image
						CreateCorrelationPlot.create(result, call.getAssociationPlotFile());

						//write the time to a file
						{
							PrintWriter writer = new PrintWriter(call.getAssociationTimeFile());
							writer.println(duration);
							writer.close();
						}
					}
				} else {
					//the discovery had an error; write the measure file as having an error.
					System.out.println(call + " === error");
					PrintWriter p = new PrintWriter(call.getAssociationFile());
					p.write("error");
					p.close();
				}
			}
		}

		System.out.println("done");
	}

	public static boolean isError(File file) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(file));
		String firstLine = r.readLine();
		r.close();
		return firstLine.startsWith("error");
	}

	public static AcceptingPetriNet readModel(svn41cnaCall call) throws FileNotFoundException, Exception {
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
}

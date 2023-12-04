package caise2020isextension;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.earthmoversstochasticconformancechecking.plugins.EarthMoversStochasticConformancePlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.importing.StochasticNetDeserializer;
import org.processmining.plugins.pnml.simple.PNMLRoot;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedAutomatonException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedLogException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedPetriNetException;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import gnu.trove.set.hash.THashSet;
import nl.tue.astar.AStarException;
import thesis.helperClasses.FakeContext;

public class Perform3Measures {
	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getMeasuresDirectory().mkdirs();

		File[] files = parameters.getLogDirectory().listFiles();
		ArrayUtils.shuffle(files);
		for (File logFile : files) {

			for (Algorithm algorithm : parameters.getAlgorithms()) {
				Call call = new Call(logFile.getName(), algorithm, parameters);

				File discoveredModelFile = call.getDiscoveredModelFile();

				if (Call.isAttempted(discoveredModelFile)) {

					for (Measure measure : parameters.getMeasures()) {

						if (!measure.getTitle().contains("E")) {

							if (!call.isMeasureDone(measure)) {
								if (!isError(call.getDiscoveredModelFile())) {
									//we can perform a measure

									System.out.println("measuring to " + call.getMeasureFile(measure));

									XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, logFile);

									StochasticNet model = loadNet(call.getDiscoveredModelFile());
									Marking initialMarking = EarthMoversStochasticConformancePlugin
											.getInitialMarking(model);

									double[] results;
									long start = System.currentTimeMillis();
									try {
										results = measure.compute(log, model, initialMarking, call);

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
									} catch (ProjectedMeasuresFailedException | AutomatonFailedException
											| InterruptedException | AStarException | UnsupportedLogException
											| IllegalTransitionException | UnsupportedPetriNetException
											| CloneNotSupportedException | UnsupportedAutomatonException e) {
										e.printStackTrace();
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

	public static StochasticNet loadNet(File file) throws Exception {
		//load the Stochastic net
		PluginContext context = new FakeContext();
		Serializer serializer = new Persister();
		PNMLRoot pnml = serializer.read(PNMLRoot.class, file);

		StochasticNetDeserializer converter = new StochasticNetDeserializer();
		Object[] objs = converter.convertToNet(context, pnml, file.getName(), true);
		return (StochasticNet) objs[0];
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

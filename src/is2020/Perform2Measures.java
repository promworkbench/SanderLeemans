package is2020;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.deckfour.xes.model.XLog;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogModelAbstract;
import org.processmining.earthmoversstochasticconformancechecking.parameters.EMSCParametersLogModelDefault;
import org.processmining.earthmoversstochasticconformancechecking.parameters.LanguageGenerationStrategyFromModelImpl;
import org.processmining.earthmoversstochasticconformancechecking.plugins.EarthMoversStochasticConformancePlugin;
import org.processmining.earthmoversstochasticconformancechecking.tracealignments.StochasticTraceAlignmentsLogModel;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.importing.StochasticNetDeserializer;
import org.processmining.plugins.pnml.simple.PNMLRoot;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import thesis.helperClasses.FakeContext;

public class Perform2Measures {
	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getMeasureDirectory().mkdirs();

		File[] files = parameters.getLogDirectory().listFiles();

		for (File logFile : files) {
			for (Algorithm algorithm : parameters.getAlgorithms()) {
				for (int unfolding : parameters.getUnfoldings()) {

					Call call = new Call(logFile, algorithm, parameters, unfolding);

					if (call.getDiscoveredModelFile().exists() // 
							&& logFile.getName().startsWith("financial")) {
						//					) {
						if (!isError(call.getDiscoveredModelFile())) {
							//we can perform a measure

							if (!call.getLogMeasuresFile().exists()) {
								System.out.println(call);

								XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, logFile);

								StochasticNet net = loadNet(call.getDiscoveredModelFile());

								Marking initialMarking = EarthMoversStochasticConformancePlugin.getInitialMarking(net);

								EMSCParametersLogModelAbstract emscParameters = new EMSCParametersLogModelDefault();
								emscParameters.setModelTerminationStrategy(new LanguageGenerationStrategyFromModelImpl(
										Long.MAX_VALUE, call.getUnfolding() * 0.01, Integer.MAX_VALUE));
								emscParameters.setDebug(true);
								emscParameters.setComputeStochasticTraceAlignments(false);

								long start = System.currentTimeMillis();
								StochasticTraceAlignmentsLogModel p = EarthMoversStochasticConformancePlugin
										.measureLogModel(log, net, initialMarking, emscParameters, new ProMCanceller() {
											public boolean isCancelled() {
												return false;
											}
										});
								long duration = System.currentTimeMillis() - start;

								double result = p.getSimilarity();

								//write the results to a file
								{
									PrintWriter writer = new PrintWriter(call.getLogMeasuresFile());
									writer.println(result);
									writer.close();
								}

								//write the time to a file
								{
									PrintWriter writer = new PrintWriter(call.getLogMeasuresTimeFile());
									writer.println(duration);
									writer.close();
								}
							}
						} else {
							//the discovery had an error; write the measure file as having an error.
							System.out.println(call + " === error");
							PrintWriter p = new PrintWriter(call.getLogMeasuresFile());
							p.write("error");
							p.close();
						}
					}
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

	public static StochasticNet loadNet(File file) throws Exception {
		//load the Stochastic net
		PluginContext context = new FakeContext();
		Serializer serializer = new Persister();
		PNMLRoot pnml = serializer.read(PNMLRoot.class, file);

		StochasticNetDeserializer converter = new StochasticNetDeserializer();
		Object[] objs = converter.convertToNet(context, pnml, file.getName(), true);
		return (StochasticNet) objs[0];
	}
}

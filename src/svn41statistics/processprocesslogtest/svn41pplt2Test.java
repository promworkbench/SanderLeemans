package svn41statistics.processprocesslogtest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.processmining.earthmoversstochasticconformancechecking.plugins.EarthMoversStochasticConformancePlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.pnml.importing.StochasticNetDeserializer;
import org.processmining.plugins.pnml.simple.PNMLRoot;
import org.processmining.statisticaltests.helperclasses.StochasticNet2StochasticLabelledPetriNet;
import org.processmining.statisticaltests.modelmodellogtest.ModelModelLogTest;
import org.processmining.statisticaltests.modelmodellogtest.ModelModelLogTestParametersAbstract;
import org.processmining.statisticaltests.modelmodellogtest.ModelModelLogTestParametersDefault;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNet;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import thesis.helperClasses.FakeContext;

public class svn41pplt2Test {
	public static void main(String... args) throws Exception {
		svn41ppltExperimentParameters parameters = new svn41ppltExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getTestsDirectory().mkdirs();

		List<svn41ppltCall> calls = parameters.getCalls();
		//Collections.shuffle(calls);

		for (svn41ppltCall call : calls) {

			File modelFileA = call.getDiscoveredModelFile(parameters.getAlgorithms().get(0));
			File modelFileB = call.getDiscoveredModelFile(parameters.getAlgorithms().get(1));

			if (!call.getTestFile().exists() && modelFileA.exists() && modelFileB.exists() && !call.getLogFile().getName().contains("11")) {
				System.out.println(call);

				XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, call.getLogFile());

				ProMCanceller canceller = new ProMCanceller() {
					public boolean isCancelled() {
						return false;
					}
				};

				ModelModelLogTestParametersAbstract testParameters = new ModelModelLogTestParametersDefault();
				testParameters.setNumberOfSamples(call.getNumberOfSamples());
				testParameters.setSampleSize(call.getSampleSize());
				testParameters.setDebug(true);
				testParameters.setSeed(1);
				testParameters.setThreads(1);

				StochasticLabelledPetriNet modelA = loadNet(modelFileA);
				StochasticLabelledPetriNet modelB = loadNet(modelFileB);

				//perform the sampling
				long start = System.currentTimeMillis();

				double p = new ModelModelLogTest().testUnitEMSC(Triple.of(modelA, modelB, log), testParameters,
						canceller, null);

				long duration = System.currentTimeMillis() - start;

				//write the results to a file
				{
					PrintWriter writer = new PrintWriter(call.getTestFile());
					writer.println(p);
					writer.close();
				}

				//write the time to a file
				{
					PrintWriter writer = new PrintWriter(call.getTestTimeFile());
					writer.println(duration);
					writer.close();
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

	public static StochasticLabelledPetriNet loadNet(File file) throws Exception {
		//load the Stochastic net
		PluginContext context = new FakeContext();
		Serializer serializer = new Persister();
		PNMLRoot pnml = serializer.read(PNMLRoot.class, file);

		StochasticNetDeserializer converter = new StochasticNetDeserializer();
		Object[] objs = converter.convertToNet(context, pnml, file.getName(), true);
		StochasticNet sNet = (StochasticNet) objs[0];

		Marking initialMarking = EarthMoversStochasticConformancePlugin.getInitialMarking(sNet);

		return StochasticNet2StochasticLabelledPetriNet.convert(sNet, initialMarking);
	}
}
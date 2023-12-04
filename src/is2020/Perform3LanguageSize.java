package is2020;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

import org.processmining.earthmoversstochasticconformancechecking.helperclasses.EfficientStochasticPetriNetSemanticsImpl;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.TransitionMap;
import org.processmining.earthmoversstochasticconformancechecking.parameters.LanguageGenerationStrategyFromModel;
import org.processmining.earthmoversstochasticconformancechecking.parameters.LanguageGenerationStrategyFromModelImpl;
import org.processmining.earthmoversstochasticconformancechecking.plugins.EarthMoversStochasticConformancePlugin;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPathLanguageImpl;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPetriNet2StochasticPathLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticTransition2IndexKey;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.importing.StochasticNetDeserializer;
import org.processmining.plugins.pnml.simple.PNMLRoot;
import org.python.google.common.util.concurrent.AtomicDouble;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import thesis.helperClasses.FakeContext;

public class Perform3LanguageSize {
	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		PluginContext context = new FakeContext();
		parameters.getLanguageSizesDirectory().mkdirs();

		File[] files = parameters.getLogDirectory().listFiles();

		for (File logFile : files) {
			for (Algorithm algorithm : parameters.getAlgorithms()) {
				for (int unfolding : parameters.getUnfoldings()) {

					Call call = new Call(logFile, algorithm, parameters, unfolding);

					if (call.getDiscoveredModelFile().exists() && logFile.getName().startsWith("Sep")
							&& unfolding <= 74) {
						if (!isError(call.getDiscoveredModelFile())) {
							//we can perform a measure

							if (!call.getLanguageSizeFile().exists()) {
								System.out.println(call);

								StochasticNet net = loadNet(call.getDiscoveredModelFile());

								Marking initialMarking = EarthMoversStochasticConformancePlugin.getInitialMarking(net);

								Activity2IndexKey activityKey = new Activity2IndexKey();
								ProMCanceller canceller = new ProMCanceller() {
									public boolean isCancelled() {
										return false;
									}
								};
								LanguageGenerationStrategyFromModel terminationStrategy = new LanguageGenerationStrategyFromModelImpl(
										Long.MAX_VALUE, call.getUnfolding() * 0.01, Integer.MAX_VALUE);

								//initialise
								EfficientStochasticPetriNetSemanticsImpl semantics = new EfficientStochasticPetriNetSemanticsImpl(
										net, initialMarking);
								TransitionMap transitionMap = new TransitionMap(net, semantics);
								StochasticTransition2IndexKey transitionKey = new StochasticTransition2IndexKey(
										semantics, activityKey);
								final AtomicInteger size = new AtomicInteger();
								final AtomicDouble sum = new AtomicDouble();
								StochasticPathLanguageImpl language = new StochasticPathLanguageImpl(transitionKey,
										activityKey) {
									@Override
									public synchronized void add(int[] path, double probability) {
										int x = size.incrementAndGet();
										double s = sum.addAndGet(probability);

										if (x % 100000 == 0) {
											System.out.println(x + " " + s);
										}
										probabilities.add(probability);
									}
								};

								//set to work
								double massCovered = StochasticPetriNet2StochasticPathLanguage.walk(language, semantics,
										transitionMap, initialMarking, terminationStrategy, canceller);

								//write the results to a file
								{
									PrintWriter writer = new PrintWriter(call.getLanguageSizeFile());
									writer.println(massCovered);
									writer.println(size.get());
									writer.close();
								}
							}
						} else {

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

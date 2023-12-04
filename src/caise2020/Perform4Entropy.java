package caise2020;

import java.io.File;
import java.io.PrintWriter;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.stochasticawareconformancechecking.automata.Log2StochasticDeterministicFiniteAutomaton;
import org.processmining.stochasticawareconformancechecking.automata.StochasticDeterministicFiniteAutomatonMapped;
import org.processmining.stochasticawareconformancechecking.helperclasses.RelativeEntropy;
import org.processmining.stochasticawareconformancechecking.helperclasses.StochasticPetriNet2StochasticDeterministicFiniteAutomaton2;
import org.processmining.stochasticawareconformancechecking.plugins.StochasticPetriNet2StochasticDeterministicFiniteAutomatonPlugin;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import thesis.helperClasses.FakeContext;

public class Perform4Entropy {

	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		final PluginContext context = new FakeContext();
		parameters.getEntropyDirectory().mkdirs();

		File[] files = parameters.getLogDirectory().listFiles();
		for (File logFile : files) {
			for (Algorithm algorithm : parameters.getAlgorithms()) {
				Call call = new Call(logFile.getName(), algorithm, parameters);

				try {
					if (Call.isDone(call.getDiscoveredModelFile()) && !call.isAttempted(call.getEntropyFile())) {
						System.out.println("computing entropy of " + call.getDiscoveredModelFile().getName());

						XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, logFile);
						StochasticNet net = Perform3Simulate.loadNet(call.getDiscoveredModelFile());

						long start = System.currentTimeMillis();

						StochasticDeterministicFiniteAutomatonMapped automatonA = Log2StochasticDeterministicFiniteAutomaton
								.convert(log, MiningParameters.getDefaultClassifier(), new ProMCanceller() {
									public boolean isCancelled() {
										return context.getProgress().isCancelled();
									}
								});

						Marking initialMarking = StochasticPetriNet2StochasticDeterministicFiniteAutomatonPlugin
								.guessInitialMarking(net);
						StochasticDeterministicFiniteAutomatonMapped automatonB = StochasticPetriNet2StochasticDeterministicFiniteAutomaton2
								.convert(net, initialMarking);
						//final Pair<Double, Double> entropy = RelativeEntropy.relativeEntropy(automatonA, automatonB);
						final Pair<Double, Double> p = RelativeEntropy.relativeEntropyHalf(automatonA, automatonB);

						//measure time
						{
							long end = System.currentTimeMillis();
							PrintWriter writer = new PrintWriter(call.getEntropyTimeFile());
							writer.write((end - start) + "\n");
							writer.write("ms\n");
							writer.close();
						}

						//write result
						PrintWriter writer = new PrintWriter(call.getEntropyFile());
						writer.write(p.getA() + " recall\n");
						writer.write(p.getB() + " precision\n");
						writer.flush();
						writer.close();
					}
				} catch (Exception e) {
					PrintWriter writer = new PrintWriter(call.getEntropyFile());
					writer.write("error\n");
					e.printStackTrace(writer);
					writer.flush();
					writer.close();
				}
			}
		}
	}
}
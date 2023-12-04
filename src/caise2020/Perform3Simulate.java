package caise2020;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Random;

import org.processmining.earthmoversstochasticconformancechecking.helperclasses.EfficientStochasticPetriNetSemantics;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.EfficientStochasticPetriNetSemanticsImpl;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.importing.StochasticNetDeserializer;
import org.processmining.plugins.pnml.simple.PNMLRoot;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import thesis.helperClasses.FakeContext;

public class Perform3Simulate {
	public static void main(String... args) throws Exception {
		ExperimentParameters parameters = new ExperimentParameters();
		parameters.getSimulationDirectory().mkdirs();

		File[] files = parameters.getLogDirectory().listFiles();
		for (File logFile : files) {

			for (Algorithm algorithm : parameters.getAlgorithms()) {
				Call call = new Call(logFile.getName(), algorithm, parameters);

				File simulationFile = call.getSimulationFile();

				if (Call.isDone(call.getDiscoveredModelFile()) && Call.isDone(call.getLogMeasuresFile())
						&& !Call.isAttempted(simulationFile)) {
					System.out.println("simulating " + call.getDiscoveredModelFile().getName());
					StochasticNet net = loadNet(call.getDiscoveredModelFile());
					TObjectDoubleMap<String> map = loadLogMeasures(call.getLogMeasuresFile());

					double[] durations = simulate(parameters.getSimulationsize(), net, map);

					//compute average
					BigDecimal sum = BigDecimal.ZERO;
					for (double duration : durations) {
						sum = sum.add(BigDecimal.valueOf(duration));
					}
					BigDecimal average = sum.divide(BigDecimal.valueOf(durations.length));

					//write result
					PrintWriter writer = new PrintWriter(simulationFile);
					writer.write(average + "\n");
					writer.flush();
					writer.close();
				}
			}
		}
	}

	public static TObjectDoubleMap<String> loadLogMeasures(File file) throws IOException {
		TObjectDoubleMap<String> result = new TObjectDoubleHashMap<>(10, 0.5f, 0);

		BufferedReader reader = new BufferedReader(new FileReader(file));
		reader.readLine(); //skip the first line
		String line = reader.readLine();
		while (line != null) {
			String value = line.substring(0, line.indexOf(' '));
			String activity = line.substring(line.indexOf(' ') + 1);

			result.put(activity, Double.valueOf(value));

			line = reader.readLine();
		}
		reader.close();
		return result;
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

	public static double[] simulate(int traces, StochasticNet net, TObjectDoubleMap activity2duration) {

		byte[] initialMarking;
		EfficientStochasticPetriNetSemantics semantics;
		{
			//create initial marking
			Marking marking = new Marking();
			for (Place place : net.getPlaces()) {
				if (net.getInEdges(place).isEmpty()) {
					marking.add(place);
				}
			}
			semantics = new EfficientStochasticPetriNetSemanticsImpl(net, marking);
			initialMarking = semantics.convert(marking);
		}

		Random random = new Random(0);
		double[] result = new double[traces];

		for (int trace = 0; trace < traces; trace++) {
			//simulate a single trace
			semantics.setState(initialMarking);
			double traceDuration = 0;

			int[] enabledTransitions = semantics.getEnabledTransitions();
			while (enabledTransitions.length > 0) {
				//keep executing until a deadlock is encountered

				//choose a transition to execute
				int chosenEnabledTransition = -1;
				{
					double totalWeight = 0;
					for (int enabledTransition : enabledTransitions) {
						totalWeight += semantics.getTransitionWeight(enabledTransition);
					}

					double chosenValue = random.nextDouble() * totalWeight;

					for (int enabledTransition : enabledTransitions) {
						if (chosenValue <= semantics.getTransitionWeight(enabledTransition)) {
							chosenEnabledTransition = enabledTransition;
							break;
						} else {
							chosenValue -= semantics.getTransitionWeight(enabledTransition);
						}
					}
				}

				//execute the transition
				semantics.executeTransition(chosenEnabledTransition);

				//System.out.println("e " + chosenEnabledTransition);

				//get the transition duration
				if (!semantics.isInvisible(chosenEnabledTransition)) {
					String activity = semantics.getLabel(chosenEnabledTransition);
					double duration = activity2duration.get(activity);
					traceDuration += duration;

					//System.out.print(semantics.getLabel(chosenEnabledTransition) + "@" + duration + ", ");
				}

				//update enabled transitions for next time
				enabledTransitions = semantics.getEnabledTransitions();
			}

			result[trace] = traceDuration;

			//System.out.println(" sum " + traceDuration);
		}
		return result;
	}
}

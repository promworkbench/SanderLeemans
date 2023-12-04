package thesis.evaluation.logconformancewithoutkfoldcrossvalidation;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.TreeSet;

import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.Dot2Image;
import org.processmining.plugins.graphviz.dot.Dot2Image.Type;
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.inductiveVisualMiner.plugins.GraphvizPetriNet;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import thesis.evaluation.logconformancewithkfoldcrossvalidation.kFoldCrossValidationConformance;
import thesis.helperClasses.FakeContext;

public class LogConformanceImage {
	public static void main(String[] args) throws Exception {
		System.out.println(LogConformanceSettings.discoveredModelDirectory);
		LogConformanceSettings.imageDirectory.mkdirs();

		TObjectIntMap<String> labelMap = new TObjectIntHashMap<>();
		TObjectIntMap<String> fileNextLabel = new TObjectIntHashMap<>();

		for (File modelFile : LogConformanceSettings.discoveredModelDirectory.listFiles()) {
			if (modelFile.getName().endsWith(".pnml")) {

				File imageFile = new File(LogConformanceSettings.imageDirectory, modelFile.getName() + ".pdf");
				File imagePngFile = new File(LogConformanceSettings.imageDirectory, modelFile.getName() + ".png");
				File imageSvgFile = new File(LogConformanceSettings.imageDirectory, modelFile.getName() + ".svg");
				File errorFile = new File(LogConformanceSettings.imageDirectory, modelFile.getName() + ".error");

				if (!imageFile.exists() && !errorFile.exists()) {

					System.out.println("loading " + modelFile);
					String base = modelFile.getName().substring(0, modelFile.getName().indexOf(".xes"));
					System.out.println(" base log " + base);
					int maxCountLabels = fileNextLabel.putIfAbsent(base, 0);

					if (kFoldCrossValidationConformance.isCorrectModel(modelFile)) {

						try {
							AcceptingPetriNet net = AcceptingPetriNetFactory.createAcceptingPetriNet();
							net.importFromStream(new FakeContext(), new FileInputStream(modelFile));

							System.out.println(" generating image " + modelFile);

							//no algorithm actually gives a correct final marking; fix it
							kFoldCrossValidationConformance.fixFinalMarking(net);

							Dot dot = GraphvizPetriNet.convert(net);

							//set experiment-specific options
							//dot.setGraphOption("layout", "neato");
							dot.setGraphOption("overlap", "vpsc");
							dot.setGraphOption("splines", "true");
							dot.setOption("margin", "0");
							dot.setOption("ranksep", "0.2");
							dot.setDirection(GraphDirection.leftRight);

							//postprocess dot for this experiment-specific
							{
								TreeSet<String> labels = new TreeSet<>();
								for (DotNode node : dot.getNodes()) {
									if (!node.getLabel().equals("")) {
										labels.add(node.getLabel());
									}
								}
								int count = maxCountLabels;
								for (String label : labels) {
									if (!labelMap.containsKey(label)) {
										labelMap.put(label, count++);
									}
								}
								fileNextLabel.put(base, count);
							}

							for (DotNode node : dot.getNodes()) {
								if (node.getLabel().equalsIgnoreCase("payment")) {
									node.setOption("style", "filled");
									node.setOption("fillcolor", "#c3e6fc");
								}
								if (!node.getLabel().equals("")) {
									node.setLabel(int2Letter(labelMap.get(node.getLabel())).toLowerCase());
									node.setOption("margin", "0");
									node.setOption("fontsize", "60");
								}
							}

							Dot2Image.dot2image(dot, imageFile, Type.pdf);
							Dot2Image.dot2image(dot, imagePngFile, Type.png);
							Dot2Image.dot2image(dot, imageSvgFile, Type.svg);
						} catch (Exception e) {
							errorFile.createNewFile();
						}
					} else {
						errorFile.createNewFile();
					}
				}
			}
		}

		//print the label map
		Object[] x = labelMap.keys();
		Arrays.sort(x);
		int i = 0;
		for (Object s : x) {
			System.out.print(int2Letter(labelMap.get(s)).toLowerCase() + "&" + s.toString().replaceAll("_", "\\\\_"));
			if (i % 2 == 1) {
				System.out.println("\\\\");
			} else {
				System.out.print("&");
			}
			i++;
		}
	}

	public static String int2Letter(int i) {
		int quot = i / 26;
		int rem = i % 26;
		char letter = (char) ('A' + rem);
		if (quot == 0) {
			return "" + letter;
		} else {
			return int2Letter(quot - 1) + letter;
		}
	}
}

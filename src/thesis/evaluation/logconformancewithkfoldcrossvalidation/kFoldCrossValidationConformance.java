package thesis.evaluation.logconformancewithkfoldcrossvalidation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeImportPlugin;
import org.processmining.plugins.bpmn.Bpmn;
import org.processmining.plugins.bpmn.plugins.BpmnImportPlugin;
import org.processmining.plugins.bpmn.plugins.BpmnSelectDiagramPlugin;
import org.processmining.processtree.ProcessTree;
import org.processmining.processtree.ptml.importing.PtmlImportTree;
import org.processmining.projectedrecallandprecision.framework.CompareParameters;
import org.processmining.projectedrecallandprecision.helperclasses.EfficientLog;
import org.processmining.projectedrecallandprecision.plugins.CompareLog2EfficientTreePlugin;
import org.processmining.projectedrecallandprecision.plugins.CompareLog2PetriNetPlugin;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.raffaeleconforti.conversion.bpmn.BPMNToPetriNetConverter;

import dk.brics.automaton2.BasicAutomata;
import gnu.trove.set.hash.THashSet;
import thesis.helperClasses.CheckRelaxedSoundnessPrerequisites;
import thesis.helperClasses.CheckRelaxedSoundnessWithLola;
import thesis.helperClasses.FakeContext;
import thesis.helperClasses.Simplicity;
import thesis.helperClasses.XLogParser2EfficientLog;

public class kFoldCrossValidationConformance {
	public static final File fullLogDirectory = new File(
			"C://Users//sander//Documents//svn//00 - the beast//experiments//logQuality\\full logs");
	public static final File testLogDirectory = new File(
			"C://Users//sander//Documents//svn//00 - the beast//experiments//logQuality\\data-test");
	public static final File modelDirectory = new File(
			"C://Users//sander//Documents//svn//00 - the beast//experiments//logQuality\\discoveredModels");
	public static final File measuresDirectory = new File(
			"C://Users//sander//Documents//svn//00 - the beast//experiments//logQuality\\measures");

	//public static final File fullLogDirectory = new File("D://svn//00 - the beast//experiments//logQuality\\full logs");
	//public static final File testLogDirectory = new File("D://svn//00 - the beast//experiments//logQuality\\data-test");
	//public static final File modelDirectory = new File(
	//		"D://svn//00 - the beast//experiments//logQuality\\discoveredModels");
	//public static final File measuresDirectory = new File("D://svn//00 - the beast//experiments//logQuality\\measures");

	public static void main(String[] args) throws Exception {
		for (final File fullLogFile : fullLogDirectory.listFiles()) {
			if (FilenameUtils.getExtension(fullLogFile.getAbsolutePath()).equals("xes")
					|| FilenameUtils.getExtension(fullLogFile.getAbsolutePath()).equals("gz")) {
				if (1 == 2 || fullLogFile.getName().contains("WABO1")) {
					System.out.println("consider full log " + fullLogFile);

					EfficientLog fullLog = loadEfficientLog(fullLogFile);

					for (int fold = 0; fold < 15; fold++) {
						File testLogFile = new File(testLogDirectory,
								fullLogFile.getName() + "-test" + fold + ".xes.gz");
						System.out.println(" test log " + testLogFile);

						EfficientLog testLog = loadEfficientLog(testLogFile);

						for (File modelFile : modelDirectory.listFiles()) {
							String prefix = fullLogFile.getName() + "-discovery" + fold + ".xes.gz-";
							if (modelFile.getName().startsWith(prefix)) {
								System.out.println("  model " + modelFile.getAbsolutePath());

								File resultFile = new File(measuresDirectory, modelFile.getName() + "-measure.txt");

								if (!resultFile.exists()) {

									if (!isCorrectModel(modelFile)) {
										FileWriter writer = new FileWriter(resultFile);
										writer.write("no model");
										writer.close();
										continue;
									}

									if (modelFile.getName().endsWith(".tree")) {
										//load the discovered model
										EfficientTree model = loadEfficientTree(modelFile);

										//compute fitness
										double fitness;
										{
											CompareParameters parameters = new CompareParameters(2);
											parameters.setDebug(false);
											parameters.setComputePrecision(false);
											ProjectedRecallPrecisionResult fitnessResult = CompareLog2EfficientTreePlugin
													.measure(testLog, model, parameters, BasicAutomata.notCanceller);
											fitness = fitnessResult.getRecall();
											System.out.println("   fitness " + fitness);
										}

										//compute precision
										CompareParameters parameters = new CompareParameters(2);
										parameters.setDebug(false);
										parameters.setComputeRecallFitness(false);
										ProjectedRecallPrecisionResult precisionResult = CompareLog2EfficientTreePlugin
												.measure(fullLog, model, parameters, BasicAutomata.notCanceller);
										double precision = precisionResult.getPrecision();
										System.out.println("   precision " + precision);

										//compute simplicity
										int simplicity = Simplicity.measure(model);
										System.out.println("   simplicity " + simplicity);

										FileWriter writer = new FileWriter(resultFile);
										writer.write(fitness + ";" + precision + "$" + simplicity);
										writer.close();
									} else if (modelFile.getName().endsWith(".pnml")
											|| modelFile.getName().endsWith(".bpmn")) {
										//load the discovered model
										AcceptingPetriNet net = AcceptingPetriNetFactory.createAcceptingPetriNet();
										if (modelFile.getName().endsWith(".pnml")) {
											net.importFromStream(new FakeContext(), new FileInputStream(modelFile));

											//no algorithm actually gives a correct final marking; fix it
											fixFinalMarking(net);
										} else {
											Bpmn bpmn = (Bpmn) new BpmnImportPlugin().importFile(new FakeContext(),
													modelFile);
											BPMNDiagram diagram = new BpmnSelectDiagramPlugin()
													.selectDefault(new FakeContext(), bpmn);
											Object[] r = BPMNToPetriNetConverter.convert(diagram);
											net = AcceptingPetriNetFactory.createAcceptingPetriNet((Petrinet) r[0],
													(Marking) r[1], (Marking) r[2]);

											//these algorithms give a final marking; fix it just to be sure
											fixFinalMarking(net);
										}

										//check weak soundness
										try {
											if (!CheckRelaxedSoundnessPrerequisites.checkPrerequisites(net)
													|| !CheckRelaxedSoundnessWithLola.isRelaxedSoundAndBounded(net)) {
												FileWriter writer = new FileWriter(resultFile);
												writer.write("not relaxed sound$" + Simplicity.measure(net));
												writer.close();
												System.out.println("not relaxed sound");
												continue;
											}
										} catch (Exception e) {
											FileWriter writer = new FileWriter(resultFile);
											writer.write("relaxed sound check failed$" + Simplicity.measure(net));
											writer.close();
											System.out.println("relaxed sound check failed");
											continue;
										}

										System.out.println("relaxed sound");

										try {
											//compute fitness
											CompareParameters fitnessParameters = new CompareParameters(2);
											fitnessParameters.setComputePrecision(false);
											ProjectedRecallPrecisionResult fitnessResult = CompareLog2PetriNetPlugin
													.measure(testLog, net, fitnessParameters,
															BasicAutomata.notCanceller);
											double fitness = fitnessResult.getRecall();
											System.out.println("   fitness " + fitness);

											//compute precision
											CompareParameters precisionParameters = new CompareParameters(2);
											precisionParameters.setComputeRecallFitness(false);
											ProjectedRecallPrecisionResult precisionResult = CompareLog2PetriNetPlugin
													.measure(fullLog, net, precisionParameters,
															BasicAutomata.notCanceller);
											double precision = precisionResult.getPrecision();
											System.out.println("   precision " + precision);

											//compute simplicity
											int simplicity = Simplicity.measure(net);
											System.out.println("   simplicity " + simplicity);

											FileWriter writer = new FileWriter(resultFile);
											writer.write(fitness + ";" + precision + "$" + simplicity);
											writer.close();
										} catch (Exception e) {
											e.printStackTrace();
											FileWriter writer = new FileWriter(resultFile);
											writer.write("error$" + Simplicity.measure(net));
											writer.close();
											continue;
										}
									}
								}
							}
						}
					}
				}
			}
		}
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

	public static EfficientLog loadEfficientLog(File file) throws Exception {
		return XLogParser2EfficientLog.load(file, MiningParameters.getDefaultClassifier());
	}

	public static EfficientTree loadEfficientTree(File file) throws Exception {
		return EfficientTreeImportPlugin.importFromFile(file);
	}

	public static ProcessTree loadProcessTree(File file) throws Exception {
		return (ProcessTree) (new PtmlImportTree()).importFile(null, file);
	}

	public static boolean isCorrectModel(File file) throws IOException {
		InputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		BufferedReader br = new BufferedReader(isr);
		boolean result = !br.readLine().contains("error");
		br.close();
		return result;
	}
}

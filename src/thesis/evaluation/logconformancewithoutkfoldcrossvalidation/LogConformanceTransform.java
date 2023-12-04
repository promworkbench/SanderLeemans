package thesis.evaluation.logconformancewithoutkfoldcrossvalidation;

import java.io.File;

import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2processTree;
import org.processmining.plugins.bpmn.Bpmn;
import org.processmining.plugins.bpmn.plugins.BpmnImportPlugin;
import org.processmining.plugins.bpmn.plugins.BpmnSelectDiagramPlugin;
import org.processmining.processtree.ProcessTree;
import org.processmining.processtree.conversion.ProcessTree2Petrinet;
import org.processmining.processtree.conversion.ProcessTree2Petrinet.PetrinetWithMarkings;

import com.raffaeleconforti.conversion.bpmn.BPMNToPetriNetConverter;

import thesis.evaluation.logconformancewithkfoldcrossvalidation.kFoldCrossValidationConformance;
import thesis.helperClasses.FakeContext;

public class LogConformanceTransform {
	public static void main(String[] args) throws Exception {
		System.out.println(LogConformanceSettings.discoveredModelDirectory);

		for (File modelFile : LogConformanceSettings.discoveredModelDirectory.listFiles()) {
			File pnFile = new File(LogConformanceSettings.discoveredModelDirectory, modelFile.getName() + ".pnml");
			if (!pnFile.exists()) {
				if (modelFile.getName().endsWith(".tree")) {

					EfficientTree model = kFoldCrossValidationConformance.loadEfficientTree(modelFile);

					PetrinetWithMarkings pn = ProcessTree2Petrinet.convert(EfficientTree2processTree.convert(model));

					AcceptingPetriNet apn = new AcceptingPetriNetImpl(pn.petrinet, pn.initialMarking, pn.finalMarking);
					apn.exportToFile(null, pnFile);
				}

				if (modelFile.getName().endsWith(".ptml")) {
					ProcessTree tree = kFoldCrossValidationConformance.loadProcessTree(modelFile);
					PetrinetWithMarkings pn = ProcessTree2Petrinet.convert(tree);
					AcceptingPetriNet apn = new AcceptingPetriNetImpl(pn.petrinet, pn.initialMarking, pn.finalMarking);
					apn.exportToFile(new FakeContext(), pnFile);
				}
				
				if (modelFile.getName().endsWith("bpmn")) {
					Bpmn bpmn = (Bpmn) new BpmnImportPlugin().importFile(new FakeContext(),
							modelFile);
					BPMNDiagram diagram = new BpmnSelectDiagramPlugin().selectDefault(
							new FakeContext(), bpmn);
					Object[] r = BPMNToPetriNetConverter.convert(diagram);
					AcceptingPetriNet apn = AcceptingPetriNetFactory.createAcceptingPetriNet((Petrinet) r[0],
							(Marking) r[1], (Marking) r[2]);
					apn.exportToFile(null, pnFile);
				}
			}
		}
	}
}

package thesis.evaluation.standAloneMiners;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.ProcessTree2EfficientTree;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeImportPlugin;
import org.processmining.processtree.ProcessTree;
import org.processmining.processtree.ptml.importing.PtmlImportTree;
import org.processmining.projectedrecallandprecision.framework.CompareParameters;
import org.processmining.projectedrecallandprecision.helperclasses.EfficientLog;
import org.processmining.projectedrecallandprecision.plugins.CompareLog2EfficientTreePlugin;
import org.processmining.projectedrecallandprecision.plugins.CompareLog2PetriNetPlugin;
import org.processmining.projectedrecallandprecision.plugins.CompareProcessTree2ProcessTreePlugin;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult;

import dk.brics.automaton2.BasicAutomata;
import thesis.evaluation.logconformancewithkfoldcrossvalidation.kFoldCrossValidationConformance;
import thesis.helperClasses.FakeContext;

public class RunProjectedRecallPrecision {
	public static void main(String[] args) throws Exception {
		boolean help = false;
		File file1 = null;
		File file2 = null;

		boolean file1isLog = false;
		boolean file1isTree = false;
		boolean file1isPN = false;
		boolean file2isLog = false;
		boolean file2isTree = false;
		boolean file2isPN = false;
		int k = 2;
		if (args.length != 2 && args.length != 3) {
			help = true;
		} else {
			file1 = new File(args[1]);
			file2 = new File(args[0]);

			file1isLog = file1.getName().endsWith(".xes") || file1.getName().endsWith(".xes.gz");
			file2isLog = file2.getName().endsWith(".xes") || file2.getName().endsWith(".xes.gz");
			file1isTree = file1.getName().endsWith(".tree") || file1.getName().endsWith(".ptml");
			file2isTree = file2.getName().endsWith(".tree") || file2.getName().endsWith(".ptml");
			file1isPN = file1.getName().endsWith(".pnml") || file1.getName().endsWith(".apnml");
			file2isPN = file2.getName().endsWith(".pnml") || file2.getName().endsWith(".apnml");

			help = help || !file1.exists() || !file2.exists() || (file1isLog && file2isLog)
					|| (!file1isLog && !file1isTree && !file2isLog && !file2isTree && !file1isPN && !file2isPN)
					|| (!file1isLog && !file1isTree && !file1isPN) || (!file2isLog && !file2isTree && !file2isPN);
		}

		if (args.length == 3) {
			try {
				k = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				help = true;
			}
		}

		if (help) {
			System.out.println("Usage: ProjectedRecallPrecision.jar log/model log/model [k]");
			System.out.println(" Log should be in XES-format (.xes or .xes.gz).");
			System.out.println("  Two logs are not supported at the moment.");
			System.out.println(
					" Model should be in EfficientTree (.tree), ProcessTree (.ptml) or in AcceptingPetriNet (.pnml) format.");
			System.out.println(" k denotes how many projected activities are considered.");
			System.out.println("  default is 2");
			System.out.println("  use a negative value to test all possibilities up to that value");
			return;
		}

		ProjectedRecallPrecisionResult result;
		if (file1isLog || file2isLog) {
			EfficientLog log = null;
			EfficientTree tree = null;
			AcceptingPetriNet pn = null;
			if (file1isLog) {
				log = kFoldCrossValidationConformance.loadEfficientLog(file1);
				if (file2isTree) {
					tree = loadTree(file2);
				} else {
					pn = loadPN(file2);
				}
			} else {
				log = kFoldCrossValidationConformance.loadEfficientLog(file2);
				if (file1isTree) {
					tree = loadTree(file1);
				} else {
					pn = loadPN(file1);
				}
			}

			if (k < 0) {
				System.out.println("k\tfitness\tprecision");
				for (int i = 1; i <= -k; i++) {
					if (tree != null) {
						result = CompareLog2EfficientTreePlugin.measure(log, tree, new CompareParameters(i),
								BasicAutomata.notCanceller);
					} else {
						result = CompareLog2PetriNetPlugin.measure(log, pn, new CompareParameters(i),
								BasicAutomata.notCanceller);
					}
					System.out.println(i + "\t" + result.getRecall() + "\t" + result.getPrecision());
				}
				return;
			}

			CompareParameters parameters = new CompareParameters(k);
			parameters.setDebug(true);
			if (tree != null) {
				result = CompareLog2EfficientTreePlugin.measure(log, tree, parameters, BasicAutomata.notCanceller);
			} else {
				result = CompareLog2PetriNetPlugin.measure(log, pn, parameters, BasicAutomata.notCanceller);
			}
		} else {
			//two models
			EfficientTree tree1 = loadTree(file1);
			EfficientTree tree2 = loadTree(file2);

			if (k < 0) {
				System.out.println("k\tfitness\tprecision");
				for (int i = 1; i <= -k; i++) {
					result = CompareProcessTree2ProcessTreePlugin.measure(tree1, tree2, new CompareParameters(i),
							BasicAutomata.notCanceller);
					System.out.println(i + "\t" + result.getRecall() + "\t" + result.getPrecision());
				}
				return;
			}
			CompareParameters parameters = new CompareParameters(k);
			parameters.setDebug(true);
			result = CompareProcessTree2ProcessTreePlugin.measure(tree1, tree2, parameters, BasicAutomata.notCanceller);

		}

		System.out.println(result.getRecallName() + " " + result.getRecall());
		System.out.println("precision " + result.getPrecision());
	}

	/**
	 * Load a .tree or a .ptml file.
	 * 
	 * @return an EfficientTree
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	public static EfficientTree loadTree(File file) throws FileNotFoundException, Exception {
		if (file.getName().endsWith(".tree")) {
			return EfficientTreeImportPlugin.importFromFile(file);
		} else if (file.getName().endsWith(".ptml")) {
			return ProcessTree2EfficientTree.convert((ProcessTree) (new PtmlImportTree()).importFile(null, file));
		}
		throw new Exception();
	}

	public static AcceptingPetriNet loadPN(File file) throws FileNotFoundException, Exception {

		//		Bpmn bpmn = (Bpmn) new BpmnImportPlugin().importFile(new FakeContext(), file);
		//		BPMNDiagram diagram = new BpmnSelectDiagramPlugin().selectDefault(new FakeContext(), bpmn);
		//		Object[] r = BPMNToPetriNetConverter.convert(diagram);
		//		return AcceptingPetriNetFactory.createAcceptingPetriNet((Petrinet) r[0], (Marking) r[1], (Marking) r[2]);

		AcceptingPetriNet net = AcceptingPetriNetFactory.createAcceptingPetriNet();
		net.importFromStream(new FakeContext(), new FileInputStream(file));
		return net;
	}
}

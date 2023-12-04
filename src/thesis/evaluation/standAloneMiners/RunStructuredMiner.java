package thesis.evaluation.standAloneMiners;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

import com.raffaeleconforti.context.FakePluginContext;
import com.raffaeleconforti.structuredminer.miner.StructuredMiner;
import com.raffaeleconforti.structuredminer.ui.SettingsStructuredMiner;

import au.edu.qut.structuring.ui.iBPStructUIResult;

public class RunStructuredMiner {

	public static void main(String[] args) throws Exception {
		boolean help = false;
		File file = null;
		if (args.length != 1) {
			help = true;
		} else {
			file = new File(args[0]);
			help = help || !file.exists();
		}

		if (help) {
			System.out.println("Usage: RunStructuredMiner.jar logfile");
			System.out.println(" Log should be in XES-format (compressed XES supported).");
			return;
		}
		BPMNDiagram bpmn = mine(file);

		//		Bpmn bpmn = (Bpmn) new BpmnImportPlugin().importFile(new FakeContext(), new File("d:\\test.bpmn"));
		//		BPMNDiagram diagram = new BpmnSelectDiagramPlugin().selectDefault(new FakeContext(), bpmn);
		//		Object[] r = BPMNToPetriNetConverter.convert(diagram);

		System.out.println(bpmn);
	}

	public static BPMNDiagram mine(File file) throws Exception {

		XLog log = RunInductiveMiner.loadLogNaive(file);

		SettingsStructuredMiner settingsStructuredMiner = new SettingsStructuredMiner(1);

		iBPStructUIResult settingStructuring = new iBPStructUIResult();
        settingStructuring.setForceStructuring(false);
        settingStructuring.setKeepBisimulation(true);
        settingStructuring.setTimeBounded(true);
        settingStructuring.setMaxMinutes(2);
        
		StructuredMiner structuredMiner = new StructuredMiner(new FakePluginContext(), log, settingsStructuredMiner,
				settingStructuring);
		BPMNDiagram diagram = structuredMiner.mine();

		return diagram;
	}

}

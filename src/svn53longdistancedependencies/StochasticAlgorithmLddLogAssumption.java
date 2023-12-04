package svn53longdistancedependencies;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.longdistancedependencies.LongDistanceDependenciesParametersAbstract;
import org.processmining.longdistancedependencies.LongDistanceDependenciesParametersDefault;
import org.processmining.longdistancedependencies.StochasticLabelledPetriNetAdjustmentWeightsEditable;
import org.processmining.longdistancedependencies.plugins.MineLongDistanceDependenciesPlugin;
import org.processmining.longdistancedependencies.plugins.StochasticLabelledPetriNetAdjustmentWeightsExportPlugin;

public class StochasticAlgorithmLddLogAssumption implements StochasticAlgorithm {

	public String getName() {
		return "LddLa";
	}

	public String getAbbreviation() {
		return "LddLa";
	}

	public String getLatexName() {
		return "LDD";
	}

	public String getFileExtension() {
		return ".slpna";
	}

	public void run(File logFile, XLog log, AcceptingPetriNet net, File modelFile) throws Exception {
		LongDistanceDependenciesParametersAbstract parameters = new LongDistanceDependenciesParametersDefault();
		parameters.setDebug(true);
		parameters.setPerformPostProcessing(true);
		parameters.setAlpha(1);
		StochasticLabelledPetriNetAdjustmentWeightsEditable result = MineLongDistanceDependenciesPlugin.mine(net, log,
				parameters, new ProMCanceller() {
					public boolean isCancelled() {
						return false;
					}
				});

		StochasticLabelledPetriNetAdjustmentWeightsExportPlugin.export(result, modelFile);
	}
}
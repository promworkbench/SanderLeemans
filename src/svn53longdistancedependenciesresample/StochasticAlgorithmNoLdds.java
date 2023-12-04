package svn53longdistancedependenciesresample;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.longdistancedependencies.LongDistanceDependenciesParametersAbstract;
import org.processmining.longdistancedependencies.LongDistanceDependenciesParametersDefault;
import org.processmining.longdistancedependencies.StochasticLabelledPetriNetAdjustmentWeightsEditable;
import org.processmining.longdistancedependencies.plugins.MineLongDistanceDependenciesPlugin;
import org.processmining.longdistancedependencies.plugins.StochasticLabelledPetriNetAdjustmentWeightsExportPlugin;

public class StochasticAlgorithmNoLdds implements StochasticAlgorithm {

	public String getName() {
		return "NoLdd";
	}

	public String getAbbreviation() {
		return "NoLdd";
	}

	public String getLatexName() {
		return "noLDD";
	}

	public String getFileExtension() {
		return ".slpna";
	}

	public void run(File logFile, XLog log, AcceptingPetriNet net, File modelFile) throws Exception {
		LongDistanceDependenciesParametersAbstract parameters = new LongDistanceDependenciesParametersDefault();
		parameters.setDebug(true);
		parameters.setPerformPostProcessing(false);
		parameters.setEnableLongDistanceDependencies(false);
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
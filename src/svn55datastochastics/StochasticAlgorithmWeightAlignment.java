package svn55datastochastics;

import java.io.File;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.statisticaltests.helperclasses.StochasticNet2StochasticLabelledPetriNet;
import org.processmining.stochasticlabelledpetrinets.StochasticLabelledPetriNetSimpleWeights;
import org.processmining.stochasticlabelledpetrinets.plugins.StochasticLabelledPetriNetExportPlugin;

import au.edu.qut.pm.spn_estimator.AlignmentEstimator;
import au.edu.qut.prom.helpers.StochasticPetriNetUtils;

public class StochasticAlgorithmWeightAlignment implements StochasticAlgorithm {

	public String getName() {
		return "alignment";
	}

	public String getAbbreviation() {
		return "alig";
	}

	public String getLatexName() {
		return "ABE";
	}

	public boolean createsDataModels() {
		return false;
	};

	public void run(File logFile, XLog log, AcceptingPetriNet anet, File modelFile) throws Exception {
		XEventClassifier classifier = new XEventNameClassifier();

		AlignmentEstimator estimator = new AlignmentEstimator();
		StochasticNet snet = estimator.estimateWeights(anet, log, classifier);

		Marking marking = StochasticPetriNetUtils.guessInitialMarking(snet);

		//store
		StochasticLabelledPetriNetSimpleWeights slnet = StochasticNet2StochasticLabelledPetriNet.convert(snet, marking);
		StochasticLabelledPetriNetExportPlugin.export(slnet, modelFile);
	}

	public String getFileExtension() {
		return ".slpn";
	}

}
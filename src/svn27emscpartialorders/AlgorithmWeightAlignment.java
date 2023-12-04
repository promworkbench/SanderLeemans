package svn27emscpartialorders;

import java.io.File;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2AcceptingPetriNet;
import org.processmining.plugins.inductiveminer2.mining.InductiveMiner;
import org.processmining.plugins.inductiveminer2.mining.MiningParametersAbstract;
import org.processmining.plugins.inductiveminer2.variants.MiningParametersIMInfrequent;
import org.processmining.plugins.pnml.exporting.StochasticNetToPNMLConverter;
import org.processmining.plugins.pnml.simple.PNMLRoot;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import au.edu.qut.pm.spn_estimator.AlignmentEstimator;
import au.edu.qut.prom.helpers.StochasticPetriNetUtils;
import caise2020isextension.FakeGraphLayoutConnection;

public class AlgorithmWeightAlignment implements Algorithm {

	public String getName() {
		return "IMf+Adam Alignment";
	}

	public String getAbbreviation() {
		return "IMAdA";
	}

	public String getLatexName() {
		return "IMfAE";
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {
		XEventClassifier classifier = new XEventNameClassifier();

		MiningParametersAbstract parameters = new MiningParametersIMInfrequent();
		Canceller canceller = new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		};
		parameters.setDebug(false);
		EfficientTree tree = InductiveMiner.mineEfficientTree(parameters.getIMLog(log), parameters, canceller);
		AcceptingPetriNet anet = EfficientTree2AcceptingPetriNet.convert(tree);

		AlignmentEstimator estimator = new AlignmentEstimator();
		StochasticNet snet = estimator.estimateWeights(anet, log, classifier);

		Marking marking = StochasticPetriNetUtils.guessInitialMarking(snet);
		//store
		PNMLRoot root = new StochasticNetToPNMLConverter().convertNet(snet, marking,
				new FakeGraphLayoutConnection(snet));
		Serializer serializer = new Persister();
		serializer.write(root, modelFile);
	}

}
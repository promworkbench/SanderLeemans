package svn44systemprecision;

import java.io.File;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.directlyfollowsmodelminer.mining.DFMMiningParametersAbstract;
import org.processmining.directlyfollowsmodelminer.mining.plugins.DirectlyFollowsModelMinerPlugin;
import org.processmining.directlyfollowsmodelminer.mining.variants.DFMMiningParametersDefault;
import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel;
import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel2AcceptingPetriNet;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.exporting.StochasticNetToPNMLConverter;
import org.processmining.plugins.pnml.simple.PNMLRoot;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import au.edu.qut.pm.spn_estimator.AbstractFrequencyEstimator;
import au.edu.qut.prom.helpers.StochasticPetriNetUtils;
import caise2020isextension.FakeGraphLayoutConnection;

public abstract class AlgorithmWeightEstimator {
	public void run(File logFile, XLog log, File modelFile, float noise) throws Exception {
		XEventClassifier classifier = new XEventNameClassifier();

		//		MiningParametersAbstract parameters = new MiningParametersIMInfrequent();
		//		parameters.setNoiseThreshold(noise);
		//		Canceller canceller = new Canceller() {
		//			public boolean isCancelled() {
		//				return false;
		//			}
		//		};
		//		parameters.setDebug(false);
		//		EfficientTree tree = InductiveMiner.mineEfficientTree(parameters.getIMLog(log), parameters, canceller);
		//		AcceptingPetriNet anet = EfficientTree2AcceptingPetriNet.convert(tree);

		DFMMiningParametersAbstract parameters = new DFMMiningParametersDefault();
		parameters.setNoiseThreshold(noise);
		Canceller canceller = new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		};
		DirectlyFollowsModel dfm = DirectlyFollowsModelMinerPlugin.mine(log, parameters, canceller);
		AcceptingPetriNet anet = DirectlyFollowsModel2AcceptingPetriNet.convert(dfm);

		AbstractFrequencyEstimator estimator = getEstimator();
		StochasticNet snet = estimator.estimateWeights(anet, log, classifier);

		Marking marking = StochasticPetriNetUtils.guessInitialMarking(snet);
		//store
		PNMLRoot root = new StochasticNetToPNMLConverter().convertNet(snet, marking,
				new FakeGraphLayoutConnection(snet));
		Serializer serializer = new Persister();
		serializer.write(root, modelFile);
	}

	public abstract AbstractFrequencyEstimator getEstimator();
}

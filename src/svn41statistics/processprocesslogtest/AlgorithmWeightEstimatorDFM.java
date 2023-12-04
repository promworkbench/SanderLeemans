package svn41statistics.processprocesslogtest;

import java.io.File;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.directlyfollowsmodelminer.mining.DFMMiner;
import org.processmining.directlyfollowsmodelminer.mining.DFMMiningParametersAbstract;
import org.processmining.directlyfollowsmodelminer.mining.variants.DFMMiningParametersDefault;
import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel;
import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel2AcceptingPetriNet;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.inductiveminer2.logs.IMLogImpl;
import org.processmining.plugins.pnml.exporting.StochasticNetToPNMLConverter;
import org.processmining.plugins.pnml.simple.PNMLRoot;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import au.edu.qut.pm.spn_estimator.AbstractFrequencyEstimator;
import au.edu.qut.prom.helpers.StochasticPetriNetUtils;
import caise2020isextension.FakeGraphLayoutConnection;

public abstract class AlgorithmWeightEstimatorDFM implements Algorithm {

	private double noise;

	public AlgorithmWeightEstimatorDFM(double noise) {
		this.noise = noise;
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {
		XEventClassifier classifier = new XEventNameClassifier();

		DFMMiningParametersAbstract miningParameters = new DFMMiningParametersDefault();
		Canceller canceller2 = new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		};
		miningParameters.setNoiseThreshold(noise);

		IMLogImpl log2 = new org.processmining.plugins.inductiveminer2.logs.IMLogImpl(log,
				miningParameters.getClassifier(), miningParameters.getLifeCycleClassifier());
		DirectlyFollowsModel dfg = DFMMiner.mine(log2, miningParameters, canceller2);

		AcceptingPetriNet anet = DirectlyFollowsModel2AcceptingPetriNet.convert(dfg);

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

	public String getFileExtension() {
		return ".pnml";
	}
}

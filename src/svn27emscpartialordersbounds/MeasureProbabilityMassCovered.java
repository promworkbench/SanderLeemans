package svn27emscpartialordersbounds;

import org.deckfour.xes.model.XLog;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.TransitionMap;
import org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder.LanguageGenerationStrategyFromModelPartialOrderImpl;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.PartialOrder;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.StochasticLanguageUtils;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPathLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.StochasticPetrinet2StochasticLanguagePartialOrder;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedAutomatonException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedLogException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedPetriNetException;

import nl.tue.astar.AStarException;

public class MeasureProbabilityMassCovered implements Measure {

	public String getTitle() {
		return "probabilityCovered";
	}

	public String getLatexTitle() {
		return "probability covered";
	}

	public String[] getMeasureNames() {
		return new String[] { "massCovered" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "mass covered" };
	}

	public int getNumberOfMeasures() {
		return 1;
	}

	public boolean printTime() {
		return false;
	}

	public double[] compute(XLog log, StochasticNet model, Marking initialMarking, Call call, int q, int r)
			throws ProjectedMeasuresFailedException, AutomatonFailedException, InterruptedException, AStarException,
			UnsupportedLogException, IllegalTransitionException, UnsupportedPetriNetException,
			CloneNotSupportedException, UnsupportedAutomatonException, Exception {

		LanguageGenerationStrategyFromModelPartialOrderImpl generation = new LanguageGenerationStrategyFromModelPartialOrderImpl();
		generation.setNumberOfTracesWithHighestProbability(q);
		generation.setNumberOfTracesRandomWalk(r);
		generation.setSeed(1);

		Activity2IndexKey activityKey = new Activity2IndexKey();
		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};
		Pair<StochasticPathLanguage<PartialOrder>, TransitionMap> p = StochasticPetrinet2StochasticLanguagePartialOrder
				.convert(model, initialMarking, activityKey, generation, canceller);

		return new double[] { StochasticLanguageUtils.getSumProbability(p.getA()) };
	}

}

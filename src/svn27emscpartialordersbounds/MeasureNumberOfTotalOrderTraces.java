package svn27emscpartialordersbounds;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;

import org.deckfour.xes.model.XLog;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.SymbolicNumber;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.TransitionMap;
import org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder.EMSCParametersLogTotalModelPartialCertainDefault;
import org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder.LanguageGenerationStrategyFromModelPartialOrder;
import org.processmining.earthmoversstochasticconformancechecking.parameters.partialorder.LanguageGenerationStrategyFromModelPartialOrderImpl;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.PartialOrder;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPathIterator;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPathLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.PartialOrder2TotalOrders;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.PartialOrderCountTotalOrders;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.PartialOrderUtils;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.partialorder.StochasticPetrinet2StochasticLanguagePartialOrder;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.mining.cuts.Cut.Operator;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.withoutlog.cutfinders.CutFinderWithoutLog;
import org.processmining.plugins.inductiveminer2.withoutlog.cutfinders.CutFinderWithoutLogIMExclusiveChoice;
import org.processmining.plugins.inductiveminer2.withoutlog.cutfinders.CutFinderWithoutLogIMSequence;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsd;
import org.processmining.plugins.inductiveminer2.withoutlog.dfgmsd.DfgMsdImpl;
import org.processmining.plugins.inductiveminer2.withoutlog.graphsplitters.SimpleDfgMsdSplitter;
import org.processmining.projectedrecallandprecision.helperclasses.AutomatonFailedException;
import org.processmining.projectedrecallandprecision.result.ProjectedRecallPrecisionResult.ProjectedMeasuresFailedException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedAutomatonException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedLogException;
import org.processmining.stochasticawareconformancechecking.helperclasses.UnsupportedPetriNetException;

import com.google.common.math.BigIntegerMath;

import nl.tue.astar.AStarException;

public class MeasureNumberOfTotalOrderTraces implements Measure {

	public String getTitle() {
		return "notot";
	}

	public String getLatexTitle() {
		return "number of totally ordered traces";
	}

	public String[] getMeasureNames() {
		return new String[] { "notot" };
	}

	public String[] getMeasureLatexNames() {
		return new String[] { "number of totally ordered traces" };
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
		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};
		LanguageGenerationStrategyFromModelPartialOrderImpl generationStrategy = EMSCParametersLogTotalModelPartialCertainDefault.defaultGenerationStrategy
				.clone();
		generationStrategy.setNumberOfTracesWithHighestProbability(q);
		generationStrategy.setNumberOfTracesRandomWalk(r);
		generationStrategy.setSeed(1);

		return new double[] { compute(model, initialMarking, generationStrategy, canceller) };
	}

	public static double compute(StochasticNet model, Marking initialMarking,
			LanguageGenerationStrategyFromModelPartialOrder generationStrategy, ProMCanceller canceller) {
		Activity2IndexKey activityKey = new Activity2IndexKey();
		activityKey.feed(model);

		Pair<StochasticPathLanguage<PartialOrder>, TransitionMap> p = StochasticPetrinet2StochasticLanguagePartialOrder
				.convert(model, initialMarking, activityKey, generationStrategy, canceller);
		StochasticPathLanguage<PartialOrder> language = p.getA();

		BigInteger result = BigInteger.ZERO;
		SymbolicNumber resultUncomputed = new SymbolicNumber(BigInteger.ZERO);
		int poCount = 0;
		for (StochasticPathIterator<PartialOrder> it = language.iterator(); it.hasNext();) {
			int[] partialOrder = it.next();
			//int[] partialOrderPath = it.getPath();

			//			System.out.println("po path  " + Arrays.toString(partialOrderPath));
//			System.out.println("po trace " + PartialOrder2String.toString(partialOrder));

			SymbolicNumber traceResult = PartialOrderCountTotalOrders.count(partialOrder, 5, canceller);
//			System.out.println("  symbolic recursive method " + traceResult);

//			traceResult = count(partialOrder, canceller);
//			System.out.println("  old method " + traceResult);

//			long x = countExhaustive(partialOrder, canceller);
//			System.out.println("  exhaustive method " + x);

			if (traceResult.isNumber()) {
				result = result.add(traceResult.bigIntegerValue());
			} else {
				resultUncomputed = resultUncomputed.add(traceResult);
			}

			//System.out.println(PartialOrder2String.toString(partialOrder));
			System.out.println(" trace    " + poCount + ": " + traceResult);
			//System.out.println(" subtotal " + result);
			//System.out.println(" brute force  " + countBruteForce(partialOrder, canceller));

			//			if (poCount % 10 == 0) {
			//				System.out.println(" pos: " + poCount + "/" + language.size());
			//			}
			poCount++;

		}

		System.out.println(resultUncomputed.add(new SymbolicNumber(result)));
		return result.longValue();
	}

	private static final long multiplier = 0x5DEECE66DL;
	private static final long addend = 0xBL;
	private static final long mask = (1L << 48) - 1;

	public int hashCode() {
		int stateA = 0;
		BitSet stateB = new BitSet();

		long seed = (stateB.hashCode() ^ multiplier) & mask;

		//int r = next(31);
		seed = (seed * multiplier + addend) & mask;
		int r = (int) (seed >>> (48 - 31));

		int m = stateA - 1;
		if ((stateA & m) == 0) {
			// i.e., bound is a power of 2
			r = (int) ((stateA * (long) r) >> 31);
		} else {
			for (int u = r; u - (r = u % stateA) + m < 0;) {
				seed = (seed * multiplier + addend) & mask;
				u = (int) (seed >>> (48 - 31));
			}
		}
		return r;
	}

	public static long countExhaustive(int[] partialOrder, ProMCanceller canceller) {
		long result = 0;
		Iterator<int[]> it = PartialOrder2TotalOrders.getTotalOrders(partialOrder, canceller).iterator();
		while (it.hasNext()) {
			it.next();
			result++;
		}
		return result;
	}

	public static SymbolicNumber count(int[] partialOrder, ProMCanceller canceller) {
		if (PartialOrderUtils.isTotalOrder(partialOrder)) {
			return new SymbolicNumber(BigInteger.ONE);
		}

		//create dfg
		DfgMsd dfg;
		{
			String[] activities = new String[PartialOrderUtils.getNumberOfEvents(partialOrder)];
			for (int activityIndex = 0; activityIndex < activities.length; activityIndex++) {
				activities[activityIndex] = "" + activityIndex;
			}
			dfg = new DfgMsdImpl(activities);
			//nodes
			for (int activityIndex = 0; activityIndex < activities.length; activityIndex++) {
				dfg.addActivity(activityIndex);
			}
			//edges
			for (int activityIndex = 0; activityIndex < activities.length; activityIndex++) {
				for (int edgeIndex = 0; edgeIndex < PartialOrderUtils.getNumberOfIncomingEdges(partialOrder,
						activityIndex); edgeIndex++) {
					int source = PartialOrderUtils.getIncomingEdgeSourceEventIndex(partialOrder, activityIndex,
							edgeIndex);
					dfg.getDirectlyFollowsGraph().addEdge(source, activityIndex, 1);
				}
			}
		}

		return count(dfg, canceller);
	}

	public static SymbolicNumber count(DfgMsd dfg, ProMCanceller canceller) {
		//try the sequence cut
		ArrayList<CutFinderWithoutLog> cutFinders = new ArrayList<CutFinderWithoutLog>();
		cutFinders.add(new CutFinderWithoutLogIMSequence());
		cutFinders.add(new CutFinderWithoutLogIMExclusiveChoice());

		Cut cut = findCut(dfg, cutFinders);
		StringBuilder debug = new StringBuilder();
		debug.append("  cut: " + cut + "\n");
		if (cut == null) {
			SymbolicNumber result = countBaseCase(dfg, canceller);
			debug.append("  result of call " + result + "\n");
			//			System.out.println(debug);
			return result;
		} else {
			//			System.out.println("  cut " + cut);

			DfgMsd[] subDfgs = SimpleDfgMsdSplitter.split(dfg, cut.getPartition(), cut.getOperator());
			if (cut.getOperator().equals(Operator.sequence)) {
				SymbolicNumber result = new SymbolicNumber(BigInteger.ONE);
				for (DfgMsd subDfg : subDfgs) {
					SymbolicNumber subResult = count(subDfg, canceller);
					//debug.append("  subresult " + subResult + "\n");
					result = result.multiply(subResult);
				}
				debug.append("  result of call " + result + "\n");
				//				System.out.println(debug);
				return result;
			} else if (cut.getOperator().equals(Operator.xor)) {
				//https://math.stackexchange.com/questions/987514/counting-permutations-that-respect-a-partial-order
				//(a+b+c)! / a!b!c!
				SymbolicNumber sum = new SymbolicNumber(BigInteger.ZERO);
				SymbolicNumber product = new SymbolicNumber(BigInteger.ONE);

				for (int i = 0; i < subDfgs.length; i++) {
					DfgMsd subDfg = subDfgs[i];
					SymbolicNumber subResult = count(subDfg, canceller)
							.multiply(new SymbolicNumber(BigInteger.valueOf(cut.getPartition().get(i).size())));
					debug.append("  subresult " + subResult + "\n");
					sum = sum.add(subResult);
					product = product.multiply(subResult.factorial());
				}

				SymbolicNumber result = sum.factorial().divide(product);
				debug.append("  result of call " + result + "\n");
				//System.out.println(debug);
				return result;
			}
		}

		return countBaseCase(dfg, canceller);
	}

	public static SymbolicNumber countBaseCase(DfgMsd dfg, ProMCanceller canceller) {
		if (dfg.getNumberOfActivities() == 1) {
			return new SymbolicNumber(BigInteger.ONE);
		}

		BigInteger result = BigInteger.ZERO;

		ArrayDeque<BitSet> queue = new ArrayDeque<>();
		queue.add(new BitSet(dfg.getNumberOfActivities()));

		while (!queue.isEmpty()) {
			BitSet state = queue.poll();

			boolean somethingEnabled = false;
			for (int eventIndex = dfg
					.getNumberOfActivities(); (eventIndex = state.previousClearBit(eventIndex - 1)) >= 0;) {
				boolean allIncomingEdgesExecuted = true;
				for (long edgeIndex : dfg.getDirectlyFollowsGraph().getIncomingEdgesOf(eventIndex)) {
					int sourceEventIndex = dfg.getDirectlyFollowsGraph().getEdgeSource(edgeIndex);

					if (!state.get(sourceEventIndex)) {
						allIncomingEdgesExecuted = false;
						break;
					}
				}

				if (allIncomingEdgesExecuted) {
					BitSet newState = (BitSet) state.clone();
					newState.set(eventIndex);
					queue.addFirst(newState);

					somethingEnabled = true;
				}
			}

			if (!somethingEnabled) {
				result = result.add(BigInteger.ONE);
			}
		}

		System.out.println(" base case " + dfg.getNumberOfActivities() + " result " + result);
		return new SymbolicNumber(result);
	}

	//https://stackoverflow.com/questions/8992437/stackoverflowerror-computing-factorial-of-a-biginteger
	public static BigInteger factorial(BigInteger n) {
		int i = n.intValue();
		if (n.equals(BigInteger.valueOf(i))) {
			return BigIntegerMath.factorial(i);
		}
		//		BigInteger result = BigInteger.ONE;
		//
		//		while (!n.equals(BigInteger.ZERO)) {
		//			result = result.multiply(n);
		//			n = n.subtract(BigInteger.ONE);
		//		}
		//
		//		return result;
		return fac6(n);
	}

	//https://stackoverflow.com/questions/51445285/quick-way-to-find-a-factorial-of-a-large-number
	private static final BigInteger SIMPLE_THRESHOLD = BigInteger.TEN;

	private static BigInteger fac6(BigInteger n) {
		return subfac(BigInteger.ONE, n);
	}

	/**
	 * compute a * (a+1) * ... *(b-1) * b The interval [a,b] includes the
	 * endpoints a and b.
	 *
	 * @param a
	 *            the interval start.
	 * @param b
	 *            the interval end, inclusive.
	 * @return the product.
	 */
	private static BigInteger subfac(BigInteger a, BigInteger b) {
		if ((b.subtract(a).compareTo(SIMPLE_THRESHOLD) < 0)) {
			BigInteger result = BigInteger.ONE;
			for (BigInteger i = a; i.compareTo(b) <= 0; i = i.add(BigInteger.ONE)) {
				result = result.multiply(i);
			}
			return result;
		} else {
			BigInteger mid = a.add(b.subtract(a).divide(BigInteger.valueOf(2)));
			return subfac(a, mid).multiply(subfac(mid.add(BigInteger.ONE), b));
		}

	}

	public static Cut findCut(DfgMsd graph, Iterable<CutFinderWithoutLog> cutFinders) {
		for (CutFinderWithoutLog cutFinder : cutFinders) {
			Cut cut = cutFinder.findCut(graph, null);

			if (cut != null && cut.isValid()) {
				return cut;
			}
		}
		return null;
	}
}
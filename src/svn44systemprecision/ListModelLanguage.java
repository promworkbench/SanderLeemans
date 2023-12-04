package svn44systemprecision;

import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.ArrayUtils;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.EfficientStochasticPetriNetSemantics;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.EfficientStochasticPetriNetSemanticsImpl;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.PrefixProbabilityMarking;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.QueueCombination;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.Transition2LabelMap;
import org.processmining.earthmoversstochasticconformancechecking.helperclasses.TransitionMap;
import org.processmining.earthmoversstochasticconformancechecking.parameters.LanguageGenerationStrategyFromModel;
import org.processmining.earthmoversstochasticconformancechecking.plugins.EarthMoversStochasticConformancePlugin;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.Activity2IndexKey;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.TotalOrder;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPathIterator;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPathLanguage;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticPathLanguageImpl;
import org.processmining.earthmoversstochasticconformancechecking.stochasticlanguage.model.StochasticTransition2IndexKey;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Pair;

import com.google.common.util.concurrent.AtomicDouble;

import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.custom_hash.TObjectDoubleCustomHashMap;
import gnu.trove.procedure.TObjectDoubleProcedure;
import gnu.trove.strategy.HashingStrategy;

public class ListModelLanguage {
	public static String convert(StochasticNet net, int maxVisibleTraceLength) throws InterruptedException {
		Activity2IndexKey activityKey = new Activity2IndexKey();
		activityKey.feed(net);
		Marking initialMarking = EarthMoversStochasticConformancePlugin.getInitialMarking(net);
		ProMCanceller canceller = new ProMCanceller() {
			public boolean isCancelled() {
				return false;
			}
		};
		LanguageGenerationStrategyFromModel terminationStrategy = new LanguageGenerationStrategyFromModel() {
			public boolean isTerminated(double massCovered) {
				return false;
			}

			public void initialise() {
			}

			public LanguageGenerationStrategyFromModel clone() {
				try {
					return (LanguageGenerationStrategyFromModel) super.clone();
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		Pair<StochasticPathLanguage<TotalOrder>, Transition2LabelMap> p = convert(net, initialMarking,
				terminationStrategy, activityKey, canceller, maxVisibleTraceLength);

		StochasticPathLanguage<TotalOrder> language = p.getA();

		TObjectDoubleMap<int[]> result = new TObjectDoubleCustomHashMap<>(new HashingStrategy<int[]>() {
			private static final long serialVersionUID = 1L;

			public int computeHashCode(int[] object) {
				return Arrays.hashCode(object);
			}

			public boolean equals(int[] o1, int[] o2) {
				return Arrays.equals(o1, o2);
			}
		});
		for (StochasticPathIterator<TotalOrder> it = language.iterator(); it.hasNext();) {
			int[] indexTrace = it.next();
			result.adjustOrPutValue(indexTrace, it.getProbability(), it.getProbability());
		}

		final StringBuilder s = new StringBuilder();
		result.forEachEntry(new TObjectDoubleProcedure<int[]>() {
			public boolean execute(int[] a, double b) {
				s.append(b);
				s.append("\n");
				s.append(ArrayUtils.toString(activityKey.toTraceString(a)));
				s.append("\n");
				return true;
			}
		});
		return s.toString();
	}

	/**
	 * Assumption: the deadlock markings are equal to the final markings.
	 * 
	 * @param net
	 * @param initialMarking
	 * @param terminationStrategy
	 * @param canceller
	 * @param maxVisibleTraceLength
	 * @return
	 * @throws IllegalTransitionException
	 * @throws InterruptedException
	 */
	public static Pair<StochasticPathLanguage<TotalOrder>, Transition2LabelMap> convert(StochasticNet net,
			Marking initialMarking, LanguageGenerationStrategyFromModel terminationStrategy,
			Activity2IndexKey activityKey, ProMCanceller canceller, int maxVisibleTraceLength)
			throws InterruptedException {

		//initialise
		EfficientStochasticPetriNetSemanticsImpl semantics = new EfficientStochasticPetriNetSemanticsImpl(net,
				initialMarking);
		Transition2LabelMap transitionMap = new Transition2LabelMap(semantics, net.getTransitions().size());
		StochasticTransition2IndexKey transitionKey = new StochasticTransition2IndexKey(semantics, activityKey);
		StochasticPathLanguageImpl<TotalOrder> language = new StochasticPathLanguageImpl<>(transitionKey, activityKey);

		//set to work
		walk(language, semantics, transitionMap, initialMarking, terminationStrategy, canceller, maxVisibleTraceLength);

		if (canceller.isCancelled()) {
			return null;
		}

		return Pair.of(language, transitionMap);
	}

	public static double walk(final StochasticPathLanguageImpl<TotalOrder> language,
			final EfficientStochasticPetriNetSemantics semantics, final TransitionMap transitionMap,
			Marking initialMarking, LanguageGenerationStrategyFromModel terminationStrategy,
			final ProMCanceller canceller, final int maxVisibleTraceLength) throws InterruptedException {
		Thread[] threads = new Thread[Math.max(1, Runtime.getRuntime().availableProcessors() - 1)];

		//initialise queues
		final ConcurrentLinkedQueue<byte[]> queue = new ConcurrentLinkedQueue<>();
		final AtomicInteger queueSize = new AtomicInteger(1);
		final AtomicInteger activeThreads = new AtomicInteger(threads.length);

		//add the first step to the queues
		queue.add(PrefixProbabilityMarking.pack(new int[0], 1.0, semantics.convert(initialMarking)));

		final AtomicDouble massCovered = new AtomicDouble(0);
		final LanguageGenerationStrategyFromModel terminationStrategy2 = terminationStrategy.clone();
		terminationStrategy2.initialise();

		for (int thread = 0; thread < threads.length; thread++) {
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						thread(semantics.clone(), transitionMap, queue, queueSize, massCovered, terminationStrategy2,
								language, canceller, maxVisibleTraceLength, activeThreads);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			threads[thread] = new Thread(runnable, "stochastic language thread " + thread);
			threads[thread].start();
		}

		for (int thread = 0; thread < threads.length; thread++) {
			threads[thread].join();
			threads[thread] = null;
		}

		if (canceller.isCancelled()) {
			return -Double.MAX_VALUE;
		}

		return massCovered.get();
	}

	public static double getTotalMass(EfficientStochasticPetriNetSemantics semantics, int[] enabledTransitions) {
		double sum = 0;
		for (int i : enabledTransitions) {
			sum += semantics.getTransitionWeight(i);
		}
		return sum;
	}

	public static double getTotalMass(EfficientStochasticPetriNetSemantics semantics, BitSet enabledTransitions) {
		double sum = 0;
		for (int transition = enabledTransitions.nextSetBit(0); transition >= 0; transition = enabledTransitions
				.nextSetBit(transition + 1)) {
			sum += semantics.getTransitionWeight(transition);
			if (transition == Integer.MAX_VALUE) {
				break;
			}
		}
		return sum;
	}

	public static void thread(EfficientStochasticPetriNetSemantics semantics, TransitionMap transitionMap,
			ConcurrentLinkedQueue<byte[]> globalQueue, AtomicInteger globalQueueSize, AtomicDouble massCovered,
			LanguageGenerationStrategyFromModel terminationStrategy, StochasticPathLanguageImpl<TotalOrder> language,
			ProMCanceller canceller, int maxVisibleTraceLength, AtomicInteger threadsActive)
			throws InterruptedException {
		final int markingLength = semantics.getState().length;

		final QueueCombination queue = new QueueCombination(globalQueue, globalQueueSize);
		//ConcurrentLinkedQueue<byte[]> queue = globalQueue;

		byte[] prefixProbabilityMarking;
		while (true) {

			if (canceller.isCancelled()) {
				return;
			}

			prefixProbabilityMarking = queue.poll();
			if (prefixProbabilityMarking == null) {
				if (terminationStrategy.isTerminated(massCovered.get())) {
					return;
				} else {
					int active = threadsActive.decrementAndGet();
					if (active <= 0) {
						return;
					}
					Thread.sleep(10);
					threadsActive.incrementAndGet();
					continue;
				}
			}

			int[] prefix = PrefixProbabilityMarking.getPrefix(prefixProbabilityMarking, markingLength);
			double prefixProbability = PrefixProbabilityMarking.getProbability(prefixProbabilityMarking);
			byte[] marking = PrefixProbabilityMarking.getMarking(prefixProbabilityMarking, markingLength);

			semantics.setState(marking);
			int[] enabledTransitions = semantics.getEnabledTransitions();
			double totalTransitionsMass = getTotalMass(semantics, enabledTransitions);

			if (enabledTransitions.length == 0) {
				//we are in a deadlock state. by assumption, that's a final state.

				//accept the current trace and add it to the log
				synchronized (language) {
					if (terminationStrategy.isTerminated(massCovered.get())) {
						threadsActive.decrementAndGet();
						return;
					}
					language.add(prefix2path(semantics, transitionMap, prefix), prefixProbability);
					if (terminationStrategy.isTerminated(massCovered.addAndGet(prefixProbability))) {
						threadsActive.decrementAndGet();
						return;
					}
				}

				//System.out.println(prefix + " accepted, total " + massCovered);

				//				System.out.println(prefixProbability + " " + Arrays.toString(prefix) + " total " + massCovered);

			} else {
				//we are not in a deadlock state; continue
				for (int transition : enabledTransitions) {
					int currentLength = getPrefixVisibleLength(semantics, prefix);
					if (currentLength < maxVisibleTraceLength
							|| (currentLength <= maxVisibleTraceLength && semantics.isInvisible(transition))) {
						semantics.setState(marking);
						semantics.executeTransition(transition);
						byte[] newMarking = semantics.getState();

						int[] newPrefix = ArrayUtils.add(prefix, transition);

						//compute the new probability
						double newProbability = prefixProbability * semantics.getTransitionWeight(transition)
								/ totalTransitionsMass;

						queue.add(PrefixProbabilityMarking.pack(newPrefix, newProbability, newMarking));
					}
				}
			}
		}
	}

	private static int getPrefixVisibleLength(EfficientStochasticPetriNetSemantics semantics, int[] prefix) {
		int result = 0;
		for (int transition : prefix) {
			if (!semantics.isInvisible(transition)) {
				result++;
			}
		}
		return result;
	}

	private static int[] prefix2path(EfficientStochasticPetriNetSemantics semantics, TransitionMap transitionMap,
			int[] prefix) {
		int[] result = new int[prefix.length];
		System.arraycopy(prefix, 0, result, 0, prefix.length);
		return result;
	}
}

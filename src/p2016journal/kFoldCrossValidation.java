package p2016journal;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.Triple;

public abstract class kFoldCrossValidation<M> {
	/**
	 * Discover a model from a log. Must be thread-safe.
	 * 
	 * @param log
	 * @return
	 */
	public abstract M discover(XLog log);

	public abstract Triple<Double, Double, Double> evaluate(XLog testLog, M model, final XEventClassifier classifier)
			throws Exception;

	public kFoldCrossValidationResult run(final XLog log, final XEventClassifier classifier, int k, long seed)
			throws Exception {
		final kFoldCrossValidationResult result = new kFoldCrossValidationResult(k);
		final AtomicBoolean error = new AtomicBoolean(false);

		final int[] traceInBucket = divideTraces(log, k, seed);

		ExecutorService computationPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		for (int bucketNr = 0; bucketNr < k; bucketNr++) {
			final int bucketNr2 = bucketNr;
			computationPool.submit(new Runnable() {

				public void run() {
					//make new sub-logs
					Pair<XLog, XLog> logs = splitLog(log, traceInBucket, bucketNr2);

					//discover a model from the training log
					M model = discover(logs.getA());

					//measure the quality of the model
					Triple<Double, Double, Double> fitnessPrecisionSimplicity;
					try {
						fitnessPrecisionSimplicity = evaluate(logs.getB(), model, classifier);
					} catch (Exception e) {
						e.printStackTrace();
						error.set(true);
						return;
					}
					result.set(fitnessPrecisionSimplicity, bucketNr2);
				}
			});
		}

		computationPool.shutdown();
		computationPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

		if (!error.get()) {
			return result;
		} else {
			return null;
		}
	}

	private int[] divideTraces(XLog log, int k, long seed) {
		//divide traces over buckets
		int[] traceInBucket = new int[log.size()];
		{
			Random random = new Random(seed);
			for (int i = 0; i < log.size(); i++) {
				traceInBucket[i] = random.nextInt(k);
			}
		}
		return traceInBucket;
	}

	private Pair<XLog, XLog> splitLog(XLog log, int[] traceInBucket, int bucketNr) {
		XLog trainingLog = new XLogImpl(log.getAttributes());
		XLog testLog = new XLogImpl(log.getAttributes());

		int i = 0;
		for (XTrace trace : log) {
			if (traceInBucket[i] == bucketNr) {
				testLog.add(trace);
			} else {
				trainingLog.add(trace);
			}
			i++;
		}

		return Pair.of(trainingLog, testLog);
	}
}

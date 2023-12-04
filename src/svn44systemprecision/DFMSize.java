package svn44systemprecision;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel;

public class DFMSize {
	/**
	 * Assumption: the DFM is deterministic, i.e. there is no reachable state
	 * where you can do two transitions with the same name.
	 * 
	 * @param dfm
	 * @param maxTraceLength
	 * @return
	 * @throws InterruptedException
	 */
	public static long getNumberOfTracesInDFM(DirectlyFollowsModel dfm, int maxTraceLength)
			throws InterruptedException {
		DirectlyFollowsModelSemantics semantics = new DirectlyFollowsModelSemanticsImpl(dfm);

		Thread[] threads = new Thread[Math.max(1, Runtime.getRuntime().availableProcessors() - 1)];

		//initialise queues
		final ConcurrentLinkedQueue<Long> queue = new ConcurrentLinkedQueue<>();
		final AtomicInteger activeThreads = new AtomicInteger(threads.length);
		final AtomicLong numberOfTraces = new AtomicLong(0);

		//add the first step to the queues
		queue.add(pack(semantics.getInitialState(), 0));

		for (int thread = 0; thread < threads.length; thread++) {
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						thread(semantics.clone(), queue, numberOfTraces, activeThreads, maxTraceLength);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			threads[thread] = new Thread(runnable, "DFM size thread " + thread);
			threads[thread].start();
		}

		for (int thread = 0; thread < threads.length; thread++) {
			threads[thread].join();
			threads[thread] = null;
		}

		return numberOfTraces.get();
	}

	private static void thread(DirectlyFollowsModelSemantics semantics, ConcurrentLinkedQueue<Long> queue,
			AtomicLong numberOfTraces, AtomicInteger threadsActive, int maxTraceLength) throws InterruptedException {
		while (true) {
			Long pack = queue.poll();
			if (pack == null) {
				int active = threadsActive.decrementAndGet();
				if (active <= 0) {
					return;
				}
				Thread.sleep(10);
				threadsActive.incrementAndGet();
				continue;
			}

			int state = getState(pack);
			int traceLength = getTraceLength(pack);

			semantics.setState(state);
			if (semantics.isFinalState()) {
				numberOfTraces.incrementAndGet();
			} else if (traceLength < maxTraceLength) {
				int[] enabledTransitions = semantics.getEnabledTransitions();
				for (int transition : enabledTransitions) {
					semantics.setState(state);
					semantics.executeTransition(transition);
					queue.add(pack(semantics.getState(), traceLength + 1));
				}
			}
		}
	}

	public static long pack(int state, int traceLength) {
		return (((long) state) << 32) | (traceLength & 0xffffffffL);
	}

	public static int getState(long pack) {
		return (int) (pack >> 32);
	}

	public static int getTraceLength(long pack) {
		return (int) pack;
	}
}
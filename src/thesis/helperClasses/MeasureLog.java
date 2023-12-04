package thesis.helperClasses;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

import org.deckfour.xes.model.XEvent;
import org.processmining.plugins.InductiveMiner.Function;

public class MeasureLog {
	public static void main(String[] args) throws Exception {
		int maxRound = 9;
		int maxTreeSeed = 10;

		for (int treeSeed = 1; treeSeed <= maxTreeSeed; treeSeed++) {

			for (int round = 9; round <= maxRound; round++) {

				File file = new File("D:\\svn\\00 - the beast\\experiments\\scalability\\data\\round " + round
						+ " treeSeed " + treeSeed + ".xes.gz");

				final AtomicLong events = new AtomicLong();
				final AtomicLong traces = new AtomicLong();
				XLogParserIncremental.parseEvents(file, new Function<XEvent, Object>() {
					public Object call(XEvent input) throws Exception {
						events.incrementAndGet();
						return null;
					}
				}, new Runnable() {
					public void run() {
						traces.incrementAndGet();
					}
				});
				System.out.println(events);
			}
			System.out.println("===");
		}
	}
}

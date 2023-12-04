import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

import org.deckfour.xes.model.XEvent;
import org.processmining.plugins.InductiveMiner.Function;

import thesis.helperClasses.XLogParserIncremental;

public class CountEvents {

	public static void main(String... args) throws Exception {
		XLogParserIncremental reader = new XLogParserIncremental();
		final AtomicLong count = new AtomicLong();
		reader.parseEvents(new File("C:\\Users\\sander\\Documents\\datasets\\Pouneh Samadi\\events-log.xes.gz"),
				new Function<XEvent, Object>() {

					public Object call(XEvent input) throws Exception {
						count.incrementAndGet();
						return null;
					}
				}, new Runnable() {
					public void run() {
						// TODO Auto-generated method stub

					}
				});
		System.out.println(count.get());
	}
}
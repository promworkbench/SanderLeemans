package thesis.helperClasses;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.Function;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;

public class Log2Latex {
	public static void main(String[] args) throws FileNotFoundException, Exception {
		File file = new File("D:\\svn\\00 - the beast\\pccfr single.xes");
		final AtomicInteger traces = new AtomicInteger(0);
		XLogParserIncremental.parseTraces(file, new Function<XTrace, Object>() {
			public Object call(XTrace trace) throws Exception {
				boolean started = false;
				System.out.print("$\\langle ");
				for (XEvent event : trace) {
					if (started) {
						System.out.print(", ");
					} else {
						started = true;
					}
					System.out.print(MiningParameters.getDefaultClassifier().getClassIdentity(event));
				}
				System.out.println("\\rangle$, ");
				
//				if (traces.get() % 5 == 4) {
//					System.out.println("\\\\");
//				} else {
//					System.out.print("&&");
//				}
				traces.incrementAndGet();
				return null;
			}
		});
	}
}

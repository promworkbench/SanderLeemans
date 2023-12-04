package svn45crimes;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.Function;

import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.set.hash.THashSet;
import thesis.helperClasses.XLogParserIncremental;
import thesis.helperClasses.XLogWriterIncremental;

public class tkde2021crimes6sampleLogs {

	public static void main(String... args) throws IOException, Exception {
		ExperimentParameters parameters = new ExperimentParameters();

		int times = 10;
		double probability = 0.1;

		File[] baseLogs = new File(parameters.getBaseDirectory(), "00-base logs").listFiles();
		for (File baseLog : baseLogs) {
			for (int time = 0; time < times; time++) {
				File outputLogFile = new File(parameters.getLogDirectory(), baseLog.getName() + time + ".xes.gz");
				if (!outputLogFile.exists()) {
					XLogWriterIncremental writer = new XLogWriterIncremental(outputLogFile);

					//read activities
					THashSet<String> activities = new THashSet<>();
					Random random = new Random(time + baseLog.getName().hashCode());

					do {
						XLogParserIncremental.parseTraces(baseLog, new Function<XTrace, Object>() {
							public Object call(XTrace input) throws Exception {
								for (XEvent event : input) {
									activities.add(XConceptExtension.instance().extractName(event));
								}
								return null;
							}
						});

						System.out.println(activities);

						//sample activities
						{
							TObjectHashIterator<String> it = activities.iterator();
							while (it.hasNext()) {
								it.next();
								double d = random.nextDouble();
								if (d > probability) {
									it.remove();
								}
							}
						}
					} while (activities.isEmpty());

					System.out.println(activities);

					XLogParserIncremental.parseTraces(baseLog, new Function<XTrace, Object>() {
						public Object call(XTrace input) throws Exception {

							Iterator<XEvent> it = input.iterator();
							while (it.hasNext()) {
								if (!activities.contains(XConceptExtension.instance().extractName(it.next()))) {
									it.remove();
								}
							}

							if (!input.isEmpty()) {
								writer.writeTrace(input);
							}

							return null;
						}
					});

					writer.close();
				}
			}
		}
	}
}

package icsoc2019;

import java.io.File;
import java.io.IOException;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.impl.XEventImpl;

import thesis.helperClasses.LogWriterIncremental;
import thesis.helperClasses.XLogWriterIncremental;

public class GenerateLogRunningExample {
	public static void main(String... args) throws IOException {
		File file = new File(
				"C:\\Users\\sander\\Documents\\svn\\27 - stochastic Petri nets\\ICSOC 2019\\testLog running.xes.gz");
		LogWriterIncremental writer = new XLogWriterIncremental(file);

		String[][] traces = { { }, { "a" }, { "a", "a" }, { "a", "a", "a" },
				{ "a", "a", "a", "a" } };
		int[] probabilities = { 1, 2, 4, 1, 2 };

		long time = 0;
		for (int t = 0; t < traces.length; t++) {
			int repetitions = probabilities[t];

			for (int i = 0; i < repetitions; i++) {
				writer.startTrace();
				XEvent event = new XEventImpl();
				for (String activity : traces[t]) {
					XTimeExtension.instance().assignTimestamp(event, time);
					XConceptExtension.instance().assignName(event, activity);
					writer.writeEvent(event);
					time++;
				}
				writer.endTrace();
			}
		}

		writer.close();
	}
}

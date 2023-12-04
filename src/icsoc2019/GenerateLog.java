package icsoc2019;

import java.io.File;
import java.io.IOException;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.impl.XEventImpl;

import thesis.helperClasses.LogWriterIncremental;
import thesis.helperClasses.XLogWriterIncremental;

public class GenerateLog {
	public static void main(String... args) throws IOException {
		File file = new File(
				"C:\\Users\\sander\\Documents\\svn\\27 - stochastic Petri nets\\ICSOC 2019\\testLog.xes.gz");
		LogWriterIncremental writer = new XLogWriterIncremental(file);

		String[][] traces = { { "a", "b", "c" }, { "a", "c", "b" }, { "a", "d" }, { "a", "d", "e", "d" },
				{ "a", "d", "e", "d", "e", "d" }, { "a", "d", "e", "d", "e", "d", "e", "d" } };
		int[] probabilities = { 10, 15, 30, 20, 15, 10 };

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

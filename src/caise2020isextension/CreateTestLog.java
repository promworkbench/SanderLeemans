package caise2020isextension;

import java.io.File;
import java.io.IOException;

import thesis.helperClasses.XLogWriterIncremental;

public class CreateTestLog {
	public static void main(String[] args) throws IOException {
		File output = new File(ExperimentParameters.baseDirectory, "0-logs/testLog.xes.gz");

		XLogWriterIncremental writer = new XLogWriterIncremental(output);

		for (int i = 0; i < 60; i++) {
			writer.startTrace();
			writer.writeEvent("a", "complete");
			writer.endTrace();
		}

		for (int i = 0; i < 40; i++) {
			writer.startTrace();
			writer.writeEvent("b", "complete");
			writer.endTrace();
		}

		writer.close();
	}
}

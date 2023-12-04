package svn51traceprobability;

import java.io.File;
import java.io.IOException;

import org.processmining.statisticaltests.test.XLogWriterIncremental;

public class SendLog {
	public static void main(String[] args) throws IOException {
		File file = new File("/home/sander/Documents/svn/53 - long distance dependencies/test log 20221012.xes.gz");

		XLogWriterIncremental writer = new XLogWriterIncremental(file);

		for (int i = 0; i < 100; i++) {
			writer.startTrace();
			writer.writeEvent("a", "complete");
			writer.writeEvent("b", "complete");
			writer.writeEvent("d", "complete");
			writer.endTrace();
		}
		
		for (int i = 0; i < 50; i++) {
			writer.startTrace();
			writer.writeEvent("a", "complete");
			writer.writeEvent("b", "complete");
			writer.writeEvent("e", "complete");
			writer.endTrace();
		}
		
		for (int i = 0; i < 75; i++) {
			writer.startTrace();
			writer.writeEvent("c", "complete");
			writer.writeEvent("d", "complete");
			writer.endTrace();
		}
		
		for (int i = 0; i < 75; i++) {
			writer.startTrace();
			writer.writeEvent("c", "complete");
			writer.writeEvent("e", "complete");
			writer.endTrace();
		}
		
//		for (int i = 0; i < 1; i++) {
//			writer.startTrace();
//			writer.writeEvent("a", "complete");
//			writer.writeEvent("b", "complete");
//			writer.writeEvent("d", "complete");
//			writer.writeEvent("d", "complete");
//			writer.endTrace();
//		}

		writer.close();
	}
}

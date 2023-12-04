package coopis2018;

import java.io.File;

import org.deckfour.xes.model.XLog;

public class AlgorithmSplit implements Algorithm {

	public String getName() {
		return "Split";
	}
	
	public String getAbbreviation() {
		return "SM";
	}

	public String getFileExtension() {
		return ".bpmn";
	}

	public void run(File logFile, XLog log, File modelFile) throws Exception {

		//Split miner automatically puts .bpmn after the file name
		File out = new File(modelFile.getParentFile().getAbsolutePath(),
				modelFile.getName().substring(0, modelFile.getName().length() - 5));

		ProcessBuilder pb = new ProcessBuilder("java", "-jar",
				"C:\\Users\\sander\\workspace\\SanderLeemans\\src\\coopis2018\\splitminer.jar", "0.1", "0.4",
				logFile.getAbsolutePath(), out.getAbsolutePath());
		pb.start().waitFor();
	}

}

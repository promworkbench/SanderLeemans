package svn45crimes;

import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

import org.deckfour.xes.model.XLog;

public class AlgorithmSplit implements Algorithm {

	public String getName() {
		return "SM";
	}

	public String getLatexName() {
		return "Split Miner";
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

		//		ProcessBuilder pb = new ProcessBuilder("java", "-jar",
		//				"/home/sander/eclipse-workspace/SanderLeemans/src/tkde2021crimes/splitminer.jar", "0.1", "0.4",
		//				logFile.getAbsolutePath(), out.getAbsolutePath());
		ProcessBuilder pb = new ProcessBuilder("java", "-jar",
				"C:\\Users\\leemans2\\eclipse-workspace\\SanderLeemans\\src\\tkde2021crimes\\splitminer.jar", "0.1",
				"0.4", logFile.getAbsolutePath(), out.getAbsolutePath());
		System.out.println(
				"java -jar /home/sander/eclipse-workspace/SanderLeemans/src/tkde2021crimes/splitminer.jar 0.1 0.4 \""
						+ logFile.getAbsolutePath() + "\" \"" + out.getAbsolutePath() + "\"");

		Process p = pb.start();
		StringBuilder error = capture(p.getErrorStream());
		out2std(p.getInputStream());

		boolean finishedOnTime = true;
		//boolean finishedOnTime = p.waitFor(5, TimeUnit.HOURS);
		//p.destroy();
		int wf = p.waitFor();

		if (!finishedOnTime) {
			throw new Exception("timeout");
		} else if (!error.toString().isEmpty()) {
			throw new Exception(error.toString());
		} else if (wf != 0) {
			throw new Exception("exit code " + wf);
		} else {
			System.out.println("bla");
		}
	}

	public static StringBuilder capture(InputStream err) {
		final StringBuilder result = new StringBuilder();
		new Thread(new Runnable() {
			public void run() {
				Scanner scanner = new Scanner(err);
				while (scanner.hasNextLine()) {
					result.append(scanner.nextLine());
				}
				scanner.close();
			}
		}).start();
		return result;
	}

	public static void out2std(InputStream std) {
		new Thread(new Runnable() {
			public void run() {
				Scanner scanner = new Scanner(std);
				while (scanner.hasNextLine()) {
					System.out.println(scanner.nextLine());
				}
				scanner.close();
			}
		}).start();
	}
}

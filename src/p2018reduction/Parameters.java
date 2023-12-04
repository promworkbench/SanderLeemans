package p2018reduction;

import java.io.File;

public class Parameters {
	public static final File baseDirectory = new File(
			"/home/sander/Documents/svn/19 - reduction rules for process trees/evaluation");

	public static final File inputLogs = new File(baseDirectory, "000 - inputLogs");
	public static final File discoveredModels = new File(baseDirectory, "010 - discovered models");

	public static File[] getInputFiles() {
		inputLogs.mkdirs();
		return inputLogs.listFiles();
	}

	public static File getDiscoveredModelFile(File inputLog) {
		discoveredModels.mkdirs();
		return new File(discoveredModels, inputLog.getName() + ".tree");
	}
}

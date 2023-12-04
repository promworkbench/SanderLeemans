package thesis.evaluation.rediscoverability.noise;

import java.io.File;

import thesis.evaluation.rediscoverability.RediscoverabilitySettings;

public class NoiseSettings {
	public static final File baseDirectory = new File("C://Users//sander//Documents//svn//00 - the beast//experiments//noise");
	//public static final File baseDirectory = new File(".");

	public static final File generatedModelDirectory = new File(baseDirectory, "generatedModels");
	public static final File logDirectory = new File(baseDirectory, "generatedLogs");
	public static final File discoveredModelDirectory = new File(baseDirectory, "discoveredModels");
	public static final File measuresDirectory = new File(baseDirectory, "measures");

	public static final RediscoverabilitySettings.Algorithm[] algorithms = RediscoverabilitySettings.Algorithm.values();

	public static final int logRounds = 13;
	public static final int logIncreaseFactor = 2;
	
	public static final int noiseRounds = 14;
	public static final int noiseIncreaseFactor = 2;
}

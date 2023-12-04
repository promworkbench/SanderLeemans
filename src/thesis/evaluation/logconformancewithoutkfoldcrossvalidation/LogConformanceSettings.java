package thesis.evaluation.logconformancewithoutkfoldcrossvalidation;

import java.io.File;

public class LogConformanceSettings {
	public static final File baseDirectory = new File(
			"C://Users//sander//Documents//svn//00 - the beast//experiments//nonAtomic");
	//public static final File baseDirectory = new File(".");

	public static final File logDirectory = new File("C://Users//sander//Documents//svn//00 - the beast//experiments//logQuality//full logs");
	public static final File discoveredModelDirectory = new File(baseDirectory, "discoveredModels");
	public static final File imageDirectory = new File(baseDirectory, "images");
	
}

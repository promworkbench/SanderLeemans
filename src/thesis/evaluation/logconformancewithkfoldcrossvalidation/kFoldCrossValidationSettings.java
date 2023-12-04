package thesis.evaluation.logconformancewithkfoldcrossvalidation;

import java.io.File;

import com.google.common.collect.ImmutableMap;

public class kFoldCrossValidationSettings {

	public static final File baseDirectory = new File(
			"C://Users//sander//Documents//svn//00 - the beast//experiments//logQuality");

	public static final File fullLogDirectory = new File(baseDirectory, "full logs");
	public static final File modelDirectory = new File(baseDirectory, "discoveredModels");
	public static final File measuresDirectory = new File(baseDirectory, "measures");

	public static final int folds = 15;

	public static final ImmutableMap<String, String> logAbbreviations = ImmutableMap.<String, String>builder()
			.put("hospital_log.xes.gz", "BP11").put("financial_log.xes.gz", "BP12")
			.put("Receipt phase WABO CoSeLoG project.xes.gz", "RPW")
			.put("Road_Traffic_Fine_Management_Process.xes.gz", "RF").put("WABO1.xes.gz", "WA1")
			.put("WABO2.xes.gz", "WA2").put("WABO3.xes.gz", "WA3").put("WABO4.xes.gz", "WA4").put("WABO5.xes.gz", "WA5")
			.build();

	public static final ImmutableMap<String, String> algorithmAbbreviations = ImmutableMap.<String, String>builder()
			.put("im", "\\IM").put("ima", "\\IMa").put("imc", "\\IMc").put("imf", "\\IMf").put("imfa", "\\IMfa")
			.put("imd", "\\IMd").put("imfd", "\\IMfd").put("imcd", "\\IMcd").put("alpha", "$\\alpha$").put("hm", "HM")
			.put("imlc", "\\IMlc").put("imflc", "\\IMflc").put("talpha", "T$\\alpha$").build();

	public static final int maxLogsWidthInTable = 3;
}

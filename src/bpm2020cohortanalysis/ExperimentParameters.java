package bpm2020cohortanalysis;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang.SystemUtils;

import gnu.trove.map.hash.THashMap;

public class ExperimentParameters {

	private static final File baseDirectory = SystemUtils.IS_OS_WINDOWS
			? new File("E:\\32 - cohort analysis (shiva, hassan, shazia)\\experiments")
			: new File("/home/sander/Documents/svn/32 - cohort analysis (shiva, hassan, shazia)/experiments");

	private static final File outputFile = new File(baseDirectory, "results.tex");
	private static final File outputCSVFile = new File(baseDirectory, "results.txt");
	private static final File outputCSVNegativeFile = new File(baseDirectory, "resultsNegative.txt");
	private static final File outputPlotFile = new File(baseDirectory, "results.png");

	private Map<String, String> log2abbreviation = new THashMap<>();
	private Map<String, String> log2sortName = new THashMap<>();

	public ExperimentParameters() {
		log2abbreviation.put("bpic18 Control summary.xes.gz", "BPIC18-1");
		log2abbreviation.put("bpic18 Department control parcels.xes.gz", "BPIC18-2");
		log2abbreviation.put("bpic18 Entitlement application.xes.gz", "BPIC18-3");
		log2abbreviation.put("bpic18 Geo parcel document.xes.gz", "BPIC18-4");
		log2abbreviation.put("bpic18 Inspection.xes.gz", "BPIC18-5");
		log2abbreviation.put("bpic18 Parcel document.xes.gz", "BPIC18-6");
		log2abbreviation.put("bpic18 Payment application.xes.gz", "BPIC18-7");
		log2abbreviation.put("bpic18 Reference alignment.xes.gz", "BPIC18-8");
		log2abbreviation.put("BPI Challenge 2018.xes.gz", "BPIC18");
		log2abbreviation.put("Road_Traffic_Fine_Management_Process.xes.gz", "Roadfines");
		log2abbreviation.put("financial_log.xes", "BPIC12");
		log2abbreviation.put("Sepsis.xes.gz", "Sepsis");
		log2abbreviation.put("BPIC15_1.xes.gz", "BPIC15-1");
		log2abbreviation.put("BPIC15_2.xes.gz", "BPIC15-2");
		log2abbreviation.put("BPIC15_3.xes.gz", "BPIC15-3");
		log2abbreviation.put("BPIC15_4.xes.gz", "BPIC15-4");
		log2abbreviation.put("BPIC15_5.xes.gz", "BPIC15-5");
		log2abbreviation.put("Hospital_log.xes.gz", "BPIC11");
		log2abbreviation.put(
				"Receipt phase of an environmental permit application process ( WABO ) CoSeLoG project.xes.gz",
				"WABO-a");
		log2abbreviation.put("CoSeLoG WABO 1.xes.gz", "WABO 1");
		log2abbreviation.put("CoSeLoG WABO 2.xes.gz", "WABO 2");
		log2abbreviation.put("CoSeLoG WABO 3.xes.gz", "WABO 3");
		log2abbreviation.put("CoSeLoG WABO 4.xes.gz", "WABO 4");
		log2abbreviation.put("CoSeLoG WABO 5.xes.gz", "WABO 5");
		log2abbreviation.put("BPI_Challenge_2019.xes.gz", "BPIC19");
		log2abbreviation.put("BPI_Challenge_2012.xes.gz", "BPIC12");
		log2abbreviation.put("BPI Challenge 2017.xes.gz", "BPIC17");

		log2abbreviation.put("BPIC20-DomesticDeclarations.xes.gz", "BPIC20-dd");
		log2abbreviation.put("BPIC20-InternationalDeclarations.xes.gz", "BPIC20-id");
		log2abbreviation.put("BPIC20-PermitLog.xes.gz", "BPIC20-pl");
		log2abbreviation.put("BPIC20-PrepaidTravelCost.xes.gz", "BPIC20-ptc");
		log2abbreviation.put("BPIC20-RequestForPayment.xes.gz", "BPIC20-rfp");
	}

	public File getBaseDirectory() {
		return baseDirectory;
	}

	public File getLogDirectory() {
		return new File(baseDirectory, "0-logs");
	}

	public File getLogMeasuresDirectory() {
		return new File(baseDirectory, "1-logMeasures");
	}

	public File getFeatureSetsDirectory() {
		return new File(baseDirectory, "2-featureSets");
	}

	public File getCohortsDirectory() {
		return new File(baseDirectory, "3-cohorts");
	}

	public static File getOutputfile() {
		return outputFile;
	}

	public static File getOutputCSVfile() {
		return outputCSVFile;
	}

	public static File getOutputCSVNegativefile() {
		return outputCSVNegativeFile;
	}

	public static File getOutputPlotfile() {
		return outputPlotFile;
	}

	public Map<String, String> getLog2abbreviation() {
		return log2abbreviation;
	}

	public Map<String, String> getSortName() {
		return log2sortName;
	}

	public int[] getFeatureSetSizes() {
		return new int[] { 1, 2, 3, 4, 5 };
	}
}
package svn53longdistancedependencies;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.SystemUtils;

import gnu.trove.map.hash.THashMap;

public class ExperimentParameters {

	public static final File baseDirectory = SystemUtils.IS_OS_LINUX
			? new File("/home/sander/Documents/svn/53 - long distance dependencies/experiments take 2")
			: new File("/mnt/hgfs/experiments");

	private static final File outputFile = new File(baseDirectory, "results.tex");

	private List<Algorithm> algorithms = new ArrayList<>();
	private List<StochasticAlgorithm> stochasticAlgorithms = new ArrayList<>();
	private List<Measure> measures = new ArrayList<>();
	private Map<String, String> log2abbreviation = new THashMap<>();
	private Map<String, String> log2sortName = new THashMap<>();

	private static final int repetitions = 10;

	public ExperimentParameters() {
		algorithms.add(new AlgorithmIMf());
		algorithms.add(new AlgorithmDFM());

		//stochasticAlgorithms.add(new StochasticAlgorithmWeightAlignment());
		//stochasticAlgorithms.add(new StochasticAlgorithmFrequency());
		stochasticAlgorithms.add(new StochasticAlgorithmLdd());
		stochasticAlgorithms.add(new StochasticAlgorithmLddLogAssumption());

		measures.add(new MeasureTransitions());
		measures.add(new MeasureTime());
		measures.add(new MeasureEMSC());
		measures.add(new MeasureWeights());

		log2abbreviation.put("bpic18 Control summary.xes.gz", "BPIC18-1");
		log2abbreviation.put("bpic18 Department control parcels.xes.gz", "BPIC18-2");
		log2abbreviation.put("bpic18 Entitlement application.xes.gz", "BPIC18-3");
		log2abbreviation.put("bpic18 Geo parcel document.xes.gz", "BPIC18-4");
		log2abbreviation.put("bpic18 Inspection.xes.gz", "BPIC18-5");
		log2abbreviation.put("bpic18 Parcel document.xes.gz", "BPIC18-6");
		log2abbreviation.put("bpic18 Payment application.xes.gz", "BPIC18-7");
		log2abbreviation.put("bpic18 Reference alignment.xes.gz", "BPIC18-8");
		log2abbreviation.put("Road_Traffic_Fine_Management_Process.xes.gz", "Roadfines");
		log2abbreviation.put("financial_log.xes.gz", "BPIC12");
		log2abbreviation.put("Sepsis.xes.gz", "Sepsis");
		log2abbreviation.put("BPIC15_1.xes.gz", "BPIC15-1");
		log2abbreviation.put("BPIC15_2.xes.gz", "BPIC15-2");
		log2abbreviation.put("BPIC15_3.xes.gz", "BPIC15-3");
		log2abbreviation.put("BPIC15_4.xes.gz", "BPIC15-4");
		log2abbreviation.put("BPIC15_5.xes.gz", "BPIC15-5");
		log2abbreviation.put("hospital log.xes.gz", "BPIC11");
		log2abbreviation.put("BPI Challenge 2017.xes.gz", "BPIC17");
		log2abbreviation.put("BPI Challenge 2017 - Offer log.xes.gz", "BPIC17-o");
		log2abbreviation.put("BPI_Challenge_2013_closed_problems.xes.gz", "BPIC13-cp");
		log2abbreviation.put("BPI_Challenge_2013_incidents.xes.gz", "BPIC13-i");
		log2abbreviation.put("BPI_Challenge_2013_open_problems.xes.gz", "BPIC13-op");
		log2abbreviation.put("bpic2020-InternationalDeclarations.xes.gz", "BPIC20-id");
		log2abbreviation.put("bpic2020-PrepaidTravelCost.xes.gz", "BPIC20-pt");
		log2abbreviation.put("bpic2020-DomesticDeclarations.xes.gz", "BPIC20-dd");
		log2abbreviation.put("bpic2020-RequestForPayment.xes.gz", "BPIC20-rf");
		log2abbreviation.put("testLog.xes.gz", "testLog40-60");

		log2sortName.put("bpic18 Control summary.xes.gz", "BPIC18-1");
		log2sortName.put("bpic18 Department control parcels.xes.gz", "BPIC18-2");
		log2sortName.put("bpic18 Entitlement application.xes.gz", "BPIC18-3");
		log2sortName.put("bpic18 Geo parcel document.xes.gz", "BPIC18-4");
		log2sortName.put("bpic18 Inspection.xes.gz", "BPIC18-5");
		log2sortName.put("bpic18 Parcel document.xes.gz", "BPIC18-6");
		log2sortName.put("bpic18 Payment application.xes.gz", "BPIC18-7");
		log2sortName.put("bpic18 Reference alignment.xes.gz", "BPIC18-8");
		log2sortName.put("Road_Traffic_Fine_Management_Process.xes.gz", "BPIC15-7");
		log2sortName.put("financial_log.xes", "BPIC12");
		log2sortName.put("Sepsis.xes.gz", "Sepsis");
		log2sortName.put("BPIC15_1.xes", "BPIC15-1");
		log2sortName.put("BPIC15_2.xes", "BPIC15-2");
		log2sortName.put("BPIC15_3.xes", "BPIC15-3");
		log2sortName.put("BPIC15_4.xes", "BPIC15-4");
		log2sortName.put("BPIC15_5.xes", "BPIC15-5");
		log2sortName.put("hospital_log.xes.gz", "BPIC11");
		log2sortName.put("BPI Challenge 2017.xes.gz", "BPIC17");
		log2sortName.put("BPI Challenge 2017 - Offer log.xes.gz", "BPIC17-o");
		log2sortName.put("bpi_challenge_2013_closed_problems.xes.gz", "BPIC13-cp");
		log2sortName.put("bpi_challenge_2013_incidents.xes.gz", "BPIC13-i");
		log2sortName.put("bpi_challenge_2013_open_problems.xes.gz", "BPIC13-op");
		log2sortName.put("testLog.xes.gz", "testLog40-60");
	}

	public File getBaseDirectory() {
		return baseDirectory;
	}

	public File getLogDirectory() {
		return new File(baseDirectory, "0-logs");
	}

	public File getDiscoveredModelsDirectory() {
		return new File(baseDirectory, "1-discoveredmodels");
	}

	public File getSplitLogsDirectory() {
		return new File(baseDirectory, "2-splitlogs");
	}

	public File getSplitLogsTestDirectory() {
		return new File(baseDirectory, "2-splitlogstest");
	}

	public File getDiscoveredStochasticModelsDirectory() {
		return new File(baseDirectory, "3-discoveredstochasticmodels");
	}

	public File getMeasuresDirectory() {
		return new File(baseDirectory, "4-measures");
	}

	public File getResultsDirectory() {
		return new File(baseDirectory, "5-results");
	}

	public static File getOutputfile() {
		return outputFile;
	}

	public Map<String, String> getLog2abbreviation() {
		return log2abbreviation;
	}

	public Map<String, String> getSortName() {
		return log2sortName;
	}

	public List<Algorithm> getAlgorithms() {
		return Collections.unmodifiableList(algorithms);
	}

	public List<StochasticAlgorithm> getStochasticAlgorithms() {
		return Collections.unmodifiableList(stochasticAlgorithms);
	}

	public List<Measure> getMeasures() {
		return Collections.unmodifiableList(measures);
	}

	public List<Call> getCalls() {
		List<Call> result = new ArrayList<>();

		File[] files = getLogDirectory().listFiles();
		for (File logFile : files) {
			if (logFile.getName().endsWith(".xes.gz")) {
				for (int repetition = 0; repetition < repetitions; repetition++) {
					for (Algorithm algorithm : algorithms) {
						for (StochasticAlgorithm stochasticAlgorithm : stochasticAlgorithms) {
							for (Measure measure : measures) {
								result.add(
										new Call(logFile, algorithm, stochasticAlgorithm, repetition, measure, this));
							}
						}
					}
				}
			}
		}
		return result;
	}
}
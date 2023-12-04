package svn45crimes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang.SystemUtils;

import gnu.trove.map.hash.THashMap;

public class ExperimentParameters {

	public static final Semaphore svn = new Semaphore(1);

	public static final File baseDirectory = SystemUtils.IS_OS_LINUX
			? new File("/home/sander/Documents/svn/45 - crimes 4p0/example experiment/")
			: new File("C:\\experiment\\example experiment");

	private List<Algorithm> algorithms = new ArrayList<>();
	private List<Measure> measures = new ArrayList<>();
	private List<Noise> noises = new ArrayList<>();
	private Map<String, String> log2abbreviation = new THashMap<>();
	private Map<String, String> log2sortName = new THashMap<>();

	private int noiseAmountMin = 0;
	private int noiseAmountMax = 95;
	private int noiseAmountStep = 5;
	private int repetitions = 3;

	public ExperimentParameters() {
		algorithms.add(new AlgorithmDFM());
		//algorithms.add(new AlgorithmIMf());
		algorithms.add(new AlgorithmSplit());
		//algorithms.add(new AlgorithmFlower());

		measures.add(new MeasureStructure());
		measures.add(new MeasureModelSize());
		measures.add(new MeasureArya());
		//measures.add(new MeasureAryaStandAlone());
		//measures.add(new MeasureProjected());
		//measures.add(new MeasureSoundness());

		noises.add(new NoiseDeleteEvent());
		noises.add(new NoiseFreshActivity());
		noises.add(new NoiseSwapEvents());

		log2abbreviation.put("bpic18 Control summary.xes.gz", "BPIC18-1");
		log2abbreviation.put("bpic18 Department control parcels.xes.gz", "BPIC18-2");
		log2abbreviation.put("bpic18 Entitlement application.xes.gz", "BPIC18-3");
		log2abbreviation.put("bpic18 Geo parcel document.xes.gz", "BPIC18-4");
		log2abbreviation.put("bpic18 Inspection.xes.gz", "BPIC18-5");
		log2abbreviation.put("bpic18 Parcel document.xes.gz", "BPIC18-6");
		log2abbreviation.put("bpic18 Payment application.xes.gz", "BPIC18-7");
		log2abbreviation.put("bpic18 Reference alignment.xes.gz", "BPIC18-8");
		log2abbreviation.put("Road_Traffic_Fine_Management_Process.xes.gz", "Roadfines");
		log2abbreviation.put("Road_Traffic_Fine_Management_Process-abstractedTime.xes.gz", "Roadfines'");
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
		log2abbreviation.put("bpi_challenge_2013_closed_problems.xes.gz", "BPIC13-cp");
		log2abbreviation.put("bpi_challenge_2013_incidents.xes.gz", "BPIC13-i");
		log2abbreviation.put("bpi_challenge_2013_open_problems.xes.gz", "BPIC13-op");
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
		log2sortName.put("Road_Traffic_Fine_Management_Process-abstractedTime.xes.gz", "BPIC15-7a");
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

	public List<Call> getCalls() {
		List<Call> result = new ArrayList<>();

		File[] files = getLogDirectory().listFiles();
		for (File logFile : files) {
			if (logFile.getName().endsWith(".xes.gz")) {
				for (Algorithm algorithm : algorithms) {
					for (Noise noise : noises) {
						for (int noiseAmount = noiseAmountMin; noiseAmount <= noiseAmountMax; noiseAmount += noiseAmountStep) {
							for (int repetition = 0; repetition < repetitions; repetition++) {
								result.add(new Call(logFile.getName(), algorithm, noise, noiseAmount / 100.0,
										repetition, this));
							}
						}
					}
				}
			} else {
				System.out.println("log file " + logFile.getName() + " skipped as it's not a .xes.gz file");
			}
		}

		return result;
	}

	public File getBaseDirectory() {
		return baseDirectory;
	}

	public File getLogDirectory() {
		return new File(baseDirectory, "0-logs");
	}

	public File getNoiseLogsDirectory() {
		return new File(baseDirectory, "1-noiselogs");
	}

	public File getDiscoveredModelsDirectory() {
		return new File(baseDirectory, "2-discoveredmodels");
	}

	public File getMeasuresDirectory() {
		return new File(baseDirectory, "3-measures");
	}

	public File getResultsDirectory() {
		return new File(baseDirectory, "4-results");
	}
	
	public File getTestsResultsFile() {
		return new File(getResultsDirectory(), "testsResults.txt");
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

	public List<Measure> getMeasures() {
		return Collections.unmodifiableList(measures);
	}

}
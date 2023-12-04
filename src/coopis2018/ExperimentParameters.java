package coopis2018;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.SystemUtils;

import gnu.trove.map.hash.THashMap;

public class ExperimentParameters {

	private static final File baseDirectory = SystemUtils.IS_OS_WINDOWS
			? new File(
					"C:\\Users\\sander\\Documents\\svn\\15 - Evolutionary Tree Miner and Inductive Miner\\experiments")
			: new File("/mnt/hgfs/experiments");
	private static final int repeat = 10;

	private static final File outputFile = new File(baseDirectory, "results.txt");

	private static final int folds = 3;

	private List<Algorithm> algorithms = new ArrayList<>();
	private List<Measure> measures = new ArrayList<>();
	private List<Measure> allMeasures = new ArrayList<>();
	
	private Map<String, String> log2abbreviation = new THashMap<>();

	public ExperimentParameters() {
		algorithms.add(new AlgorithmIMf());
		algorithms.add(new AlgorithmIndulpet());
		algorithms.add(new AlgorithmETM());
		algorithms.add(new AlgorithmSplit());
		algorithms.add(new AlgorithmFlower());
		//algorithms.add(new AlgorithmTrace());

		measures.add(new MeasureETC());
		measures.add(new MeasureProjected());
		measures.add(new MeasureSimplicity());
		//measures.add(new MeasureSoundness());
		//measures.add(new MeasureBoundedness());

		allMeasures.add(new MeasureSimplicity());
		allMeasures.add(new MeasureETC());
		allMeasures.add(new MeasureArya());
		allMeasures.add(new MeasureProjected());
		allMeasures.add(new MeasureRelaxedSoundness());
		allMeasures.add(new MeasureBoundedness());
		
		log2abbreviation.put("bpic18 Control summary.xes.gz", "bpic18-1");
		log2abbreviation.put("bpic18 Department control parcels.xes.gz", "bpic18-2");
		log2abbreviation.put("bpic18 Entitlement application.xes.gz", "bpic18-3");
		log2abbreviation.put("bpic18 Geo parcel document.xes.gz", "bpic18-4");
		log2abbreviation.put("bpic18 Inspection.xes.gz", "bpic18-5");
		log2abbreviation.put("bpic18 Parcel document.xes.gz", "bpic18-6");
		log2abbreviation.put("bpic18 Payment application.xes.gz", "bpic18-7");
		log2abbreviation.put("bpic18 Reference alignment.xes.gz", "bpic18-8");
		log2abbreviation.put("Road_Traffic_Fine_Management_Process.xes.gz", "rtf");
		log2abbreviation.put("financial_log.xes", "bpic12");
		log2abbreviation.put("Sepsis.xes.gz", "sps");
		log2abbreviation.put("BPIC15_1.xes", "bpic15-1");
		log2abbreviation.put("BPIC15_2.xes", "bpic15-2");
		log2abbreviation.put("BPIC15_3.xes", "bpic15-3");
		log2abbreviation.put("BPIC15_4.xes", "bpic15-4");
		log2abbreviation.put("BPIC15_5.xes", "bpic15-5");
		log2abbreviation.put("hospital_log.xes", "bpic11");
	}

	public File getBaseDirectory() {
		return baseDirectory;
	}

	public File getLogDirectory() {
		return new File(baseDirectory, "0-logs");
	}

	public File getSplitLogDirectory() {
		return new File(baseDirectory, "1-splitLogs");
	}

	public File getModelsDirectory() {
		return new File(baseDirectory, "2-models");
	}

	public File getMeasuresDirectory() {
		return new File(baseDirectory, "3-measures");
	}

	public List<Algorithm> getAlgorithms() {
		return Collections.unmodifiableList(algorithms);
	}

	public List<Measure> getMeasures() {
		return Collections.unmodifiableList(measures);
	}

	public static int getNumberOfFolds() {
		return folds;
	}

	public static Iterable<Integer> getFolds() {
		return new Iterable<Integer>() {
			public Iterator<Integer> iterator() {
				return new Iterator<Integer>() {
					int now = -1;

					public Integer next() {
						now++;
						return now;
					}

					public boolean hasNext() {
						return now < folds - 1;
					}
				};
			}
		};
	}

	public static int getNumberOfRuns() {
		return repeat;
	}

	public static Iterable<Integer> getRuns() {
		return new Iterable<Integer>() {
			public Iterator<Integer> iterator() {
				return new Iterator<Integer>() {
					int now = -1;

					public Integer next() {
						now++;
						return now;
					}

					public boolean hasNext() {
						return now < repeat - 1;
					}
				};
			}
		};
	}

	public List<Measure> getAllMeasures() {
		return Collections.unmodifiableList(allMeasures);
	}

	public static File getOutputfile() {
		return outputFile;
	}

	public Map<String, String> getLog2abbreviation() {
		return log2abbreviation;
	}

}

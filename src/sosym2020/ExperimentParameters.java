package sosym2020;

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
					"C:\\Users\\sander\\Documents\\svn\\19 - reduction rules for process trees\\experiments\\computer-discovered")
			: new File("/mnt/hgfs/experiments");

	private static final int repeat = 5;
	private static final int folds = 1;
	private static final File outputFile = new File(baseDirectory, "results.txt");

	private List<Algorithm> algorithms = new ArrayList<>();
	private List<Measure> measures = new ArrayList<>();
	private List<Measure> allMeasures = new ArrayList<>();

	private Map<String, String> log2abbreviation = new THashMap<>();
	private Map<String, String> log2sortName = new THashMap<>();

	public ExperimentParameters() {
		algorithms.add(new AlgorithmIMf());
		algorithms.add(new AlgorithmETM());
		algorithms.add(new AlgorithmIndulpet());

		measures.add(new MeasureNoReduction());
		measures.add(new MeasureTreeReduction());
		measures.add(new MeasurePNReduction());
		measures.add(new MeasureBothReduction());

		allMeasures.add(new MeasureNoReduction());
		allMeasures.add(new MeasureTreeReduction());
		allMeasures.add(new MeasurePNReduction());
		allMeasures.add(new MeasureBothReduction());

		log2abbreviation.put("bpic18 Control summary.xes.gz", "BPIC18-1");
		log2abbreviation.put("bpic18 Department control parcels.xes.gz", "BPIC18-2");
		log2abbreviation.put("bpic18 Entitlement application.xes.gz", "BPIC18-3");
		log2abbreviation.put("bpic18 Geo parcel document.xes.gz", "BPIC18-4");
		log2abbreviation.put("bpic18 Inspection.xes.gz", "BPIC18-5");
		log2abbreviation.put("bpic18 Parcel document.xes.gz", "BPIC18-6");
		log2abbreviation.put("bpic18 Payment application.xes.gz", "BPIC18-7");
		log2abbreviation.put("bpic18 Reference alignment.xes.gz", "BPIC18-8");
		log2abbreviation.put("Road_Traffic_Fine_Management_Process.xes.gz", "Roadfines");
		log2abbreviation.put("financial_log.xes", "BPIC12");
		log2abbreviation.put("Sepsis.xes.gz", "Sepsis");
		log2abbreviation.put("BPIC15_1.xes", "BPIC15-1");
		log2abbreviation.put("BPIC15_2.xes", "BPIC15-2");
		log2abbreviation.put("BPIC15_3.xes", "BPIC15-3");
		log2abbreviation.put("BPIC15_4.xes", "BPIC15-4");
		log2abbreviation.put("BPIC15_5.xes", "BPIC15-5");
		log2abbreviation.put("hospital_log.xes", "BPIC11");
		
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
		log2sortName.put("hospital_log.xes", "BPIC11");
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
	
	public Map<String, String> getSortName() {
		return log2sortName;
	}

}

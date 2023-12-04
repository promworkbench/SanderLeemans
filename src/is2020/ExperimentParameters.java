package is2020;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.SystemUtils;

import gnu.trove.map.hash.THashMap;

public class ExperimentParameters {

	private static final File baseDirectory = SystemUtils.IS_OS_WINDOWS
			? new File("C:\\Users\\sander\\Documents\\svn\\27 - stochastic Petri nets\\experiments\\changing")
			: new File("/home/sander/Documents/svn/27 - stochastic Petri nets/experiments/changing/");

	private static final File outputFile = new File(baseDirectory, "results.txt");
	private static final int minUnfolding = 2;
	private static final int stepUnfolding = 2;
	private static final int maxUnfolding = 98;

	private List<Algorithm> algorithms = new ArrayList<>();
	private Map<String, String> log2abbreviation = new THashMap<>();
	private Map<String, String> log2sortName = new THashMap<>();

	public ExperimentParameters() {
		//algorithms.add(new AlgorithmAndreas());
		//algorithms.add(new AlgorithmStochasticFlower());
		algorithms.add(new AlgorithmInductiveMinerStochasticDumb());

		log2abbreviation.put("bpic18 Control summary.xes.gz", "BPIC18-c");
		log2abbreviation.put("bpic18 Department control parcels.xes.gz", "BPIC18-d");
		log2abbreviation.put("bpic18 Entitlement application.xes.gz", "BPIC18-e");
		log2abbreviation.put("bpic18 Geo parcel document.xes.gz", "BPIC18-g");
		log2abbreviation.put("bpic18 Inspection.xes.gz", "BPIC18-i");
		log2abbreviation.put("bpic18 Parcel document.xes.gz", "BPIC18-pd");
		log2abbreviation.put("bpic18 Payment application.xes.gz", "BPIC18-pa");
		log2abbreviation.put("bpic18 Reference alignment.xes.gz", "BPIC18-r");
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

	public File getDiscoveredModelsDirectory() {
		return new File(baseDirectory, "1-discoveredmodels");
	}

	public File getMeasureDirectory() {
		return new File(baseDirectory, "2-measures");
	}

	public File getLanguageSizesDirectory() {
		return new File(baseDirectory, "3-languageSizes");
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
		return algorithms;
	}

	public static Iterable<Integer> getUnfoldings() {
		return new Iterable<Integer>() {
			public Iterator<Integer> iterator() {
				return new Iterator<Integer>() {
					int now = minUnfolding - stepUnfolding;

					public Integer next() {
						now += stepUnfolding;
						return now;
					}

					public boolean hasNext() {
						return now < maxUnfolding;
					}
				};
			}
		};
	}

	public int getNumberOfUnfoldings() {
		return (maxUnfolding - minUnfolding) / stepUnfolding;
	}

}

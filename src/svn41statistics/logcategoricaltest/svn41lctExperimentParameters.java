package svn41statistics.logcategoricaltest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang.SystemUtils;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.plugins.inductiveminer2.attributes.AttributeImpl;
import org.processmining.plugins.inductiveminer2.attributes.AttributeImpl.Type;

import gnu.trove.map.hash.THashMap;

public class svn41lctExperimentParameters {

	public static final Semaphore svn = new Semaphore(1);

	public static final File baseDirectory = SystemUtils.IS_OS_LINUX ? new File(
			"/home/sander/Documents/svn/41 - stochastic statistics/experiments/09 - log categorical test sensitivity")
			: new File(
					"C:\\Users\\leemans2\\Documents\\svn\\41 - stochastic statistics\\experiments\\09 - log categorical test sensitivity");

	Map<String, Attribute> log2attribute = new THashMap<>();

	private int sampleSizeMin = 100;
	private int sampleSizeMax = 1500;
	private int sampleSizeStep = 100;

	private int numberOfSamplesMin = 100;
	private int numberOfSamplesMax = 1500;
	private int numberOfSamplesStep = 100;

	public svn41lctExperimentParameters() {
		log2attribute.put("Road fines with trace attributes.xes.gz", new AttributeImpl("vehicleClass", Type.literal));
		log2attribute.put("BPIC15_1.xes.gz", new AttributeImpl("parts", Type.literal));
		log2attribute.put("BPIC15_merged.xes.gz", new AttributeImpl("fromLog", Type.literal));
		log2attribute.put("bpic11.xes.gz", new AttributeImpl("diagnosis_1", Type.literal));
		//		log2attribute.put("bpic12-a.xes.gz", new AttributeImpl("AMOUNT_REQ", Type.numeric));
		log2attribute.put("BPI Challenge 2017.xes.gz", new AttributeImpl("LoanGoal", Type.literal));
		log2attribute.put("BPI_Challenge_2019-amount lifted.xes.gz", new AttributeImpl("Item Type", Type.literal));
	}

	public List<svn41lctCall> getCalls() {
		List<svn41lctCall> result = new ArrayList<>();

		File[] files = getLogDirectory().listFiles();
		for (File logFile : files) {
			if (logFile.getName().endsWith(".xes.gz") && log2attribute.containsKey(logFile.getName())) {
				for (int numberOfSamples : getNumbersOfSamples()) {
					for (int sampleSize : getSampleSizes()) {
						result.add(new svn41lctCall(logFile.getName(), numberOfSamples, sampleSize, this,
								log2attribute.get(logFile.getName())));
					}
				}
			} else {
				System.out.println("log file " + logFile.getName()
						+ " skipped as it's not a .xes.gz file or no attribute is defined for it");
			}
		}

		return result;

	}

	public List<Integer> getSampleSizes() {
		List<Integer> result = new ArrayList<>();
		for (int sampleSize = sampleSizeMin; sampleSize <= sampleSizeMax; sampleSize += sampleSizeStep) {
			result.add(sampleSize);
		}
		return result;
	}

	public List<Integer> getNumbersOfSamples() {
		List<Integer> result = new ArrayList<>();
		for (int numberOfSamples = numberOfSamplesMin; numberOfSamples <= numberOfSamplesMax; numberOfSamples += numberOfSamplesStep) {
			result.add(numberOfSamples);
		}
		return result;
	}

	public File getBaseDirectory() {
		return baseDirectory;
	}

	public File getLogDirectory() {
		return new File(baseDirectory.getParentFile(), "00 - base logs");
	}

	public File getAssociationDirectory() {
		return new File(baseDirectory, "1-values");
	}

	public File getResultsDirectory() {
		return new File(baseDirectory, "2-results");
	}
}
package svn41statistics.sensitivity.ConformanceNumericalAssociation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang.SystemUtils;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.plugins.inductiveminer2.attributes.AttributeImpl;
import org.processmining.plugins.inductiveminer2.attributes.AttributeImpl.Type;

import gnu.trove.map.hash.THashMap;

public class svn41cnaExperimentParameters {

	public static final Semaphore svn = new Semaphore(1);

	public static final File baseDirectory = SystemUtils.IS_OS_LINUX ? new File(
			"/home/sander/Documents/svn/41 - stochastic statistics/experiments/07 - conformance numerical association sensitivity")
			: new File(
					"C:\\Users\\leemans2\\Documents\\svn\\41 - stochastic statistics\\experiments\\07 - conformance numerical association sensitivity");

	private List<Algorithm> algorithms = new ArrayList<>();
	Map<String, Attribute> log2attribute = new THashMap<>();

	private int samplesMin = 1000;
	private int samplesMax = 15000;
	private int sampleStep = 1000;

	public svn41cnaExperimentParameters() {
		algorithms.add(new AlgorithmDFM());
		//algorithms.add(new AlgorithmIMf());

		log2attribute.put("Road fines with trace attributes.xes.gz", new AttributeImpl("amount", Type.numeric));
		log2attribute.put("bpic11.xes.gz", new AttributeImpl("Age", Type.numeric));
		log2attribute.put("bpic12-a.xes.gz", new AttributeImpl("AMOUNT_REQ", Type.numeric));
		log2attribute.put("BPI Challenge 2017.xes.gz", new AttributeImpl("RequestedAmount", Type.numeric));
		log2attribute.put("BPI_Challenge_2019-amount lifted.xes.gz",
				new AttributeImpl("Cumulative net worth (EUR)", Type.numeric));
		log2attribute.put("bpic20-DomesticDeclarations.xes.gz",
				new AttributeImpl("Amount", Type.numeric));
	}

	public List<svn41cnaCall> getCalls() {
		List<svn41cnaCall> result = new ArrayList<>();

		File[] files = getLogDirectory().listFiles();
		for (File logFile : files) {
			if (logFile.getName().endsWith(".xes.gz") && log2attribute.containsKey(logFile.getName())) {
				for (Algorithm algorithm : getAlgorithms()) {
					for (int noiseAmount : getSamples()) {
						result.add(new svn41cnaCall(logFile.getName(), algorithm, noiseAmount, this,
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

	public Iterable<Integer> getSamples() {
		List<Integer> result = new ArrayList<>();
		for (int noiseAmount = samplesMin; noiseAmount <= samplesMax; noiseAmount += sampleStep) {
			result.add(noiseAmount);
		}
		return result;
	}

	public File getBaseDirectory() {
		return baseDirectory;
	}

	public File getLogDirectory() {
		return new File(baseDirectory.getParentFile(), "00 - base logs");
	}

	public File getDiscoveredModelsDirectory() {
		return new File(baseDirectory, "1-discoveredmodels");
	}

	public File getAssociationDirectory() {
		return new File(baseDirectory, "2-association");
	}

	public File getBaselineDirectory() {
		return new File(baseDirectory, "3-baseline");
	}

	public File getResultsDirectory() {
		return new File(baseDirectory, "4-results");
	}

	public List<Algorithm> getAlgorithms() {
		return Collections.unmodifiableList(algorithms);
	}

}
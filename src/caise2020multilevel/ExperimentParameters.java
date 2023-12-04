package caise2020multilevel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.classification.XEventResourceClassifier;

import gnu.trove.map.hash.THashMap;

public class ExperimentParameters {

	private static final File baseDirectory = new File(
			"C:\\Users\\sander\\Documents\\svn\\22 - multi-level process mining\\experiments");

	private static final File outputFile = new File(baseDirectory, "results.txt");
	private static final File outputLatexFile = new File(baseDirectory, "results.tex");

	private static final int folds = 3;
	private static final int repeat = 10;

	private List<Algorithm> algorithms = new ArrayList<>();
	private List<Measure> measures = new ArrayList<>();
	private Map<String, String> log2abbreviation = new THashMap<>();

	private Map<String, XEventClassifier[]> log2classifiers = new THashMap<>();

	public ExperimentParameters() {
		algorithms.add(new AlgorithmFlower());
		algorithms.add(new AlgorithmTrace());
		algorithms.add(new AlgorithmDFMM());
		algorithms.add(new AlgorithmSplitMiner());
		algorithms.add(new AlgorithmIMf());
		algorithms.add(new AlgorithmMLMFF());
		algorithms.add(new AlgorithmMLMDD());
		algorithms.add(new AlgorithmMLMII());
		algorithms.add(new AlgorithmMLMSS());
		algorithms.add(new AlgorithmMLMDI());

		measures.add(new MeasureProjected());
		measures.add(new MeasureAutomated());
		measures.add(new MeasureUser());
		//measures.add(new MeasureFlatSize());
		//measures.add(new MeasureFlatAverageConnectorDegree());
		//measures.add(new MeasureUserSize());
		//measures.add(new MeasureAverageConnectorDegree());
		//measures.add(new MeasureCardoso());

		XEventClassifier cEventName = new XEventNameClassifier();
		XEventClassifier cResource = new XEventResourceClassifier();
		XEventClassifier cLifecycle = new XEventAttributeClassifier("lifecycle:transition", "lifecycle:transition");

		putClassifiers("bpic12.xes.gz", cEventName, cLifecycle);
		putClassifiers("bpic13 closed problems.xes.gz", cEventName, cLifecycle);
		putClassifiers("bpic15-1.xes.gz", //
				new XEventAttributeClassifier("monitoringResource", "monitoringResource"), //
				new XEventAttributeClassifier("activityNameEN", "activityNameEN"));
		putClassifiers("bpic17.xes.gz", new XEventAttributeClassifier("EventOrigin", "EventOrigin"), //
				cEventName);
		putClassifiers("bpic18 Payment application.xes.gz", new XEventAttributeClassifier("subprocess", "subprocess"), //
				cEventName, cLifecycle);
		putClassifiers("sepsis.xes.gz", //
				new XEventAttributeClassifier("org:group", "org:group"), //
				cEventName);

		log2abbreviation.put("bpic12.xes.gz", "bpic12");
		log2abbreviation.put("bpic13 closed problems.xes.gz", "bpic13-cp");
		log2abbreviation.put("bpic15-1.xes.gz", "bpic15-1");
		log2abbreviation.put("bpic17.xes.gz", "bpic17");
		log2abbreviation.put("bpic18 Payment application.xes.gz", "bpic18-pa");
		log2abbreviation.put("sepsis.xes.gz", "sepsis");
	}

	private void putClassifiers(String logName, XEventClassifier... classifiers) {
		log2classifiers.put(logName, classifiers);
	}

	public File getBaseDirectory() {
		return baseDirectory;
	}

	public File getLogDirectory() {
		return new File(baseDirectory, "0 - logs");
	}

	public File getTransformedLogDirectory() {
		return new File(baseDirectory, "0 - transformedLogs");
	}

	public File getSplitLogDirectory() {
		return new File(baseDirectory, "1 - splitLogs");
	}

	public File getModelsDirectory() {
		return new File(baseDirectory, "2 - models");
	}

	public File getFlattenedModelsDirectory() {
		return new File(baseDirectory, "3 - flattenedmodels");
	}

	public File getMeasuresDirectory() {
		return new File(baseDirectory, "4 - measures");
	}

	public static File getOutputFile() {
		return outputFile;
	}

	public static File getOutputLatexFile() {
		return outputLatexFile;
	}

	public List<Algorithm> getAlgorithms() {
		return algorithms;
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

	public List<Measure> getMeasures() {
		return Collections.unmodifiableList(measures);
	}

	public List<Measure> getAllMeasures() {
		return Collections.unmodifiableList(measures);
	}

	public XEventClassifier[] getClassifiers(File logFile) {
		return log2classifiers.get(logFile.getName());
	}

	public String getLogAbbreviation(File logFile) {
		return log2abbreviation.get(logFile.getName());
	}
}
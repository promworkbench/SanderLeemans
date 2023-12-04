package thesis.evaluation.logconformancewithkfoldcrossvalidation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.Function;

import thesis.helperClasses.XLogWriterIncremental;
import thesis.helperClasses.XLogParserIncremental;

public class KFoldCrossValidationSplitLog {

	public static final int k = 6;
	public static final int discoveryPart = 4;

	public static void main(String[] args) throws Exception {
		final Random random = new Random(1);

		boolean help = false;
		File directory = null;
		if (args.length != 1) {
			help = true;
		} else {
			directory = new File(args[0]);
			help = help || !directory.exists() || !directory.isDirectory();
		}

		if (help) {
			System.out.println("Usage: directory");
			System.out.println(" Logs should be in XES-format (compressed XES supported).");
			return;
		}

		for (final File file : directory.listFiles()) {
			if (FilenameUtils.getExtension(file.getAbsolutePath()).equals("xes")
					|| FilenameUtils.getExtension(file.getAbsolutePath()).equals("gz")) {

				final List<int[]> combinations = binom(k, discoveryPart);
				final XLogWriterIncremental[] discoveryLogs = new XLogWriterIncremental[combinations.size()];
				final XLogWriterIncremental[] testLogs = new XLogWriterIncremental[combinations.size()];
				{
					int i = 0;
					for (int[] trace : combinations) {
						discoveryLogs[i] = new XLogWriterIncremental(new File(
								"D:\\svn\\00 - the beast\\experiments\\logQuality\\data-discovery\\"
										+ file.getName() + "-discovery" + i + ".xes.gz"));
						testLogs[i] = new XLogWriterIncremental(new File(
								"D:\\svn\\00 - the beast\\experiments\\logQuality\\data-test\\" + file.getName()
										+ "-test" + i + ".xes.gz"));
						i++;
					}
				}

				XLogParserIncremental.parseTraces(file, new Function<XTrace, Object>() {
					public Object call(XTrace input) throws Exception {
						int inLog = random.nextInt(k);
						for (int combination = 0; combination < combinations.size(); combination++) {
							// for each combination, the trace goes into either the discovery or the test log
							if (ArrayUtils.contains(combinations.get(combination), inLog)) {
								discoveryLogs[combination].writeTrace(input);
							} else {
								testLogs[combination].writeTrace(input);
							}
						}

						return null;
					}
				});

				for (int i = 0; i < combinations.size(); i++) {
					discoveryLogs[i].close();
					testLogs[i].close();
				}
			}
		}
	}

	/**
	 * r choose k; return all possibilities
	 * 
	 * @param r
	 * @param k
	 */
	public static List<int[]> binom(int r, int k) {
		int[] input = new int[r]; // input array
		for (int i = 0; i < r; i++) {
			input[i] = i;
		}

		List<int[]> subsets = new ArrayList<>();

		int[] s = new int[k]; // here we'll keep indices 
								// pointing to elements in input array

		if (k <= input.length) {
			// first index sequence: 0, 1, 2, ...
			for (int i = 0; (s[i] = i) < k - 1; i++) {
			}
			subsets.add(getSubset(input, s));
			for (;;) {
				int i;
				// find position of item that can be incremented
				for (i = k - 1; i >= 0 && s[i] == input.length - k + i; i--) {
				}
				if (i < 0) {
					break;
				} else {
					s[i]++; // increment this item
					for (++i; i < k; i++) { // fill up remaining items
						s[i] = s[i - 1] + 1;
					}
					subsets.add(getSubset(input, s));
				}
			}
		}
		return subsets;
	}

	// generate actual subset by index sequence
	private static int[] getSubset(int[] input, int[] subset) {
		int[] result = new int[subset.length];
		for (int i = 0; i < subset.length; i++) {
			result[i] = input[subset[i]];
		}
		return result;
	}
}

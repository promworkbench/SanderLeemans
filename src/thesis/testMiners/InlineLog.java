package thesis.testMiners;

import java.util.Random;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;

import p2015sosym.efficienttree.generatebehaviour.GenerateLog;

/**
 * Construct a log if the activities are known beforehand.
 * 
 * @author sleemans
 *
 */
public class InlineLog {

	public static long randomSeed = 1;

	public static int[][] trace(int... events) {
		int[][] result = new int[1][];
		result[0] = events;
		return result;
	}

	public static int[][] fromTree(EfficientTree tree, int numberOfTraces) {
		int[][] result = new int[numberOfTraces][];
		Random randomGenerator = new Random(randomSeed);
		int i = 0;
		for (int[] trace : GenerateLog.generateTraces(tree, numberOfTraces, randomGenerator, false)) {
			result[i] = trace;
			i++;
		}
		return result;
	}

	public static int[][] multiply(int cardinality, int[][]... sublogs) {
		//count the number of traces
		int count = 0;
		for (int[][] sublog : sublogs) {
			count += sublog.length;
		}

		int[][] result = new int[cardinality * count][];
		count = 0;
		for (int sublog = 0; sublog < sublogs.length; sublog++) {
			for (int trace = 0; trace < sublogs[sublog].length; trace++) {
				for (int i = 0; i < cardinality; i++) {
					result[count] = sublogs[sublog][trace];
					count++;
				}
			}
		}
		return result;
	}
	
	public static int[][] log(int[][]... sublogs) {
		return multiply(1, sublogs);
	}
	
	public static int[][] log(int[][] sublog, int[][][] sublogs) {
		int[][][] result = new int[sublogs.length + 1][][];
		for (int i = 0; i < sublogs.length ; i++) {
			result[i] = sublogs[i];
		}
		result[sublogs.length] = sublog;
		return multiply(1, result);
	}

	public static XLog log(String[] int2activity, int[][]... sublogs) {
		XAttributeMap logMap = new XAttributeMapImpl();
		GenerateLog.putLiteral(logMap, "concept:name", "generated log from process tree");
		XLog result = new XLogImpl(logMap);
		for (int[][] sublog : sublogs) {
			for (int[] trace : sublog) {
				result.add(GenerateLog.trace2xTrace(int2activity, trace));
			}
		}
		return result;
	}
}
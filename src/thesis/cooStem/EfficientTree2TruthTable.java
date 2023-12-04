package thesis.cooStem;

import java.util.List;
import java.util.Random;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;

import p2015sosym.efficienttree.generatebehaviour.GenerateLog;

public class EfficientTree2TruthTable {
	
	public static int numberOfTraces = 10000;
	
	public static TruthTable convert(EfficientTree tree) throws Exception {
		TruthTable result = new TruthTable(tree.getInt2activity().length);
		
		//generate traces
		Random random = new Random();
		for (int[] trace : GenerateLog.generateTraces(tree, numberOfTraces, random, false)) {
			boolean[] keys = new boolean[tree.getInt2activity().length];
			for (int activity : trace) {
				keys[activity] = true;
			}
			result.set(true, keys);
		}
		
		return result;
	}
	
	public static TruthTable convert2(EfficientTree tree) throws Exception {
		TruthTable result = new TruthTable(tree.getInt2activity().length);
		
		//generate traces
		List<int[]> traces = GenerateCompleteLog.generate(tree);
		for (int[] trace : traces) {
			boolean[] keys = new boolean[tree.getInt2activity().length];
			for (int activity : trace) {
				keys[activity] = true;
			}
			result.set(true, keys);
		}
		
		return result;
	}
}

package generation;

import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.processmining.processtree.Block;
import org.processmining.processtree.Block.And;
import org.processmining.processtree.Block.Def;
import org.processmining.processtree.Block.DefLoop;
import org.processmining.processtree.Block.Seq;
import org.processmining.processtree.Block.Xor;
import org.processmining.processtree.Block.XorLoop;
import org.processmining.processtree.Node;
import org.processmining.processtree.Task.Automatic;
import org.processmining.processtree.Task.Manual;

public class GenerateLanguage {
	
	
	public static final ConcurrentHashMap<String, Set<String>> cache = new ConcurrentHashMap<>(); 
	
	/**
	 * Get the language of this tree as a set of traces. Limitations: 1) each
	 * loop is executed at most three times, 2) node names may be only 1
	 * character long.
	 * @param node
	 * @param hash
	 * @return
	 */
	public static Set<String> get(Node node, String hash) {
		if (!cache.containsKey(hash)) {
			cache.put(hash, generate(node));
//			System.out.println("generate");
		} else {
//			System.out.println("from cache");
		}
		return cache.get(hash);
	}

	public static Set<String> generate(Node node) {
		if (node instanceof Automatic) {
			Set<String> result = new THashSet<String>();
			result.add("");
			return result;
		} else if (node instanceof Manual) {
			Set<String> result = new THashSet<String>();
			result.add(node.getName());
			return result;
		} else if (node instanceof Seq) {
			Node[] array = new Node[((Block) node).getChildren().size()];
			((Block) node).getChildren().toArray(array);
			return getLanguageSeq(array);
		} else if (node instanceof DefLoop || node instanceof XorLoop) {
			return getLanguageLoop(((Block) node).getChildren().get(0), ((Block) node).getChildren().get(1),
					((Block) node).getChildren().get(2));
		} else if (node instanceof Xor || node instanceof Def) {
			Node[] array = new Node[((Block) node).getChildren().size()];
			((Block) node).getChildren().toArray(array);
			return getLanguageXor(array);
		} else if (node instanceof And) {
			Node[] array = new Node[((Block) node).getChildren().size()];
			((Block) node).getChildren().toArray(array);
			return getLanguageAnd(array);
		}

		return null;
	}

	public static Set<String> getLanguageXor(Node... children) {
		Set<String> result = new THashSet<>();
		for (Node child : children) {
			result.addAll(generate(child));
		}
		return result;
	}

	public static Set<String> getLanguageLoop(Node body, Node redo, Node exit) {
		Set<String> result;
		//body once
		{
			Node[] once = new Node[2];
			once[0] = body;
			once[1] = exit;
			result = getLanguageSeq(once);
		}

		//body twice
		{
			Node[] twice = new Node[4];
			twice[0] = body;
			twice[1] = redo;
			twice[2] = body;
			twice[3] = exit;
			result.addAll(getLanguageSeq(twice));
		}

		//body three times
		{
			Node[] thrice = new Node[6];
			thrice[0] = body;
			thrice[1] = redo;
			thrice[2] = body;
			thrice[3] = redo;
			thrice[4] = body;
			thrice[5] = exit;
			result.addAll(getLanguageSeq(thrice));
		}

		//body four times
		{
			Node[] four = new Node[8];
			four[0] = body;
			four[1] = redo;
			four[2] = body;
			four[3] = redo;
			four[4] = body;
			four[5] = redo;
			four[6] = body;
			four[7] = exit;
			result.addAll(getLanguageSeq(four));
		}
		return result;
	}

	public static Set<String> getLanguageSeq(Node... children) {
		//fetch the language of all children and count
		List<List<String>> subLogs = new ArrayList<>();
		long possibilities = 1;
		for (Node child : children) {
			List<String> subLog = new ArrayList<>(generate(child));
			subLogs.add(subLog);
			possibilities *= subLog.size();
		}

		//combine all possibilities
		Set<String> result = new THashSet<>();
		int[] choose = new int[children.length];
		for (int i = 0; i < possibilities; i++) {
			StringBuilder trace = new StringBuilder();
			for (int j = 0; j < children.length; j++) {
				trace.append(subLogs.get(j).get(choose[j]));
			}
			result.add(trace.toString());

			//progress counter
			if (i < possibilities - 1) {
				int childNr = 0;
				choose[childNr]++;
				while (choose[childNr] >= subLogs.get(childNr).size()) {
					choose[childNr] = 0;
					childNr++;
					choose[childNr]++;
				}
			}

		}

		return result;
	}

	public static Set<String> getLanguageAnd(Node... children) {
		//fetch the language of all children and count (first level: traces of children)
		List<List<String>> subLogs = new ArrayList<>();
		long possibilities = 1;
		for (Node child : children) {
			List<String> subLog = new ArrayList<>(generate(child));
			subLogs.add(subLog);
			possibilities *= subLog.size();
		}

		//combine all possibilities
		Set<String> result = new THashSet<>();
		int[] choose = new int[children.length];
		for (int i = 0; i < possibilities; i++) {
			String[] subTraces = new String[children.length];
			for (int j = 0; j < children.length; j++) {
				subTraces[j] = subLogs.get(j).get(choose[j]);
			}
			result.addAll(shuffleProduct(subTraces));

			//progress counters
			if (i < possibilities - 1) {
				int childNr = 0;
				choose[childNr]++;
				while (choose[childNr] >= subLogs.get(childNr).size()) {
					choose[childNr] = 0;
					childNr++;
					choose[childNr]++;
				}
			}
		}

		return result;
	}

	/**
	 * Construct the shuffle product of the given traces.
	 * 
	 * @param traces
	 * @return
	 */
	public static Set<String> shuffleProduct(String... traces) {
		//idea: iteratively add a single trace to the existing traces.
		Set<String> result = new THashSet<>();
		result.add(traces[0]);
		for (int i = 1; i < traces.length; i++) {
			Set<String> newResult = new THashSet<>();
			for (String previous : result) {
				newResult.addAll(shuffleProduct2(traces[i], previous));
			}
			result = newResult;
		}

		return result;
	}

	private static Set<String> shuffleProduct2(String trace1, String trace2) {
		Set<String> result = new THashSet<>();
		if (trace1.isEmpty()) {
			result.add(trace2);
			return result;
		} else if (trace2.isEmpty()) {
			result.add(trace1);
			return result;
		}

		//one option is to start with a character of the first trace
		{
			String letter = trace1.substring(0, 1);
			Set<String> subResult = shuffleProduct2(trace1.substring(1), trace2);
			for (String subTrace : subResult) {
				result.add(letter + subTrace);
			}
		}

		//another option is to start with a character of the second trace
		{
			String letter = trace2.substring(0, 1);
			Set<String> subResult = shuffleProduct2(trace1, trace2.substring(1));
			for (String subTrace : subResult) {
				result.add(letter + subTrace);
			}
		}

		return result;
	}
}

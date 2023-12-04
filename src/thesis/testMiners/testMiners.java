package thesis.testMiners;

import thesis.testMiners.testGraphs.testGraphs;
import thesis.testMiners.testGraphs.testIntGraphs;

public class testMiners {
	private static boolean stopAtError = true;

	public static void main(String... args) throws Exception {
		if (!testGraphs.test(stopAtError))
			return;
		if (!testIntGraphs.test(stopAtError))
			return;
		if (!testReductionRules.test(stopAtError))
			return;
		if (!TestRediscoverability.test(stopAtError))
			return;
		if (!testNoise.test(stopAtError))
			return;
		System.out.println("done");
	}

}

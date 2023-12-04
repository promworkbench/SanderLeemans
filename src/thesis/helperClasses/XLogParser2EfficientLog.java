package thesis.helperClasses;

import java.io.File;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.Function;
import org.processmining.projectedrecallandprecision.helperclasses.EfficientLog;

public class XLogParser2EfficientLog {

	public static EfficientLog load(File file, final XEventClassifier classifier) throws Exception {
		final EfficientLog result = new EfficientLog();
		XLogParserIncremental.parseTraces(file, new Function<XTrace, Object>() {
			public Object call(XTrace input) throws Exception {
				result.addTrace(input, classifier);
				return null;
			}
		});
		result.finalise();
		return result;
	}
}

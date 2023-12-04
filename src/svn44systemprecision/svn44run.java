package svn44systemprecision;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.directlyfollowsmodelminer.mining.DFMMiningParametersAbstract;
import org.processmining.directlyfollowsmodelminer.mining.plugins.DirectlyFollowsModelMinerPlugin;
import org.processmining.directlyfollowsmodelminer.mining.variants.DFMMiningParametersDefault;
import org.processmining.directlyfollowsmodelminer.model.DirectlyFollowsModel;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.xeslite.plugin.OpenLogFileLiteImplPlugin;

import svn27emscpartialorders.Perform3Measures;
import thesis.helperClasses.FakeContext;

public class svn44run {
	public static void main(String[] args) throws Exception {
		//load log
		File logFile = new File("/home/sander/Desktop/BPIC'12 a_activities.xes");
		PluginContext context = new FakeContext();
		XLog log = (XLog) new OpenLogFileLiteImplPlugin().importFile(context, logFile);
		int maxTraceLength = getMaxTraceLength(log);

		//measure DFM of log
		long logDFMsize;
		{
			DFMMiningParametersAbstract parameters = new DFMMiningParametersDefault();
			parameters.setNoiseThreshold(1);
			Canceller canceller = new Canceller() {
				public boolean isCancelled() {
					return false;
				}
			};
			DirectlyFollowsModel dfm = DirectlyFollowsModelMinerPlugin.mine(log, parameters, canceller);
			logDFMsize = DFMSize.getNumberOfTracesInDFM(dfm, maxTraceLength);
		}

		for (int noise = 0; noise <= 100; noise += 10) {
			//get model
			File modelFile = File.createTempFile("log", ".pnml");
			{
				//System.out.println(modelFile.getAbsolutePath());
				modelFile.deleteOnExit();
				new AlgorithmWeightFrequency().run(logFile, log, modelFile, noise / 100f);
			}

			StochasticNet net = Perform3Measures.loadNet(modelFile);
			String stochasticLanguage = ListModelLanguage.convert(net, maxTraceLength);

			File stochasticFile = new File(
					"/home/sander/Desktop/BPIC'12 a_activities-stochastic language-DFM-n" + noise + ".txt");
			FileUtils.write(stochasticFile, "logDFMsize " + logDFMsize + "\n" + stochasticLanguage);

			modelFile.delete();
		}
	}

	public static int getMaxTraceLength(XLog log) {
		int result = 0;
		for (XTrace trace : log) {
			result = Math.max(result, trace.size());
		}
		return result;
	}
}

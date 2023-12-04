package batch;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.InductiveMiner.Quintuple;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.InductiveMiner.jobList.ThreadPoolMiner;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.graphviz.dot.Dot2Image;
import org.processmining.plugins.graphviz.dot.Dot2Image.Type;
import org.processmining.plugins.inductiveVisualMiner.plugins.GraphvizPetriNet;
import org.processmining.plugins.log.OpenLogFilePlugin;
import org.processmining.plugins.petrinet.behavioralanalysis.woflan.Woflan;
import org.processmining.plugins.petrinet.behavioralanalysis.woflan.WoflanDiagnosis;

import batch.CompareMinersTimers.CompareMinersTimer;
import batch.miners.Miner;
import batch.miners.Trace;

@Plugin(name = "Batch compare miners", returnLabels = { "Compare Miners result", "Dummy" }, returnTypes = {
		CompareMinersResult.class,
		CompareMinersResult.class }, parameterLabels = { "Log", "Parameters" }, userAccessible = true)
public class CompareMiners {
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Batch compare miners, default", requiredParameterLabels = {})
	public Object[] mineDefault(PluginContext context) {
		return this.mineParameters(context, new CompareMinersParameters());
	}

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Batch compare miners, parameterized", requiredParameterLabels = { 1 })
	public Object[] mineParameters(final PluginContext context, final CompareMinersParameters parameters) {

		//read all the files in the given folder
		List<File> files = getListOfFiles(parameters.getFolder(), parameters.getExtensions());
		final CompareMinersResult result = new CompareMinersResult(parameters, files);

		//run the miners
		ThreadPoolMiner pool = new ThreadPoolMiner(2);
		for (File file : files) {
			final File file2 = file;
			/*
			 * pool.addJob( new Runnable() { public void run() {
			 */
			executeComparison(context, parameters, file2, result);
			/*
			 * } });
			 */
		}

		try {
			pool.join();
		} catch (ExecutionException e) {
			//debug("something failed (thread join)");
			e.printStackTrace();
		}

		return new Object[] { result, result };
	}

	private void executeComparison(PluginContext context, CompareMinersParameters batchParameters, File file,
			CompareMinersResult result) {

		//read the log
		XLog log;
		try {
			OpenLogFilePlugin logImporter = new OpenLogFilePlugin();
			log = (XLog) logImporter.importFile(context, file);
		} catch (Exception e) {
			//debug("error encountered (log import)");
			e.printStackTrace();
			return;
		}

		result.append(file, "<br>Log " + file + "<br>");

		//call each miner to mine a Petri net
		for (Miner miner : batchParameters.getMiners()) {

			debug("Start batch mining of " + file + " by " + miner.getIdentification());
			result.append(file, "&nbsp;&nbsp;Mined by " + miner.getIdentification() + "<br>");

			XEventClassifier classifier = new XEventAndClassifier(new XEventNameClassifier(),
					new XEventLifeTransClassifier());
			XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, classifier);

			//mine a petri net and its markings
			Quintuple<Petrinet, Marking, Marking, TransEvClassMapping, Boolean> t;
			CompareMinersTimer timer = result.getTimers().startMiningTimer(file, miner);
			try {
				t = decacheOrMine(context, log, logInfo, batchParameters, classifier, result, miner, file);
			} catch (Exception e) {
				result.append(file,
						"&nbsp;&nbsp;&nbsp;&nbsp;Mining failed.(" + e.getMessage() + ")(" + timer.stop() + "ms)<br>");
				e.printStackTrace();
				continue;
			}
			if (!t.getE()) {
				result.append(file, "&nbsp;&nbsp;&nbsp;&nbsp;mining took " + timer.stop() + " ms<br>");
			} else {
				result.append(file, "&nbsp;&nbsp;&nbsp;&nbsp;petrinet loaded from cache <br>");
			}

			Petrinet petrinet = t.getA();
			Marking initialMarking = t.getB();
			Marking finalMarking = t.getC();
			TransEvClassMapping mapping = t.getD();

			//validate result
			if (petrinet == null || initialMarking == null || mapping == null) {
				result.append(file, "&nbsp;&nbsp;&nbsp;&nbsp;Mining failed to produce non-null results.<br>");
				continue;
			}

			//notify if initial or final marking is invalid
			if (initialMarking == null || initialMarking.size() == 0) {
				result.append(file, "&nbsp;&nbsp;&nbsp;&nbsp;initial marking is empty.<br>");
			}

			//report complexity
			result.reportComplexity(file, miner, petrinet.getEdges().size(), petrinet.getPlaces().size(),
					petrinet.getTransitions().size());

			//check soundness
			if (batchParameters.getCheckSoundness()) {
				Boolean sound = checkSoundness(context, file, petrinet, result);
				if (sound == null) {
					//soundness check failed
					continue;
				} else if (!sound) {
					//model is not sound
					continue;
				}
			}

			//replay the log
			if (batchParameters.getReplayLog()) {

				if (finalMarking != null && finalMarking.size() != 0) {
					//replay the log with final marking
					File replayedLogFile = getBaseFile(miner, file, "-replayedLogWithFinalMarking.csv",
							batchParameters.getPetrinetOutputFolder());
					try {
						new TimeOut().runWithHardTimeOut(
								new CompareMinersReplay(context, file, miner, log, logInfo, petrinet, initialMarking,
										finalMarking, mapping, result, true, replayedLogFile, classifier),
								batchParameters.getMaxReplayTime());
					} catch (Exception e) {
						e.printStackTrace();
						result.append(file, "&nbsp;&nbsp;&nbsp;&nbsp;Log replay with final marking failed. ("
								+ e.getMessage() + ")<br>");
					}
				} else {
					result.append(file, "&nbsp;&nbsp;&nbsp;&nbsp;replay with final marking skipped.<br>");
				}

				/*
				 * //replay the log without final marking File replayedLogFile =
				 * getBaseFile(miner, file,
				 * "-replayedLogWithoutFinalMarking.csv",
				 * batchParameters.getPetrinetOutputFolder()); try { new
				 * TimeOut().runWithHardTimeOut( new
				 * CompareMinersReplay(context, file, miner, log, logInfo,
				 * petrinet, initialMarking, null, mapping, result, false,
				 * replayedLogFile, classifier),
				 * batchParameters.getMaxReplayTime()); } catch (Exception e) {
				 * e.printStackTrace(); result.append(file,
				 * "&nbsp;&nbsp;&nbsp;&nbsp;Log replay without final marking failed. ("
				 * + e.getMessage() + ")<br>"); }
				 */

			}
		}

	}

	private Quintuple<Petrinet, Marking, Marking, TransEvClassMapping, Boolean> decacheOrMine(PluginContext context,
			XLog log, XLogInfo logInfo, CompareMinersParameters batchParameters, XEventClassifier classifier,
			CompareMinersResult result, Miner miner, File file) throws Exception {

		File pnmlFile = getBaseFile(miner, file, "-petrinet.pnml", batchParameters.getPetrinetOutputFolder());

		try {
			Triple<Petrinet, Marking, Marking> t;
			if (pnmlFile.exists()) {
				t = StorePetrinet.loadStoredPetrinet(context, pnmlFile);
			} else {
				t = StorePetrinet.loadStoredPetrinet(context,
						getBaseFile(miner, file, "-petrinet.pnml", batchParameters.getPetrinetInputFolder()));
			}

			Petrinet petrinet = t.getA();
			Marking initialMarking = t.getB();
			Marking finalMarking = t.getC();

			if (finalMarking.size() == 0) {
				finalMarking = null;
			}

			//reconstruct the mapping
			TransEvClassMapping mapping = new Trace().getTransEvClassMapping(classifier, logInfo, petrinet);

			debug("Reload Petri net");

			return new Quintuple<Petrinet, Marking, Marking, TransEvClassMapping, Boolean>(petrinet, initialMarking,
					finalMarking, mapping, true);

		} catch (Exception e) {
			//mine
			Quadruple<Petrinet, Marking, Marking, TransEvClassMapping> t = miner.mine(context, log, classifier, logInfo,
					batchParameters.getMaxMiningTime());
			Petrinet petrinet = t.getA();
			Marking initialMarking = t.getB();
			Marking finalMarking = t.getC();
			TransEvClassMapping mapping = t.getD();

			//try to guess a final marking
			boolean finalMarkingGuessed = false;
			if (finalMarking == null || finalMarking.size() == 0) {
				finalMarking = guessFinalMarking(petrinet);
				if (finalMarking == null) {
					result.append(file,
							"&nbsp;&nbsp;&nbsp;&nbsp;final marking was invalid, no probable final marking found. <br>");
				} else {
					result.append(file,
							"&nbsp;&nbsp;&nbsp;&nbsp;final marking was invalid, guessed a probable final marking. <br>");
				}
				finalMarkingGuessed = true;
			}

			//output image
			if (batchParameters.getPetrinetOutputFolder() != null) {
				File img = visualisePetrinet(petrinet, initialMarking, finalMarking, miner, file, batchParameters,
						finalMarkingGuessed);
				result.append(file,
						"<img src=\"" + img.getName() + "\" style='max-width: 1900px; max-height: 900px;'><br>");
			}

			//output file
			StorePetrinet.store(petrinet, initialMarking, finalMarking, mapping, pnmlFile);

			return new Quintuple<Petrinet, Marking, Marking, TransEvClassMapping, Boolean>(petrinet, initialMarking,
					finalMarking, mapping, false);
		}
	}

	/*
	 * Perform soundness check using Woflan returns true if the model is sound,
	 * false if the model is not sound, null if the check was unsuccessful
	 */
	private Boolean checkSoundness(PluginContext context, File file, Petrinet petrinet, CompareMinersResult result) {
		//apply woflan
		Woflan woflan = new Woflan();
		WoflanDiagnosis woflanResult;
		try {
			woflanResult = woflan.diagnose(context, petrinet);
		} catch (Exception e1) {
			e1.printStackTrace();
			result.append(file, "&nbsp;&nbsp;&nbsp;&nbsp;Woflan call failed.<br>");
			return null;
		}

		//apply ugly hack to obtain woflan final result
		Field f;
		try {
			f = woflanResult.getClass().getDeclaredField("diagnosis");
		} catch (NoSuchFieldException e2) {
			e2.printStackTrace();
			return null;
		} catch (SecurityException e2) {
			e2.printStackTrace();
			return null;
		}
		f.setAccessible(true);
		Integer woflanResultDiagnosis;
		try {
			woflanResultDiagnosis = (Integer) f.get(woflanResult);
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
			return null;
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
			return null;
		}
		if (woflanResultDiagnosis == 4) {
			result.append(file, "&nbsp;&nbsp;&nbsp;&nbsp;model is sound.<br>");
			return true;
		} else {
			result.append(file, "&nbsp;&nbsp;&nbsp;&nbsp;Model is not sound (");
			switch (woflanResultDiagnosis) {
				case 0 :
					result.append(file, "not a workflow net");
					break;
				case 1 :
					result.append(file, "unbounded");
					break;
				case 2 :
					result.append(file, "dead");
					break;
				case 3 :
					result.append(file, "not live");
					break;
			}
			result.append(file, ")<br>");
			return false;
		}
	}

	private File visualisePetrinet(
			Petrinet petrinet, 
			Marking initialMarking, 
			Marking finalMarking, 
			Miner miner,
			File file, 
			CompareMinersParameters batchParameters,
			boolean finalMarkingGuessed) {
		
		File outputFilePNG = getBaseFile(miner, file, "-petrinet.png", batchParameters.getPetrinetOutputFolder());
		File outputFilePDF = getBaseFile(miner, file, "-petrinet.pdf", batchParameters.getPetrinetOutputFolder());
		Dot2Image.dot2image(GraphvizPetriNet.convert(petrinet, initialMarking, Arrays.asList(new Marking[]{finalMarking})), outputFilePNG, Type.png);
		Dot2Image.dot2image(GraphvizPetriNet.convert(petrinet, initialMarking, Arrays.asList(new Marking[]{finalMarking})), outputFilePDF, Type.pdf);
		return outputFilePNG;
	}

	/*
	 * Not all miners provide a proper final marking Try to guess it: if there
	 * is exactly one place having no output arcs, then that is the final
	 * marking
	 * 
	 * designed to be called from CompareMiners, as its call must be recorded
	 * for scientific reproducibility
	 */
	private Marking guessFinalMarking(Petrinet petrinet) {
		Marking newFinalMarking = new Marking();
		for (Place place : petrinet.getPlaces()) {
			if (petrinet.getOutEdges(place).isEmpty()) {
				newFinalMarking.add(place);
			}
		}

		if (newFinalMarking.size() == 1) {
			return newFinalMarking;
		}
		return null;
	}

	private List<File> getListOfFiles(File file, Set<String> extensions) {
		List<File> result = new LinkedList<File>();
		if (file.isFile()) {
			String name = file.getName();
			if (extensions.contains(name.substring(name.length() - 4, name.length()))) {
				result.add(file);
			}
		} else if (file.isDirectory()) {
			File[] listOfFiles = file.listFiles();
			if (listOfFiles != null) {
				for (int i = 0; i < listOfFiles.length; i++) {
					result.addAll(getListOfFiles(listOfFiles[i], extensions));
				}
			}
		}
		return result;
	}

	private static File getBaseFile(Miner miner, File file, String postfix, File folder) {
		String x = file.getName();
		if (x.indexOf(".") > 0) {
			x = x.substring(0, x.lastIndexOf("."));
		}
		return new File(folder, x + "-" + miner.getIdentification() + postfix);
	}

	private void debug(String s) {
		System.out.println(s);
	}
}

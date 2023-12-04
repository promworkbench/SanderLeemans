package thesis.evaluation.rediscoverability.noise;

import java.io.File;
import java.util.Random;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;

import gnu.trove.list.array.TIntArrayList;
import p2015sosym.efficienttree.generatebehaviour.GenerateLog;
import thesis.evaluation.logconformancewithkfoldcrossvalidation.kFoldCrossValidationConformance;
import thesis.helperClasses.XLogWriterIncremental;

public class NoiseIncompletenessGenerateLogs {
	public static void main(String[] args) throws Exception {

		for (File modelFile : NoiseSettings.generatedModelDirectory.listFiles()) {
			EfficientTree tree = kFoldCrossValidationConformance.loadEfficientTree(modelFile);

			int logRound = 13;

			for (int noiseRound = 0; noiseRound < NoiseSettings.noiseRounds; noiseRound++) {
				File logFile = new File(NoiseSettings.logDirectory,
						modelFile.getName() + "-logRound" + logRound + "-noiseRound" + noiseRound + ".xes.gz");

				if (!logFile.exists()) {
					int logSize = (int) Math.pow(NoiseSettings.logIncreaseFactor, logRound);
					int noiseSize = (int) Math.pow(NoiseSettings.noiseIncreaseFactor, noiseRound);
					XLogWriterIncremental logWriter = new XLogWriterIncremental(logFile);

					//complete log without noise
					{
						Random logRandom = new Random(1);
						for (int[] trace : GenerateLog.generateTraces(tree, logSize, logRandom, false)) {
							logWriter.writeTrace(trace, tree.getInt2activity());
						}
					}

					//complete log with noise
					{
						//make a noise map
						Random noiseRandom = new Random(1);
						int[] noiseEventsToAdd = new int[(int) logSize];
						for (int noiseEvent = 0; noiseEvent < noiseSize; noiseEvent++) {
							noiseEventsToAdd[noiseRandom.nextInt(logSize)]++;
						}

						Random logRandom = new Random(1);
						int traceNumber = 0;
						for (int[] trace : GenerateLog.generateTraces(tree, logSize, logRandom, false)) {
							TIntArrayList newTrace = new TIntArrayList(trace);

							//insert the required number of noise events
							for (int noiseEvent = 0; noiseEvent < noiseEventsToAdd[traceNumber]; noiseEvent++) {
								newTrace.insert(noiseRandom.nextInt(newTrace.size() + 1),
										noiseRandom.nextInt(tree.getInt2activity().length));
							}

							logWriter.writeTrace(newTrace.iterator(), tree.getInt2activity());
							traceNumber++;
						}
					}

					logWriter.close();
				}
			}
		}
	}
}

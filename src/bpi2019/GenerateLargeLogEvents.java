package bpi2019;

import java.io.File;
import java.util.Random;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeExportPlugin;

import p2015sosym.efficienttree.generatebehaviour.GenerateLog;
import thesis.helperClasses.XLogWriterIncremental;

public class GenerateLargeLogEvents {

	public static void main(String[] args) throws Exception {

		File folder = new File("C:\\Users\\sander\\Documents\\datasets\\Pouneh Samadi");

		EfficientTree tree = InlineTree.xor( //
				InlineTree.seq(InlineTree.leaf("a"), InlineTree.leaf("b"), InlineTree.leaf("c"), InlineTree.leaf("d")), //
				InlineTree.and(InlineTree.leaf("e"), InlineTree.leaf("f"), InlineTree.leaf("g"), InlineTree.leaf("h")), // 
				InlineTree.loop(InlineTree.leaf("i"), InlineTree.leaf("j"), InlineTree.leaf("k")));

		System.out.println(tree);

		//write to file
		EfficientTreeExportPlugin.export(tree, new File(folder, "events-tree.tree"));

		long logSize = (long) Math.pow(10, 7);
		long randomSeed = 1;
		Random random = new Random(randomSeed);

		//generate log
		long time = 0;
		XLogWriterIncremental logWriter = new XLogWriterIncremental(new File(folder, "events-log.xes.gz"));
		for (int[] trace : GenerateLog.generateTraces(tree, logSize, random, false)) {
			logWriter.writeTrace(trace, tree.getInt2activity(), time);
			time++;
		}
		logWriter.close();
	}
}

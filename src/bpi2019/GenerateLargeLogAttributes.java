package bpi2019;

import java.io.File;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.XVisitor;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.InlineTree;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeExportPlugin;

import p2015sosym.efficienttree.generatebehaviour.GenerateLog;
import thesis.helperClasses.LogWriterIncremental;
import thesis.helperClasses.XLogWriterIncremental;

public class GenerateLargeLogAttributes {

	public static void main(String[] args) throws Exception {

		File folder = new File("C:\\Users\\sander\\Documents\\datasets\\Pouneh Samadi");

		EfficientTree tree = InlineTree.xor( //
				InlineTree.seq(InlineTree.leaf("a"), InlineTree.leaf("b"), InlineTree.leaf("c"), InlineTree.leaf("d")), //
				InlineTree.and(InlineTree.leaf("e"), InlineTree.leaf("f"), InlineTree.leaf("g"), InlineTree.leaf("h")), // 
				InlineTree.loop(InlineTree.leaf("i"), InlineTree.leaf("j"), InlineTree.leaf("k")));

		System.out.println(tree);

		//write to file
		EfficientTreeExportPlugin.export(tree, new File(folder, "attributes-tree.tree"));

		long logSize = (long) Math.pow(10, 3);
		long randomSeed = 1;
		Random random = new Random(randomSeed);

		final XAttributeMap attributes = new org.deckfour.xes.model.impl.XAttributeMapImpl();
		XEvent event = new XEvent() {

			public void setAttributes(XAttributeMap arg0) {
				// TODO Auto-generated method stub

			}

			public boolean hasAttributes() {
				// TODO Auto-generated method stub
				return false;
			}

			public Set<XExtension> getExtensions() {
				// TODO Auto-generated method stub
				return null;
			}

			public XAttributeMap getAttributes() {
				return attributes;
			}

			public XID getID() {
				// TODO Auto-generated method stub
				return null;
			}

			public void accept(XVisitor arg0, XTrace arg1) {
				// TODO Auto-generated method stub

			}

			public XEvent clone() {
				return null;
			}
		};
		AtomicInteger number = new AtomicInteger();

		String[] names = new String[(int) logSize];
		for (int i = 0; i < names.length; i++) {
			names[i] = "a" + i;
		}

		//generate log
		LogWriterIncremental logWriter = new XLogWriterIncremental(new File(folder, "attributes-log.xes.gz"));
		for (int[] trace : GenerateLog.generateTraces(tree, logSize, random, false)) {
			logWriter.startTrace();

			int n = number.incrementAndGet();
			attributes.put("a" + n, new XAttributeLiteralImpl("a" + n, "v" + n));
			logWriter.writeEvent(event);

			logWriter.endTrace();
		}
		logWriter.close();
	}
}

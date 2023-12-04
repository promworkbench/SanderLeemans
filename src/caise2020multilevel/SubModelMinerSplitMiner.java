package caise2020multilevel;

import java.io.File;
import java.io.FileInputStream;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.multilevelminer.mining.CommonParameters;
import org.processmining.multilevelminer.mining.SubModelMiner;
import org.processmining.multilevelminer.multilevelmodel.SubModelEdit;
import org.processmining.multilevelminer.multilevelmodel.SubModelImplBPMN;
import org.processmining.multilevelminer.multilevelmodel.types.SubModelTypeBPMN;
import org.processmining.plugins.inductiveminer2.mining.MiningParametersAbstract;

import com.raffaeleconforti.memorylog.XAttributeLiteralImpl;

import thesis.helperClasses.FakeContext;
import thesis.helperClasses.XLogWriterIncremental;

public class SubModelMinerSplitMiner implements SubModelMiner<BPMNDiagram, Activity, Double> {

	public String toString() {
		return "Split Miner (SM)";
	}

	public Double getParameters() {
		return 0.0d;
	}

	public void setNoiseLevel(MiningParametersAbstract parameters, double noiseValue) {

	}

	public void incorporate(CommonParameters commonParameters, Double parameters) {

	}

	public SubModelEdit<BPMNDiagram, Activity> mine(XLog log, CommonParameters commonParameters, Double parameters,
			Canceller canceller) {

		//Split Miner does not support empty logs; return a dummy model.
		if (log.isEmpty()) {
			BPMNDiagram model = new BPMNDiagramImpl("empty");
			Event startEvent = model.addEvent("start", Event.EventType.START, Event.EventTrigger.NONE,
					Event.EventUse.CATCH, true, null);
			Event endEvent = model.addEvent("end", Event.EventType.END, Event.EventTrigger.NONE, Event.EventUse.THROW,
					true, null);
			model.addFlow(startEvent, endEvent, "");
			return new SubModelImplBPMN(model);
		}

		XEventClassifier classifier = commonParameters.getClassifier();

		try {
			//write the log to a temp file
			File logFile = File.createTempFile("log-splitMiner", ".xes.gz");
			{
				XLogWriterIncremental writer = new XLogWriterIncremental(logFile);
				for (XTrace trace : log) {
					XTrace newTrace = new XTraceImpl(trace.getAttributes());
					for (XEvent event : trace) {
						String activity = classifier.getClassIdentity(event);
						XEvent newEvent = new XEventImpl((XAttributeMap) event.getAttributes().clone());
						newEvent.getAttributes().put(XConceptExtension.KEY_NAME,
								new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, activity));
						newTrace.add(newEvent);
					}
					writer.writeTrace(newTrace);
				}
				writer.close();
			}

			File modelFile = File.createTempFile("log-splitMiner", ".bpmn");

			AlgorithmSplitMiner.callSplitMiner(logFile, modelFile);

			logFile.delete();
			BPMNDiagram model = SubModelTypeBPMN.instance.parseModel(new FakeContext(), new FileInputStream(modelFile));
			modelFile.delete();
			return new SubModelImplBPMN(model);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
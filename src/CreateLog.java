import java.io.File;

import org.processmining.models.graphbased.directed.petrinet.StochasticNet;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet.DistributionType;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet.ExecutionPolicy;
import org.processmining.models.graphbased.directed.petrinet.StochasticNet.TimeUnit;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.TimedTransition;
import org.processmining.models.graphbased.directed.petrinet.impl.StochasticNetImpl;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.exporting.StochasticNetToPNMLConverter;
import org.processmining.plugins.pnml.simple.PNMLRoot;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import caise2020.FakeGraphLayoutConnection;

public class CreateLog {
	public static void main(String... args) throws Exception {
//		File logFile = new File("C:\\users\\sander\\desktop\\example.xes.gz");
		File modelFile = new File("/home/sander/Desktop/example.pnml");

//		LogWriterIncremental writer = new XLogWriterIncremental(logFile);
//
//		for (int i = 0; i < 10; i++) {
//			writer.startTrace();
//			writer.writeEvent("a", "complete");
//			writer.endTrace();
//		}
//
//		for (int i = 0; i < 20; i++) {
//			writer.startTrace();
//			writer.writeEvent("a", "complete");
//			writer.writeEvent("b", "complete");
//			writer.writeEvent("d", "complete");
//			writer.writeEvent("e", "complete");
//			writer.endTrace();
//		}
//
//		for (int i = 0; i < 30; i++) {
//			writer.startTrace();
//			writer.writeEvent("a", "complete");
//			writer.writeEvent("b", "complete");
//			writer.writeEvent("e", "complete");
//			writer.endTrace();
//		}
//
//		for (int i = 0; i < 30; i++) {
//			writer.startTrace();
//			writer.writeEvent("a", "complete");
//			writer.writeEvent("d", "complete");
//			writer.writeEvent("b", "complete");
//			writer.writeEvent("e", "complete");
//			writer.endTrace();
//		}
//
//		for (int i = 0; i < 3; i++) {
//			writer.startTrace();
//			writer.writeEvent("a", "complete");
//			writer.writeEvent("c", "complete");
//			writer.writeEvent("d", "complete");
//			writer.writeEvent("e", "complete");
//			writer.endTrace();
//		}
//
//		for (int i = 0; i < 3; i++) {
//			writer.startTrace();
//			writer.writeEvent("a", "complete");
//			writer.writeEvent("c", "complete");
//			writer.writeEvent("e", "complete");
//			writer.endTrace();
//		}
//
//		for (int i = 0; i < 4; i++) {
//			writer.startTrace();
//			writer.writeEvent("a", "complete");
//			writer.writeEvent("d", "complete");
//			writer.writeEvent("e", "complete");
//			writer.endTrace();
//		}
//
//		writer.close();

		//make model
		StochasticNet net = new StochasticNetImpl("example");
		net.setExecutionPolicy(ExecutionPolicy.RACE_ENABLING_MEMORY);
		net.setTimeUnit(TimeUnit.HOURS);
		Place source = net.addPlace("source");
		Marking marking = new Marking();
		marking.add(source);
		Place sink = net.addPlace("sink");
		Place p1 = net.addPlace("p1");
		Place p2 = net.addPlace("p2");
		Place p3 = net.addPlace("p3");
		Place p4 = net.addPlace("p4");

		TimedTransition a2 = net.addTimedTransition("a", 0.1, DistributionType.UNIFORM, 0.0, 200.0);
		net.addArc(source, a2);
		net.addArc(a2, sink);

		TimedTransition a1 = net.addTimedTransition("a", 0.9, DistributionType.UNIFORM, 0.0, 200.0);
		net.addArc(source, a1);
		net.addArc(a1, p1);
		net.addArc(a1, p2);

		TimedTransition b = net.addTimedTransition("b", 0.98, DistributionType.UNIFORM, 0.0, 200.0);
		net.addArc(p1, b);
		net.addArc(b, p3);

		TimedTransition c = net.addTimedTransition("c", 0.02, DistributionType.UNIFORM, 0.0, 200.0);
		net.addArc(p1, c);
		net.addArc(c, p3);

		TimedTransition d = net.addTimedTransition("d", 0.5, DistributionType.UNIFORM, 0.0, 200.0);
		net.addArc(p2, d);
		net.addArc(d, p4);

		TimedTransition t1 = net.addTimedTransition("tau", 0.5, DistributionType.UNIFORM, 0.0, 200.0);
		t1.setInvisible(true);
		net.addArc(p2, t1);
		net.addArc(t1, p4);

		TimedTransition e = net.addTimedTransition("e", 1, DistributionType.UNIFORM, 0.0, 200.0);
		net.addArc(p4, e);
		net.addArc(p3, e);
		net.addArc(e, sink);

		//store
		PNMLRoot root = new StochasticNetToPNMLConverter().convertNet(net, marking, new FakeGraphLayoutConnection(net));
		Serializer serializer = new Persister();
		serializer.write(root, modelFile);
	}
}

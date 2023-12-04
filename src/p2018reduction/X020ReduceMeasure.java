package p2018reduction;

import java.io.File;
import java.io.FileNotFoundException;

import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree2AcceptingPetriNet;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReduce;
import org.processmining.plugins.InductiveMiner.plugins.EfficientTreeImportPlugin;
import org.processmining.plugins.InductiveMiner.reduceacceptingpetrinet.ReduceAcceptingPetriNetKeepLanguage;

public class X020ReduceMeasure {
	public static void main(String... args) throws FileNotFoundException, Exception {

		System.out.println(
				"log,discovered (pt),discovered (pn),pn reduced (pn), pt reduced (pt),pt reduced (pn), pt+pn reduced (pn)");

		for (File logFile : Parameters.getInputFiles()) {

			System.out.print(logFile.getName());
			System.out.print(",");

			File modelFile = Parameters.getDiscoveredModelFile(logFile);

			EfficientTree tree = EfficientTreeImportPlugin.importFromFile(modelFile);
			System.out.print(tree.traverse(tree.getRoot()));
			System.out.print(",");

			{
				AcceptingPetriNet net = EfficientTree2AcceptingPetriNet.convert(tree);
				System.out.print(net.getNet().getNodes().size() + net.getNet().getTransitions().size());
				System.out.print(",");

				ReduceAcceptingPetriNetKeepLanguage.reduce(net, new Canceller() {
					public boolean isCancelled() {
						return false;
					}
				});
				System.out.print(net.getNet().getNodes().size() + net.getNet().getTransitions().size());
				System.out.print(",");
			}

			EfficientTreeReduce.reduce(tree);

			System.out.print(tree.traverse(tree.getRoot()));
			System.out.print(",");

			{
				AcceptingPetriNet net = EfficientTree2AcceptingPetriNet.convert(tree);
				System.out.print(net.getNet().getNodes().size() + net.getNet().getTransitions().size());
				System.out.print(",");

				ReduceAcceptingPetriNetKeepLanguage.reduce(net, new Canceller() {
					public boolean isCancelled() {
						return false;
					}
				});
				System.out.print(net.getNet().getNodes().size() + net.getNet().getTransitions().size());
			}

			System.out.println();

		}
	}
}

package thesis.evaluation.standAloneMiners;

import java.io.File;
import java.io.FileInputStream;

import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.plugins.InductiveMiner.reduceacceptingpetrinet.MurataFSP1keepLanguage;

import thesis.helperClasses.FakeContext;

public class RunMurata {
	public static void main(String[] args) throws Exception {
		File in = new File(
				"C:\\users\\sander\\Desktop\\Receipt phase WABO CoSeLoG project.xes.gz-discovery0.xes.gz-fo.pnml");
		File out = new File("C:\\users\\sander\\Desktop\\out.pnml");

		AcceptingPetriNet net = AcceptingPetriNetFactory.createAcceptingPetriNet();
		net.importFromStream(new FakeContext(), new FileInputStream(in));

		MurataFSP1keepLanguage.reduce(net, new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		});

		net.exportToFile(new FakeContext(), out);
	}
}

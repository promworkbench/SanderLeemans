package svn55datastochastics;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.stochasticlabelleddatapetrinet.plugins.SLDPN;
import org.processmining.stochasticlabelleddatapetrinet.plugins.SLDPNDiscoveryParametersAbstract;
import org.processmining.stochasticlabelleddatapetrinet.plugins.SLDPNDiscoveryParametersDefault;
import org.processmining.stochasticlabelleddatapetrinet.plugins.SLDPNDiscoveryPlugin;
import org.processmining.stochasticlabelleddatapetrinet.plugins.SLDPNExportPlugin;

public class StochasticAlgorithmData2 implements StochasticAlgorithm {

	public String getName() {
		return "data-based stochastic discovery 2 without one-hot-encoding";
	}

	public String getAbbreviation() {
		return "dsd2";
	}

	public String getLatexName() {
		return "\\ourtechnique{}we";
	}

	public String getFileExtension() {
		return ".sldpn";
	}

	public boolean createsDataModels() {
		return true;
	}

	public void run(File logFile, XLog log, AcceptingPetriNet net, File modelFile) throws Exception {
		SLDPNDiscoveryParametersAbstract parameters = new SLDPNDiscoveryParametersDefault();
		parameters.setOneHotEncoding(false);
		SLDPN result = SLDPNDiscoveryPlugin.discover(log, net, parameters);
		SLDPNExportPlugin.export(result, modelFile);
	}
}
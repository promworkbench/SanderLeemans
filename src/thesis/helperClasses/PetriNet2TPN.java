package thesis.helperClasses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.models.graphbased.NodeID;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

public class PetriNet2TPN {

	public static void main(String[] args) throws FileNotFoundException, Exception {
		//load the discovered model
		AcceptingPetriNet net = AcceptingPetriNetFactory.createAcceptingPetriNet();
		net.importFromStream(
				new FakeContext(),
				new FileInputStream(
						new File(
								"D:\\svn\\00 - the beast\\experiments\\logQuality\\discoveredModels\\Receipt phase WABO CoSeLoG project.xes.gz-discovery11.xes.gz-alpha.pnml")));

		System.out.println(convert(net));
	}

	public static String convert(AcceptingPetriNet petriNet) {
		StringBuilder result = new StringBuilder();

		//places
		for (Place place : petriNet.getNet().getPlaces()) {
			result.append("place ");
			result.append(removeNode(place.getId()));

			if (petriNet.getInitialMarking().contains(place)) {
				result.append(" init " + petriNet.getInitialMarking().occurrences(place));
			}

			result.append(";\n");
		}

		//transitions
		for (Transition transition : petriNet.getNet().getTransitions()) {

			if (petriNet.getNet().getInEdges(transition).isEmpty()
					|| petriNet.getNet().getOutEdges(transition).isEmpty()) {
				return "Don't bother about this Petri net: it's not a workflow net, as not all transitions have incoming and outgoing connections.";
			}

			result.append("trans ");
			result.append(removeNode(transition.getId()));
			result.append("\n\tin ");
			result.append(StringUtils.join(
					FluentIterable.from(petriNet.getNet().getInEdges(transition)).transform(
							new Function<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>, String>() {
								public String apply(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> arg0) {
									return removeNode(arg0.getSource().getId());
								}
							}), ", "));
			result.append("\n\tout ");
			result.append(StringUtils.join(
					FluentIterable.from(petriNet.getNet().getOutEdges(transition)).transform(
							new Function<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>, String>() {
								public String apply(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> arg0) {
									return removeNode(arg0.getTarget().getId());
								}
							}), ", "));
			result.append(";\n\n");
		}

		return result.toString();
	}

	private static String removeNode(NodeID id) {
		return "n" + id.toString().substring(6).replace('-', '_');
	}
}

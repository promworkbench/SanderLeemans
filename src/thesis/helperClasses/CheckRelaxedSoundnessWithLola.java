package thesis.helperClasses;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

public class CheckRelaxedSoundnessWithLola {
	public static void main(String... args) throws FileNotFoundException, Exception {

		AcceptingPetriNet net = AcceptingPetriNetFactory.createAcceptingPetriNet();
		net.importFromStream(new FakeContext(), new FileInputStream(new File(
				"D:\\svn\\00 - the beast\\experiments\\logQuality\\discoveredModels\\Receipt phase WABO CoSeLoG project.xes.gz-discovery11.xes.gz-alpha.pnml")));

		System.out.println(isRelaxedSoundAndBounded(net));
	}

	public static boolean isSound(AcceptingPetriNet net)
			throws UnsupportedEncodingException, IOException, JSONException, ConnectionCannotBeObtained {
		AcceptingPetriNet reducedNet = reduceWorkflowNet(net);
		String lolaPetriNet = PetriNet2Lola.convert(reducedNet.getNet(), reducedNet.getInitialMarking());

		//is bounded
		if (!isBounded(reducedNet)) {
			System.out.println("not bounded");
			return false;
		}

		//can always reach the final marking
		String lolaFinalMarkingReachableFormula = getFinalMarkingAlwaysReachableFormula(reducedNet);
		if (!callLola(lolaPetriNet, lolaFinalMarkingReachableFormula)) {
			System.out.println("final marking not always reachable");
			return false;
		}

		//transitions are live
		for (Transition transition : reducedNet.getNet().getTransitions()) {
			String transitionIsDeadFormula = getTransitionDeadFormula(reducedNet, transition);
			if (callLola(lolaPetriNet, transitionIsDeadFormula)) {
				System.out.println("dead transition");
				return false;
			}
		}

		System.out.println("sound");
		return true;
	}

	/**
	 * Very sorry, this method requires a Cygwin + Lola installation. I'm afraid
	 * you'll have to compile it yourself.
	 * 
	 * @param net
	 * @return
	 * @throws IOException
	 * @throws ConnectionCannotBeObtained
	 * @throws JSONException
	 */
	public static boolean isRelaxedSoundAndBounded(AcceptingPetriNet net)
			throws IOException, ConnectionCannotBeObtained, JSONException {
		AcceptingPetriNet reducedNet = reduceWorkflowNet(net);
		String lolaPetriNet = PetriNet2Lola.convert(reducedNet.getNet(), reducedNet.getInitialMarking());

		for (Place place : reducedNet.getNet().getPlaces()) {
			String lolaBoundedNessFormula = getPlaceIsBoundedFormula(reducedNet, place);
			if (!callLola(lolaPetriNet, lolaBoundedNessFormula, "--encoder=full", "--search=cover")) {
				return false;
			}
		}

		String lolaFinalMarkingReachableFormula = getFinalMarkingReachableFormula(reducedNet);
		if (!callLola(lolaPetriNet, lolaFinalMarkingReachableFormula)) {
			return false;
		}

		return true;
	}

	public static boolean isBounded(AcceptingPetriNet net)
			throws UnsupportedEncodingException, IOException, JSONException, ConnectionCannotBeObtained {
		AcceptingPetriNet reducedNet = reduceWorkflowNet(net);
		String lolaPetriNet = PetriNet2Lola.convert(reducedNet.getNet(), reducedNet.getInitialMarking());

		for (Place place : reducedNet.getNet().getPlaces()) {
			String lolaBoundedNessFormula = getPlaceIsBoundedFormula(reducedNet, place);
			if (!callLola(lolaPetriNet, lolaBoundedNessFormula, "--encoder=full", "--search=cover")) {
				return false;
			}
		}

		return true;
	}

	public static boolean isRelaxedSound(AcceptingPetriNet net)
			throws ConnectionCannotBeObtained, UnsupportedEncodingException, IOException, JSONException {
		AcceptingPetriNet reducedNet = reduceWorkflowNet(net);
		String lolaPetriNet = PetriNet2Lola.convert(reducedNet.getNet(), reducedNet.getInitialMarking());

		String lolaFinalMarkingReachableFormula = getFinalMarkingReachableFormula(reducedNet);
		if (!callLola(lolaPetriNet, lolaFinalMarkingReachableFormula)) {
			return false;
		}

		return true;
	}

	public static boolean callLola(String lolaPetriNet, String lolaFormula, String... commandLineOptions)
			throws IOException, UnsupportedEncodingException, JSONException {
		//		ProcessBuilder pb = new ProcessBuilder().command("C:\\cygwin64\\bin\\bash.exe", "--login", "-i", "-c",
		//				"\"lola --json " + commandLineOption + "--markinglimit=200000000 --threads=12 --formula='"
		//						+ lolaFormula + "'\"");
		String[] options = new String[commandLineOptions.length + 5];
		options[0] = "/usr/local/bin/lola";
		options[1] = "--json";
		System.arraycopy(commandLineOptions, 0, options, 2, commandLineOptions.length);
		options[options.length - 3] = "--markinglimit=200000000";
		options[options.length - 2] = "--threads=12";
		options[options.length - 1] = "--formula=" + lolaFormula;
		ProcessBuilder pb = new ProcessBuilder().command(options);
		pb.redirectErrorStream(true);
		Process dotProcess = pb.start();
		BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(dotProcess.getOutputStream(), "UTF-8"));
		out2.write(lolaPetriNet);
		out2.flush();
		out2.close();

		//		PrintWriter net = new PrintWriter(new File("/mnt/hgfs/experiments/net.txt"));
		//		net.print(lolaPetriNet);
		//		net.close();

		System.out.println(ArrayUtils.toString(options));
		System.out.println(StringUtils.join(options, ' '));

		if (1 == 2) {
			String result = IOUtils.toString(dotProcess.getInputStream());

			System.out.println(result);

		} else {
			InputStream stdout = new BufferedInputStream(dotProcess.getInputStream());

			Scanner scanner = new Scanner(stdout);
			try {
				while (scanner.hasNextLine()) {
					String result = scanner.nextLine();
					System.out.println(result);

					if (result.contains("result:")) {
						return result.contains("yes");
					}
					if (result.startsWith("{")) {
						JSONObject json = new JSONObject(result);
						System.out.println(json);

						JSONObject array = json.getJSONObject("analysis");
						boolean result2 = array.getBoolean("result");
						return result2;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				scanner.close();
			}
		}

		throw new IOException("failed to call Lola");
	}

	/**
	 * Reduces the given accepting petri net. Notice that the intitial and final
	 * markings are reconstructed, e.g. in the reduced net, all places without
	 * incoming arcs are initial markings.
	 * 
	 * @param net
	 * @return
	 * @throws ConnectionCannotBeObtained
	 */
	public static AcceptingPetriNet reduceWorkflowNet(AcceptingPetriNet net) throws ConnectionCannotBeObtained {
		Petrinet reducedNet = net.getNet();//new Murata().runWF(null, net.getNet(), new MurataParameters());

		Marking initialMarking = new Marking();
		for (Place p : reducedNet.getPlaces()) {
			if (reducedNet.getInEdges(p).isEmpty()) {
				initialMarking.add(p);
			}
		}

		Marking finalMarking = new Marking();
		for (Place p : reducedNet.getPlaces()) {
			if (reducedNet.getOutEdges(p).isEmpty()) {
				finalMarking.add(p);
			}
		}

		return AcceptingPetriNetFactory.createAcceptingPetriNet(reducedNet, initialMarking, finalMarking);
	}

	public static String getFinalMarkingReachableFormula(AcceptingPetriNet net) {

		List<String> finalMarkingsFormulae = new ArrayList<>();
		for (final Marking finalMarking : net.getFinalMarkings()) {
			finalMarkingsFormulae.add(StringUtils
					.join(FluentIterable.from(net.getNet().getPlaces()).transform(new Function<Place, String>() {
						public String apply(Place place) {
							if (finalMarking.contains(place)) {
								return PetriNet2Lola.removeNode(place.getId()) + " = "
										+ finalMarking.occurrences(place);
							} else {
								return PetriNet2Lola.removeNode(place.getId()) + " = 0";
							}
						}
					}), " AND "));
		}

		return "EF (" + StringUtils.join(finalMarkingsFormulae, ") OR (") + ")";

	}

	public static String getPlaceIsBoundedFormula(AcceptingPetriNet net, Place place) {
		return "AG (" + PetriNet2Lola.removeNode(place.getId()) + " < oo)";
	}

	public static String getFinalMarkingAlwaysReachableFormula(AcceptingPetriNet net) {
		List<String> finalMarkingsFormulae = new ArrayList<>();
		for (final Marking finalMarking : net.getFinalMarkings()) {
			finalMarkingsFormulae.add(StringUtils
					.join(FluentIterable.from(net.getNet().getPlaces()).transform(new Function<Place, String>() {
						public String apply(Place place) {
							if (finalMarking.contains(place)) {
								return PetriNet2Lola.removeNode(place.getId()) + " = "
										+ finalMarking.occurrences(place);
							} else {
								return PetriNet2Lola.removeNode(place.getId()) + " = 0";
							}
						}
					}), " AND "));
		}

		return "AGEF (" + StringUtils.join(finalMarkingsFormulae, ") OR (") + ")";
	}

	public static String getTransitionDeadFormula(AcceptingPetriNet net, Transition transition) {
		return "AG NOT FIREABLE(" + PetriNet2Lola.removeNode(transition.getId()) + ")";
	}
}

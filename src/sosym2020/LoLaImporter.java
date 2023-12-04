package sosym2020;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetImpl;
import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
import org.processmining.models.semantics.petrinet.Marking;

import gnu.trove.map.hash.THashMap;

@Plugin(name = "LoLa Petri net", parameterLabels = { "Filename" }, returnLabels = { "Petri net" }, returnTypes = {
		AcceptingPetriNet.class })
@UIImportPlugin(description = "LoLa files", extensions = { "lola" })
public class LoLaImporter extends AbstractImportPlugin {

	private static final int BUFFER_SIZE = 8192 * 4;
	private static final String CHARSET = Charset.defaultCharset().name();

	protected AcceptingPetriNet importFromStream(PluginContext context, InputStream input, String filename,
			long fileSizeInBytes) throws Exception {
		//read the file
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = input.read(buffer)) > -1) {
			baos.write(buffer, 0, len);
		}
		baos.flush();

		input = new ByteArrayInputStream(baos.toByteArray());
		BufferedReader r = new BufferedReader(new InputStreamReader(input, CHARSET), BUFFER_SIZE);
		AcceptingPetriNet pn = readFile(r, filename);

		if (pn != null) {
			return pn;
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(null, "Invalid LoLa file", "Invalid file", JOptionPane.ERROR_MESSAGE);
			}
		});
		context.getFutureResult(0).cancel(false);
		return null;
	}

	private AcceptingPetriNet readFile(BufferedReader r, String name) throws IOException {
		PetrinetImpl pn = new PetrinetImpl(name);

		//places
		THashMap<String, Place> x = new THashMap<>();
		{
			if (searchLine(r, "place") == null) {
				return null;
			}
			ArrayList<String> places = searchLine(r, ";");
			if (places == null) {
				return null;
			}
			for (Iterator<String> it = places.iterator(); it.hasNext();) {
				String place = it.next();

				//chop off ,
				if (it.hasNext() && place.endsWith(",")) {
					place = place.substring(0, place.length() - 1);
				}

				Place pnPlace = pn.addPlace(place);
				x.put(place, pnPlace);
			}
		}

		//marking
		Marking initialMarking = new Marking();
		{
			if (searchLine(r, "marking") == null) {
				return null;
			}
			ArrayList<String> places = searchLine(r, ";");
			if (places == null) {
				return null;
			}
			for (String place : places) {
				String placeName = place.substring(0, place.indexOf(':'));
				int weight = Integer.valueOf(place.substring(place.indexOf(':') + 1).trim());
				Place pnPlace = x.get(placeName);
				if (pnPlace == null) {
					return null;
				}
				initialMarking.add(pnPlace, weight);
			}
		}

		//transitions
		while (true) {
			ArrayList<String> t = searchLineStartingWith(r, "transition");
			if (t == null) {
				break;
			}
			String tName = t.get(t.size() - 1);
			System.out.println(tName);
			Transition transition = pn.addTransition(tName);
			if (searchLine(r, "consume") == null) {
				return null;
			}

			List<String> consume = searchLineEndingWith(r, ";");
			if (consume == null) {
				return null;
			}
			for (Iterator<String> it = consume.iterator(); it.hasNext();) {
				String p = it.next();

				//chop off , at the end
				if (it.hasNext() && p.endsWith(",")) {
					p = p.substring(0, p.length() - 1);
				}

				String placeName = p.substring(0, p.indexOf(':'));
				int weight = Integer.valueOf(p.substring(p.indexOf(':') + 1).trim());
				Place place = x.get(placeName);
				pn.addArc(place, transition, weight);
			}

			if (searchLine(r, "produce") == null) {
				return null;
			}
			List<String> produce = searchLineEndingWith(r, ";");
			for (Iterator<String> it = produce.iterator(); it.hasNext();) {
				String p = it.next();

				//chop off , at the end
				if (it.hasNext() && p.endsWith(",")) {
					p = p.substring(0, p.length() - 1);
				}

				String placeName = p.substring(0, p.indexOf(':'));
				int weight = Integer.valueOf(p.substring(p.indexOf(':') + 1).trim());
				Place place = x.get(placeName);
				if (place == null) {
					return null;
				}
				pn.addArc(transition, place, weight);
			}
		}

		return new AcceptingPetriNetImpl(pn, initialMarking);
	}

	private ArrayList<String> searchLine(BufferedReader r, String search) throws IOException {
		ArrayList<String> result = new ArrayList<>();
		while (true) {
			String line = r.readLine();
			if (line == null) {
				return null;
			}
			if (line.trim().toLowerCase().equals(search)) {
				return result;
			}
			result.add(line.trim());
		}
	}

	private ArrayList<String> searchLineStartingWith(BufferedReader r, String search) throws IOException {
		ArrayList<String> result = new ArrayList<>();
		while (true) {
			String line = r.readLine();
			if (line == null) {
				return null;
			}
			if (line.trim().toLowerCase().startsWith(search)) {
				result.add(line.trim().substring(search.length()).trim());
				return result;
			}
			result.add(line.trim());
		}
	}

	private List<String> searchLineEndingWith(BufferedReader r, String search) throws IOException {
		ArrayList<String> result = new ArrayList<>();
		while (true) {
			String line = r.readLine();
			if (line == null) {
				return null;
			}
			if (line.trim().toLowerCase().endsWith(search)) {
				result.add(line.trim().substring(0, line.trim().length() - search.length()).trim());
				return result;
			}
			result.add(line.trim());
		}
	}

}

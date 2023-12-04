package p2018reduction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.DotNode;

import gnu.trove.map.hash.THashMap;

public class ReadFile {
	public static void main(String... args) throws IOException, JSONException {
		File dir = new File(
				"/home/sander/Documents/svn/19 - reduction rules for process trees/evaluation/448 models from FAOC Untangling paper");

		for (File file : dir.listFiles()) {
			System.out.println(file);
			readFile(file);
		}

	}

	public static void readFile(File file) throws IOException, JSONException {
		String source = new String(Files.readAllBytes(file.toPath()));
		JSONObject json = new JSONObject(source);

		Map<String, DotNode> id2node = new THashMap<>();

		Dot dot = new Dot();

		JSONArray gateways = json.getJSONArray("gateways");
		for (int i = 0; i < gateways.length(); i++) {
			JSONObject o = gateways.getJSONObject(i);

			DotNode node = dot.addNode(o.getString("type"));
			id2node.put(o.getString("id"), node);
		}

		JSONArray tasks = json.getJSONArray("tasks");
		for (int i = 0; i < tasks.length(); i++) {
			JSONObject o = tasks.getJSONObject(i);

			DotNode node = dot.addNode(o.getString("label"));
			id2node.put(o.getString("id"), node);
		}
		JSONArray flows = json.getJSONArray("flows");
		for (int i = 0; i < flows.length(); i++) {
			JSONObject o = flows.getJSONObject(i);
			
			DotNode src = id2node.get(o.get("src"));
			DotNode tgt = id2node.get(o.get("tgt"));
			
			dot.addEdge(src, tgt);
		}

		System.out.println(dot);
	}
}

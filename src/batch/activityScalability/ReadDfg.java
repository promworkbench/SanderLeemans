package batch.activityScalability;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.dfgOnly.Dfg;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ReadDfg {

	Dfg dfg;

	@Plugin(name = "Create directly-follows graph from XLog", returnLabels = { "XLog" }, returnTypes = { Dfg.class }, parameterLabels = {}, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Generate model and log", requiredParameterLabels = {})
	public Dfg read(PluginContext context) {
		File file;
		
		//ask user for file
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
		} else {
			context.getFutureResult(0).cancel(false);
			return null;
		}

		dfg = new DfgImpl();
		SAXParser parser = null;
		try {
			parser = SAXParserFactory.newInstance().newSAXParser();
		} catch (ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
		Parser handler = new Parser();
		try {
			parser.parse(file, handler);
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
		return dfg;
	}

	public class Parser extends DefaultHandler {

		XEventClass lastEventClass = null;
		boolean inEvent = false;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) {
			switch (qName) {
				case "trace" :
					lastEventClass = null;
					break;
				case "event" :
					inEvent = true;
					break;
				case "string" :
					if (inEvent) {
						String name = null;
						for (int i = 0; i < attributes.getLength(); i++) {
							if (attributes.getQName(i).equals("key") && attributes.getValue(i).equals("concept:name")) {
								for (int j = 0; j < attributes.getLength(); j++) {
									if (attributes.getQName(j).equals("value")) {
										name = attributes.getValue(j);
										break;
									}
								}
							}
						}
						if (name != null) {
							XEventClass activity = getEventClass(name);
							if (lastEventClass == null) {
								//this is a start activity
								dfg.addStartActivity(activity, 1);
							} else {
								//add a dfg-edge
								dfg.addDirectlyFollowsEdge(lastEventClass, activity, 1);
							}
							lastEventClass = activity;
						}
					}
					break;
				default :
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) {
			switch (qName) {
				case "trace" :
					if (lastEventClass != null) {
						dfg.addEndActivity(lastEventClass, 1);
					}
					lastEventClass = null;
					break;
				case "event" :
					inEvent = false;
					break;
			}
		}

		Map<String, XEventClass> activities = new HashMap<>();

		private XEventClass getEventClass(String name) {
			if (activities.containsKey(name)) {
				return activities.get(name);
			}
			XEventClass a = new XEventClass(name, activities.size());
			activities.put(name, a);
			return a;
		}

	}
}

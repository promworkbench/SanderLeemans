package filter;

import java.awt.Color;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import com.fluxicon.slickerbox.factory.SlickerFactory;

@Plugin(name = "Combine event attributes", returnLabels = { "Log with combined event attributes" }, returnTypes = { XLog.class }, parameterLabels = { "Log" }, userAccessible = true)
public class UnifyValues {

	private XLog log;
	private JList<String> keys;
	private DefaultListModel<String> keysList = new DefaultListModel<String>();
	private JList<String> values;
	private DefaultListModel<String> valuesList = new DefaultListModel<String>();

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Filter log on life cycle, default", requiredParameterLabels = { 0 })
	public XLog filterLog(UIPluginContext context, final XLog log) throws Exception {

		this.log = log;

		SlickerFactory factory = SlickerFactory.instance();

		JPanel thresholdsPanel = factory.createRoundedPanel(15, Color.gray);
		thresholdsPanel.setLayout(new BoxLayout(thresholdsPanel, BoxLayout.Y_AXIS));

		keys = new JList<String>(keysList);
		keys.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		keys.setLayoutOrientation(JList.VERTICAL);
		keys.setVisibleRowCount(-1);
		thresholdsPanel.add(new JScrollPane(keys));

		values = new JList<String>(valuesList);
		values.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		values.setLayoutOrientation(JList.VERTICAL);
		values.setVisibleRowCount(-1);
		thresholdsPanel.add(new JScrollPane(values));

		fillKeysValues();

		InteractionResult result = context.showWizard("Combine attributes", true, true, thresholdsPanel);
		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(true);
			return null;
		}

		return filter();
	}

	public void fillKeysValues() {
		Set<String> ks = new TreeSet<String>();

		//find all attributes
		for (XTrace trace : log) {
			for (XEvent event : trace) {
				ks.addAll(event.getAttributes().keySet());
			}
		}

		for (String ec : ks) {
			keysList.addElement(ec);
			valuesList.addElement(ec);
		}
	}

	public XLog filter() {
//		System.out.println(keys.getSelectedValues());
		
		for (XTrace trace : log) {
			for (XEvent event : trace) {
				//check whether this event contains all selected values
				boolean containsAll = true;
				String newKey = "";
				String newValue = "";
				for (Object s : keys.getSelectedValues()) {
					containsAll = containsAll && event.getAttributes().containsKey(s);
					if (containsAll) {
						newKey = newKey + event.getAttributes().get(s).toString();
					}
				}
				for (Object s : values.getSelectedValues()) {
					containsAll = containsAll && event.getAttributes().containsKey(s);
					if (containsAll) {
						newValue = newValue + event.getAttributes().get(s).toString();
					}
				}
				
//				System.out.println("add attribute " + newKey + " -> " + newValue);
				
				if (containsAll) {
					//make new attribute
					XAttribute attr = new XAttributeLiteralImpl(newKey, newValue);
					event.getAttributes().put(newKey, attr);
				}
			}
		}
		return log;
	}
}

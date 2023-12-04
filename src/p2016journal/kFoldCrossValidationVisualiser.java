package p2016journal;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.contexts.util.HtmlPanel;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.graphviz.colourMaps.ColourMap;
import org.processmining.plugins.graphviz.colourMaps.ColourMapWhiteRed;

public class kFoldCrossValidationVisualiser {
	@Plugin(name = "k-fold validation visualisation", returnLabels = { "Projected recall/precision visualization" }, returnTypes = { JComponent.class }, parameterLabels = { "Projected recall/precision" }, userAccessible = false)
	@Visualizer
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Show projected recall and precision", requiredParameterLabels = { 0 })
	public JComponent visualize(PluginContext context, kFoldCrossValidationResult result) {
		StringBuilder text = new StringBuilder();
		text.append("<html>");
		text.append("fitness:    " + result.getFitness());
		text.append("<br>");
		text.append("precision:  " + result.getPrecision());
		text.append("<br>");
		text.append("simplicity: " + result.getSimplicity());

		ColourMap colourMap = new ColourMapWhiteRed();

		//by run
		{
			text.append("<br>");
			text.append("<table>");
			text.append("<tr><td>run</td><td>fitness</td><td>precision</td><td>simplicity</td>");
			for (int bucketNr = 0; bucketNr < result.getK(); bucketNr++) {
				text.append("<tr>");
				text.append("<td>" + bucketNr + "</td>");

				double fitness = result.getFitness(bucketNr);
				if (fitness >= -0.01) {
					text.append("<td bgcolor=\""
							+ ColourMap.toHexString(colourMap.colour((long) (fitness * 1000), 1000)) + "\">");
					text.append(String.format("%.3f", fitness));
					text.append("</td>");
				} else {
					text.append("<td>-</td>");
				}

				double precision = result.getPrecision(bucketNr);
				if (precision >= -.01) {
					text.append("<td bgcolor=\""
							+ ColourMap.toHexString(colourMap.colour((long) (precision * 1000), 1000)) + "\">");
					text.append(String.format("%.3f", precision));
					text.append("</td>");
				} else {
					text.append("<td>-</td>");
				}

				double simplicity = result.getSimplicity(bucketNr);
				text.append("<td bgcolor=\""
						+ ColourMap.toHexString(colourMap.colour((long) (simplicity * 1000), 1000)) + "\">");
				text.append(String.format("%.3f", simplicity));
				text.append("</td>");

				text.append("</tr>");
			}
			text.append("</table>");
			text.append("<br>");
		}

		text.append("</html>");
		return new HtmlPanel(text.toString());
	}
}

import java.awt.Dimension;

import javax.swing.JFrame;

import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.DotElement;
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.graphviz.visualisation.DotPanel;
import org.processmining.plugins.graphviz.visualisation.listeners.DotElementSelectionListener;

import com.kitfox.svg.SVGDiagram;

public class Andrea {

	public static class CustomDotNode extends DotNode {
		public CustomDotNode(String label) {
			super(label, null);

			setOption("shape", "box");
			setOption("style", "rounded,filled");

			addSelectionListener(new DotElementSelectionListener() {
				public void selected(DotElement element, SVGDiagram image) {
					System.out.println("Selected " + getLabel());
					element.setOption("fillcolor", "#FF00FF");
				}

				public void deselected(DotElement element, SVGDiagram image) {
					System.out.println("Deselected " + getLabel());
					element.setOption("fillcolor", "green");
				}
			});
		}
	}

	public static void main(String[] args) {

		Dot dot = new Dot();

		dot.addNode(new CustomDotNode("n1"));
		dot.addNode(new CustomDotNode("n2"));

		DotPanel p = new DotPanel(dot);
		p.setPreferredSize(new Dimension(800, 600));

		JFrame f = new JFrame("test");
		f.add(p);
		f.pack();

		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

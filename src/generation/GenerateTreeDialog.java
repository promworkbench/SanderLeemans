package generation;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class GenerateTreeDialog extends JPanel {

	private static final long serialVersionUID = 7233338381448125579L;

	public GenerateTreeDialog(final GenerateTreeParameters parameters) {
		SlickerFactory factory = SlickerFactory.instance();

		JPanel thresholdsPanel = factory.createRoundedPanel(15, Color.gray);
		thresholdsPanel.setLayout(null);
		thresholdsPanel.setBounds(0, 0, 570, 240);

		JLabel thresholdTitle = factory.createLabel("Generate random process tree");
		thresholdTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
		thresholdsPanel.add(thresholdTitle);
		thresholdTitle.setBounds(10, 10, 400, 30);

		int y = 50;
		int ystep = 30;
		
		int[] xs = new int[]{20, 250};
		int xmargin = 10;

		{
			JLabel seedLabel = factory.createLabel("Random seed");
			thresholdsPanel.add(seedLabel);
			seedLabel.setBounds(xs[0], y, xs[1] - xs[0] - xmargin, 20);

			final JTextField seedField = new JTextField(parameters.getSeed() + "");
			seedField.addKeyListener(new KeyListener() {
				public void keyTyped(KeyEvent arg0) {

				}

				public void keyPressed(KeyEvent arg0) {

				}

				public void keyReleased(KeyEvent arg0) {
					parameters.setSeed(Long.valueOf(seedField.getText()));
				}
			});
			thresholdsPanel.add(seedField);
			add(thresholdsPanel);
			seedField.setBounds(xs[1], y + 2, 150, 20);
		}

		y += ystep;

		{
			JLabel incompleteLabel = factory.createLabel("Number of activities");
			thresholdsPanel.add(incompleteLabel);
			incompleteLabel.setBounds(xs[0], y, xs[1] - xs[0] - xmargin, 20);

			final JTextField sizeField = new JTextField(parameters.getNumberOfActivities() + "");
			sizeField.addKeyListener(new KeyListener() {
				public void keyTyped(KeyEvent arg0) {

				}

				public void keyPressed(KeyEvent arg0) {

				}

				public void keyReleased(KeyEvent arg0) {
					parameters.setNumberOfActivities(Integer.valueOf(sizeField.getText()));
				}
			});
			thresholdsPanel.add(sizeField);
			add(thresholdsPanel);
			sizeField.setBounds(xs[1], y + 2, 150, 20);
		}
		
		y += ystep;
		
		{
			JLabel incompleteLabel = factory.createLabel("Maximum number of children per node");
			thresholdsPanel.add(incompleteLabel);
			incompleteLabel.setBounds(xs[0], y, xs[1] - xs[0] - xmargin, 20);

			final JTextField sizeField = new JTextField(parameters.getMaxNumberOfChildren() + "");
			sizeField.addKeyListener(new KeyListener() {
				public void keyTyped(KeyEvent arg0) {

				}

				public void keyPressed(KeyEvent arg0) {

				}

				public void keyReleased(KeyEvent arg0) {
					parameters.setMaxNumberOfChildren(Integer.valueOf(sizeField.getText()));
				}
			});
			thresholdsPanel.add(sizeField);
			add(thresholdsPanel);
			sizeField.setBounds(xs[1], y + 2, 150, 20);
		}
		
		y += ystep;

		{
			JLabel incompleteLabel = factory.createLabel("No start+end in loop");
			thresholdsPanel.add(incompleteLabel);
			incompleteLabel.setBounds(xs[0], y, xs[1] - xs[0] - xmargin, 20);

			final JCheckBox checkbox = new JCheckBox();
			checkbox.setSelected(parameters.isStartEndDisjointInLoop());
			checkbox.addKeyListener(new KeyListener() {
				public void keyTyped(KeyEvent arg0) {

				}

				public void keyPressed(KeyEvent arg0) {

				}

				public void keyReleased(KeyEvent arg0) {
					parameters.setStartEndDisjointInLoop(checkbox.isSelected());
				}
			});
			thresholdsPanel.add(checkbox);
			add(thresholdsPanel);
			checkbox.setBounds(xs[1], y + 2, 20, 20);
		}

		setLayout(null);
		validate();
		repaint();
	}
}

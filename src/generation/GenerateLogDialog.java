package generation;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class GenerateLogDialog extends JPanel {

	private static final long serialVersionUID = 7233338381448125579L;

	public GenerateLogDialog(final GenerateLogParameters parameters) {
		SlickerFactory factory = SlickerFactory.instance();

		JPanel thresholdsPanel = factory.createRoundedPanel(15, Color.gray);
		thresholdsPanel.setLayout(null);
		thresholdsPanel.setBounds(0, 0, 570, 280);

		JLabel thresholdTitle = factory.createLabel("Generate log from process tree");
		thresholdTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
		thresholdsPanel.add(thresholdTitle);
		thresholdTitle.setBounds(10, 10, 300, 30);

		{
			JLabel seedLabel = factory.createLabel("Random seed");
			thresholdsPanel.add(seedLabel);
			seedLabel.setBounds(20, 50, 100, 20);

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
			seedField.setBounds(165, 52, 360, 20);
		}

		{
			JLabel incompleteLabel = factory.createLabel("Number of traces");
			thresholdsPanel.add(incompleteLabel);
			incompleteLabel.setBounds(20, 110, 150, 20);
		
			final JTextField sizeField = new JTextField(parameters.getNumberOfTraces() + "");
			sizeField.addKeyListener(new KeyListener() {
				public void keyTyped(KeyEvent arg0) {

				}

				public void keyPressed(KeyEvent arg0) {

				}

				public void keyReleased(KeyEvent arg0) {
					parameters.setNumberOfTraces(Integer.valueOf(sizeField.getText()));
				}
			});
			thresholdsPanel.add(sizeField);
			add(thresholdsPanel);
			sizeField.setBounds(165, 112, 360, 20);
		}
		
		{
			JLabel noisyTracesLabel = factory.createLabel("Number of noisy traces");
			thresholdsPanel.add(noisyTracesLabel);
			noisyTracesLabel.setBounds(20, 170, 150, 20);
			
			final JTextField sizeField = new JTextField(parameters.getNoisyTraces() + "");
			sizeField.addKeyListener(new KeyListener() {
				public void keyTyped(KeyEvent arg0) {

				}

				public void keyPressed(KeyEvent arg0) {

				}

				public void keyReleased(KeyEvent arg0) {
					parameters.setNumberOfNoisyTraces(Integer.valueOf(sizeField.getText()));
				}
			});
			thresholdsPanel.add(sizeField);
			add(thresholdsPanel);
			sizeField.setBounds(165, 172, 360, 20);
		}
		
		{
			JLabel noisyEventsPerTraceLabel = factory.createLabel("Number of events per noisy trace");
			thresholdsPanel.add(noisyEventsPerTraceLabel);
			noisyEventsPerTraceLabel.setBounds(20, 230, 150, 20);
			
			final JTextField sizeField = new JTextField(parameters.getNoiseEventsPerTrace() + "");
			sizeField.addKeyListener(new KeyListener() {
				public void keyTyped(KeyEvent arg0) {

				}

				public void keyPressed(KeyEvent arg0) {

				}

				public void keyReleased(KeyEvent arg0) {
					parameters.setNoiseEventsPerTrace(Integer.valueOf(sizeField.getText()));
				}
			});
			thresholdsPanel.add(sizeField);
			add(thresholdsPanel);
			sizeField.setBounds(165, 232, 360, 20);
		}

		setLayout(null);
		validate();
		repaint();
	}
}

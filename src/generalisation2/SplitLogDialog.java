package generalisation2;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class SplitLogDialog extends JPanel {

	private static final long serialVersionUID = 7693870370139578439L;
	private final JLabel noiseLabel;
	private final JSlider noiseSlider;
	private final JLabel noiseValue;
	private float value = 0.3f;

	public SplitLogDialog() {
		SlickerFactory factory = SlickerFactory.instance();

		int gridy = 1;

		setLayout(new GridBagLayout());
		
		JLabel spacer = factory.createLabel(" ");
		{
			GridBagConstraints cSpacer = new GridBagConstraints();
			cSpacer.gridx = 0;
			cSpacer.gridy = gridy;
			cSpacer.anchor = GridBagConstraints.WEST;
			add(spacer, cSpacer);
		}

		//noise threshold
		noiseLabel = factory.createLabel("Part of traces to be put in test set");
		{
			GridBagConstraints cNoiseLabel = new GridBagConstraints();
			cNoiseLabel.gridx = 0;
			cNoiseLabel.gridy = 1;
			cNoiseLabel.anchor = GridBagConstraints.WEST;
			add(noiseLabel, cNoiseLabel);
		}

		noiseSlider = factory.createSlider(SwingConstants.HORIZONTAL);
		{
			noiseSlider.setMinimum(0);
			noiseSlider.setMaximum(1000);
			noiseSlider.setValue((int) (value * 1000));
			GridBagConstraints cNoiseSlider = new GridBagConstraints();
			cNoiseSlider.gridx = 1;
			cNoiseSlider.gridy = 1;
			cNoiseSlider.fill = GridBagConstraints.HORIZONTAL;
			cNoiseSlider.anchor = GridBagConstraints.NORTH;
			add(noiseSlider, cNoiseSlider);
		}

		noiseValue = factory.createLabel(String.format("%.2f", value));
		{
			GridBagConstraints cNoiseValue = new GridBagConstraints();
			cNoiseValue.gridx = 2;
			cNoiseValue.gridy = 1;
			cNoiseValue.anchor = GridBagConstraints.NORTH;
			add(noiseValue, cNoiseValue);
		}
		
//		gridy++;
//
//		{
//			GridBagConstraints gbcFiller = new GridBagConstraints();
//			gbcFiller.weighty = 1.0;
//			gbcFiller.gridy = gridy;
//			gbcFiller.fill = GridBagConstraints.VERTICAL;
//			add(Box.createGlue(), gbcFiller);
//		}

		noiseSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				value = (float) (noiseSlider.getValue() / 1000.0);
				noiseValue.setText(String.format("%.2f", value));
			}
		});
	}
	
	public float getValue() {
		return value;
	}
}

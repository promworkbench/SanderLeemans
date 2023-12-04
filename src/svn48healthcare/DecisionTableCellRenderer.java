package svn48healthcare;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DisplayType;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DisplayType.Type;

public class DecisionTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -7148998664457522071L;

	public final DecimalFormat numberFormat = new DecimalFormat("0.0000");

	public DecisionTableCellRenderer() {
		setHorizontalTextPosition(SwingConstants.LEADING);
		setVerticalAlignment(JLabel.TOP);
	}

	public Component getTableCellRendererComponent(JTable table, Object object, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, object, isSelected, hasFocus, row, column);

		//default properties
		setHorizontalAlignment(JLabel.LEFT);

		if (object == null) {
			setText("");
			setIcon(null);
		} else if (object instanceof DisplayType) {
			if (((DisplayType) object).getType() == Type.image) {
				//image
				setText("");
				BufferedImage im = ((DisplayType.Image) object).getImage();
				setIcon(new ImageIcon(im));
			} else {
				//text
				setText(object.toString());
				setIcon(null);
			}
			setHorizontalAlignment(((DisplayType) object).getHorizontalAlignment());
		} else if (object instanceof ImageIcon) {
			setText("");
			setIcon((ImageIcon) object);
		} else if (object instanceof Pair<?, ?>) {
			@SuppressWarnings("unchecked")
			Pair<Integer, ImageIcon> p = (Pair<Integer, ImageIcon>) object;
			setText(p.getA() + " ");
			setIcon(p.getB());
		} else {
			setText(object.toString());
			setIcon(null);
		}

		return this;
	}
}
package org.processmining.cohortanalysis.visualisation;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DisplayType;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DisplayType.Type;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMDecorator;

public class ProcessDifferencesParetoPanel extends JPanel {

	private static final long serialVersionUID = 2551557353855744026L;

	public final static int blockSize = 10;
	public final static int margin = 25;

	private ProcessDifferencesPareto processDifferences;

	private int selectedIndex = -1;

	public ProcessDifferencesParetoPanel() {
		setOpaque(false);

		setToolTipText("blabla");
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		double minx = processDifferences.getMinAbsoluteDifference();
		double maxx = processDifferences.getMaxAbsoluteDifference();
		double miny = processDifferences.getMinRelativeDifference();
		double maxy = processDifferences.getMaxRelativeDifference();

		g.setColor(IvMDecorator.textColour);

		for (int index = 0; index < processDifferences.size(); index++) {
			int x = getX(minx, maxx, index);
			int y = getY(miny, maxy, index);
			g.drawLine(x - blockSize / 2, y, x + blockSize / 2, y);
			g.drawLine(x, y - 5, x, y + 5);

			if (index == selectedIndex) {
				g.fillRect(x - blockSize / 2, y - blockSize / 2, blockSize, blockSize);

				g.drawLine(x, margin, x, getHeight() - margin);
				g.drawLine(margin, y, getWidth() - margin, y);
			}
		}

		Graphics2D g2 = (Graphics2D) g;
		g2.drawString("absolute difference", 20, getHeight() - 5);

		g2.rotate(-0.5 * Math.PI);
		g2.drawString("relative difference", -getHeight() + 20, 10);
		g2.rotate(0.5 * Math.PI);
	}

	public int getX(double minx, double maxx, int index) {
		return margin + ((int) (((processDifferences.getAbsoluteDifference(index) - minx) / (maxx - minx))
				* (this.getWidth() - 2 * margin)));
	}

	public int getY(double miny, double maxy, int index) {
		int height = this.getHeight();
		return height - ((int) (((processDifferences.getRelativeDifference(index) - miny) / (maxy - miny))
				* (height - 2 * margin))) - margin;
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		Point p = event.getPoint();
		double x = p.getX();
		double y = p.getY();

		double minx = processDifferences.getMinAbsoluteDifference();
		double maxx = processDifferences.getMaxAbsoluteDifference();
		double miny = processDifferences.getMinRelativeDifference();
		double maxy = processDifferences.getMaxRelativeDifference();

		ArrayList<String> result = new ArrayList<>();

		for (int index = 0; index < processDifferences.size(); index++) {
			int indexX = getX(minx, maxx, index);
			int indexY = getY(miny, maxy, index);

			if (Math.abs(x - indexX) <= blockSize / 2 && Math.abs(y - indexY) <= blockSize / 2) {
				DisplayType from = processDifferences.getFrom(index);
				DisplayType to = processDifferences.getTo(index);
				DisplayType absolute = DisplayType.numeric(processDifferences.getAbsoluteDifference(index));
				DisplayType relative = DisplayType.numeric(processDifferences.getRelativeDifference(index));
				String r = "<tr><td>" + from;
				if (to.getType() != Type.NA) {
					r += " -> " + to;
				}
				result.add(r + "</td><td>&#916;abs " + absolute + "</td><td>&#916;rel " + relative + "</td></tr>");
			}
		}
		if (result.isEmpty()) {
			return null;
		}

		return "<html><table>" + String.join("<br>", result) + "</table></html>";
	}

	public void setData(ProcessDifferencesPareto processDifferences) {
		this.processDifferences = processDifferences;
		repaint();
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
		repaint();
	}
}
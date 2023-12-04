package svn45crimes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.math.plot.utils.Array;
import org.processmining.plugins.graphviz.colourMaps.ColourMap;
import org.processmining.plugins.graphviz.colourMaps.ColourMapViridis;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMDecorator;

public class CorrelationPlot {

	private int sizeX1DPlot = 0;
	private int marginX = 0;
	private int sizeX2DPlot = 150;

	private int sizeY1DPlot = 0;
	private int marginY = 0;
	private int sizeY2DPlot = 150;

	private int alias = 5;
	private ColourMap colourMap = new ColourMapViridis();
	private Color backgroundFigure = Color.white;
	private Color backgroundPlot = Color.white;

	public BufferedImage create(String nameX, double[] valuesX, String nameY, double[] valuesY, double modelStart,
			double modelSlope) {

		double minX = Array.min(valuesX);
		double maxX = Array.max(valuesX);
		double minY = Array.min(valuesY);
		double maxY = Array.max(valuesY);

		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

		//set background
		{
			Graphics2D ig2 = image.createGraphics();
			ig2.setBackground(backgroundFigure);
			ig2.clearRect(0, 0, getWidth(), getHeight());
		}

		fillImage2DPlot(image, valuesX, minX, maxX, valuesY, minY, maxY, sizeX1DPlot + marginX, 0, sizeX2DPlot,
				sizeY2DPlot, modelStart, modelSlope);

		if (sizeX1DPlot > 0 && sizeY1DPlot > 0) {
			fillImage1DPlotHorizontal(image, valuesX, minX, maxX, sizeX1DPlot + marginX, sizeY2DPlot + marginY,
					sizeX2DPlot, sizeY1DPlot);
			drawTextHorizontal(image, nameX, sizeX1DPlot + marginX, sizeY2DPlot, sizeX2DPlot, marginY);

			fillImage1DPlotVertical(image, valuesY, minY, maxY, 0, 0, sizeX1DPlot, sizeY2DPlot);
			drawTextVertical(image, nameY, sizeX1DPlot, 0, marginX, sizeY2DPlot);
		}
		return image;
	}

	public void drawTextHorizontal(BufferedImage image, String name, int offsetX, int offsetY, int sizeX, int sizeY) {
		Graphics2D g = (Graphics2D) image.getGraphics();

		Font font = IvMDecorator.font;
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics(font);

		int width = metrics.stringWidth(name);
		int height = ((metrics.getAscent() + metrics.getDescent()) / 2);

		int offsetXp = offsetX + (sizeX - width) / 2;
		offsetXp = Math.max(offsetX, offsetXp);
		offsetY += (sizeY / 2) + (height / 2);
		offsetY += 2; //manual adjustment

		g.setColor(IvMDecorator.textColour);

		g.drawString(name, offsetXp, offsetY);

		g.drawString("0", offsetX, offsetY);

		String end = "1";
		int widthE = metrics.stringWidth(end);
		g.drawString(end, offsetX + sizeX - widthE, offsetY);
	}

	public void drawTextVertical(BufferedImage image, String name, int offsetX, int offsetY, int sizeX, int sizeY) {
		Graphics2D g = (Graphics2D) image.getGraphics();

		Font font = IvMDecorator.font;
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics(font);

		int originX = offsetX;
		int originY = offsetY + sizeY;

		g.rotate(-Math.PI / 2, originX, originY);

		g.setColor(IvMDecorator.textColour);
		{
			int width = metrics.stringWidth(name);
			int height = ((metrics.getAscent() + metrics.getDescent()) / 2) + 2;
			g.drawString(name, //
					originX - //
							width / 2 + //translate text width
							sizeY / 2, //translate to center of graph
					originY + //
							height / 2 + //translate text height 
							offsetY + //translate empty space
							sizeX / 2);
		}

		{
			String start = "0";
			int width = metrics.stringWidth(start);
			int height = ((metrics.getAscent() + metrics.getDescent()) / 2) + 2;
			g.drawString(start, //
					originX, //
					originY + //
							height / 2 + //translate text height 
							offsetY + //translate empty space
							sizeX / 2);
		}

		{
			String end = "1";
			int width = metrics.stringWidth(end);
			int height = ((metrics.getAscent() + metrics.getDescent()) / 2) + 2;
			g.drawString(end, //
					originX + //
							sizeY + //translate to end of graph
							-width, //translate to right-align
					originY + //
							height / 2 + //translate text height 
							offsetY + //translate empty space
							sizeX / 2);
		}

		g.setColor(IvMDecorator.textColour);
	}

	public void fillImage1DPlotHorizontal(BufferedImage image, double[] valuesX, double minX, double maxX, int offsetX,
			int offsetY, int sizeX, int sizeY) {
		double[] counts = new double[sizeX];
		double max = 1;
		if (valuesX.length > 0) {

			//fill array
			{
				for (int i = 0; i < valuesX.length; i++) {
					double x = valuesX[i];

					if (x > -Double.MAX_VALUE) {
						int indexX = (int) Math.round((sizeX - 1) * ((x - minX) / (maxX - minX)));

						if (indexX >= counts.length || indexX < 0) {
							System.out.println(
									"indexX " + indexX + ", sizeX " + sizeX + ", minX " + minX + ", maxX " + maxX);
						}

						counts[indexX]++;
					} else {
						//non-present value
					}
				}
			}

			//get extrema
			max = 0;
			{
				for (int x = 0; x < sizeX; x++) {
					max = Math.max(max, Array.max(counts[x]));
				}
			}
		}

		//set pixels
		for (int x = 0; x < sizeX; x++) {
			Color colour = counts[x] > 0 ? colourMap.colour(counts[x], 0, max) : backgroundPlot;
			for (int y = 0; y < sizeY; y++) {
				image.setRGB(x + offsetX, y + offsetY, colour.getRGB());
			}
		}
	}

	public void fillImage1DPlotVertical(BufferedImage image, double[] valuesY, double minY, double maxY, int offsetX,
			int offsetY, int sizeX, int sizeY) {
		double[] counts = new double[sizeY];
		double max = 1;
		if (valuesY.length > 0) {

			//fill array
			{
				for (int i = 0; i < valuesY.length; i++) {
					double y = valuesY[i];
					if (y > -Double.MAX_VALUE) {
						int indexY = (int) Math.round((sizeY - 1) * ((y - minY) / (maxY - minY)));

						counts[indexY]++;
					} else {
						//non-present value
					}
				}
			}

			//get extrema
			max = 0;
			{
				for (int y = 0; y < sizeY; y++) {
					max = Math.max(max, Array.max(counts[y]));
				}
			}
		}

		//set pixels (invert Y)
		for (int y = 0; y < sizeY; y++) {
			Color colour = counts[y] > 0 ? colourMap.colour(counts[y], 0, max) : backgroundPlot;
			for (int x = 0; x < sizeX; x++) {
				image.setRGB(x + offsetX, (sizeY - y) + offsetY, colour.getRGB());
			}
		}
	}

	public void fillImage2DPlot(BufferedImage image, double[] valuesX, double minX, double maxX, double[] valuesY,
			double minY, double maxY, int offsetX, int offsetY, int sizeX, int sizeY, double modelStart,
			double modelSlope) {
		double[][] counts = new double[sizeX][sizeY];
		double max = 1;

		if (valuesX.length > 0 && valuesY.length > 0) {

			//fill array
			{
				for (int i = 0; i < valuesX.length; i++) {
					double x = valuesX[i];
					int indexX = (int) Math.round((sizeX - 1) * ((x - minX) / (maxX - minX)));

					double y = valuesY[i];
					int indexY = (int) Math.round((sizeY - 1) * ((y - minY) / (maxY - minY)));

					//counts[indexX][indexY]++;
					//System.out.println("index " + indexX + " " + indexY);
					for (int aX = indexX - alias; aX <= indexX + alias; aX++) {
						for (int aY = indexY - alias; aY <= indexY + alias; aY++) {
							if (0 <= aX && aX < sizeX && 0 <= aY && aY < sizeY) {
								double diagonal = Math.sqrt(Math.pow(aX - indexX, 2) + Math.pow(aY - indexY, 2));
								double value = Math.max(0, 1 - (diagonal / alias));
								//System.out.println(" increase " + aX + " " + aY + " by " + value);
								counts[aX][aY] += value;
							}
						}
					}
				}
			}

			//get extrema
			max = 0;
			{
				for (int x = 0; x < sizeX; x++) {
					max = Math.max(max, Array.max(counts[x]));
				}
			}
		}

		//set pixels (invert Y)
		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				Color colour = backgroundPlot;
				if (counts[x][y] > 0) {
					colour = colourMap.colour(counts[x][y], 0, max);
				}
				image.setRGB(x + offsetX, (sizeY - y) + offsetY - 1, colour.getRGB());
			}
		}

		//draw model
		{
			Graphics2D g = (Graphics2D) image.getGraphics();

			int x = 0;
			int y = (int) Math.round((sizeY - 1) * ((modelStart - minY) / (maxY - minY)));
			int x1 = sizeX;
			int y1 = (int) Math
					.round((sizeY - 1) * (((modelStart + modelSlope * (maxX - minX)) - minY) / (maxY - minY)));
			g.setColor(Color.red);
			g.setStroke(new BasicStroke(3));
			g.drawLine(x + offsetX, (sizeY - y) + offsetY, x1 + offsetX, (sizeY - y1) + offsetY);
		}
	}

	public int getSizeX1DPlot() {
		return sizeX1DPlot;
	}

	public void setSizeX1DPlot(int sizeX1DPlot) {
		this.sizeX1DPlot = sizeX1DPlot;
	}

	public int getMarginX() {
		return marginX;
	}

	public void setMarginX(int marginX) {
		this.marginX = marginX;
	}

	public int getSizeX2DPlot() {
		return sizeX2DPlot;
	}

	public void setSizeX2DPlot(int sizeX2DPlot) {
		this.sizeX2DPlot = sizeX2DPlot;
	}

	public int getSizeY1DPlot() {
		return sizeY1DPlot;
	}

	public void setSizeY1DPlot(int sizeY1DPlot) {
		this.sizeY1DPlot = sizeY1DPlot;
	}

	public int getWidth() {
		return getSizeX1DPlot() + getMarginX() + getSizeX2DPlot();
	}

	public int getHeight() {
		return getSizeY1DPlot() + getMarginY() + getSizeY2DPlot();
	}

	public int getMarginY() {
		return marginY;
	}

	public void setMarginY(int marginY) {
		this.marginY = marginY;
	}

	public int getSizeY2DPlot() {
		return sizeY2DPlot;
	}

	public void setSizeY2DPlot(int sizeY2DPlot) {
		this.sizeY2DPlot = sizeY2DPlot;
	}

	public int getAlias() {
		return alias;
	}

	public void setAlias(int alias) {
		this.alias = alias;
	}

	public ColourMap getColourMap() {
		return colourMap;
	}

	public void setColourMap(ColourMap colourMap) {
		this.colourMap = colourMap;
	}

	public Color getBackgroundFigure() {
		return backgroundFigure;
	}

	public void setBackgroundFigure(Color backgroundFigure) {
		this.backgroundFigure = backgroundFigure;
	}

	public Color getBackgroundPlot() {
		return backgroundPlot;
	}

	public void setBackgroundPlot(Color backgroundPlot) {
		this.backgroundPlot = backgroundPlot;
	}
}

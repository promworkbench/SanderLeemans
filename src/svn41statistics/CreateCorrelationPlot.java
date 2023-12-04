package svn41statistics;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.processmining.statisticaltests.association.CorrelationPlot;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

public class CreateCorrelationPlot {

	/**
	 * 
	 * @param values
	 *            [x, y]
	 * @param outputImageFile
	 * @throws IOException
	 */
	public static void create(double[][] values, File outputImageFile) throws IOException {
		CorrelationPlot plot = new CorrelationPlot();
		plot.setBackgroundPlot(Color.white);
		plot.setBackgroundFigure(Color.white);
		plot.setSizeX2DPlot(200);
		plot.setSizeY2DPlot(200);
		plot.setSizeX1DPlot(0);
		plot.setSizeY1DPlot(0);
		plot.setMarginX(0);
		plot.setMarginY(0);

		//read the correlation sample file
		TDoubleList deltaValues = new TDoubleArrayList();
		TDoubleList deltaTraces = new TDoubleArrayList();
		deltaValues.addAll(values[0]);
		deltaTraces.addAll(values[1]);

		System.out.println("number of samples " + deltaValues.size());

		outputImageFile.mkdirs();

		BufferedImage image = plot.create("Δ value", deltaValues.toArray(), "Δ trace", deltaTraces.toArray());
		ImageIO.write(image, "png", outputImageFile);
	}
}

package crc;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class Run extends ApplicationFrame {

	private static final long serialVersionUID = -7204395025278089343L;

	public static void main(final String[] args) {

		ExperimentParameters parameters = new ExperimentParameters();
		DataIn dataIn = new DataIn();
		DataOut dataOut = new DataOut(parameters.getDataOutFile(), parameters);

		final Run demo = new Run("Data", dataIn, dataOut);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}

	public Run(final String title, DataIn dataIn, DataOut dataOut) {
		super(title);

		final XYSeries coarse;
		final XYSeries middle;
		final XYSeries fine;
		if (1 == 1) {
			//plot the data
			coarse = new XYSeries("Data coarse");
			middle = new XYSeries("Data middle");
			fine = new XYSeries("Data fine");
			for (int i = 0; i < dataOut.outCoarse.length; i++) {
				coarse.add(((double) i) / dataOut.outCoarse.length, dataOut.outCoarse[i]);
				middle.add(((double) i) / dataOut.outCoarse.length, dataOut.outMiddle[i]);
				fine.add(((double) i) / dataOut.outCoarse.length, dataOut.outFine[i]);
			}
		} else {
			coarse = null;
			middle = null;
			fine = null;
		}

		final XYSeries averageCoarse;
		final XYSeries averageMiddle;
		final XYSeries averageFine;
		if (1 == 2) {
			//plot the average data
			averageCoarse = new XYSeries("Data average coarse");
			averageMiddle = new XYSeries("Data average middle");
			averageFine = new XYSeries("Data average fine");
			for (int i = 0; i < dataOut.outCoarse.length; i++) {
				averageCoarse.add(((double) i) / dataOut.windowOutCoarse.length, dataOut.windowOutCoarse[i]);
				averageMiddle.add(((double) i) / dataOut.windowOutCoarse.length, dataOut.windowOutMiddle[i]);
				averageFine.add(((double) i) / dataOut.windowOutCoarse.length, dataOut.windowOutFine[i]);
			}
		} else {
			averageCoarse = null;
			averageMiddle = null;
			averageFine = null;
		}

		final XYSeries modelCoarse;
		final XYSeries modelMiddle;
		final XYSeries modelFine;
		if (1 == 2) {
			//plot the model
			modelCoarse = new XYSeries("Model Coarse");
			modelMiddle = new XYSeries("Model Middle");
			modelFine = new XYSeries("Model Fine");

			State state = new State();
			state.coarse = dataOut.outCoarse[0];
			state.middle = dataOut.outMiddle[0];
			state.fine = dataOut.outFine[0];
			Model model = new Model();
			ModelParameters parameters = new ModelParameters();
			Product product = new Product();

			//run the model
			for (int i = 0; i < dataOut.outCoarse.length; i++) {
				double x = ((double) i) / dataOut.outCoarse.length;
				model.updateProduct(state, product, parameters);
				state = model.newState(state, parameters, product, dataIn);

				modelCoarse.add(x, product.coarse);
				modelMiddle.add(x, product.middle);
				modelFine.add(x, product.fine);

				//check state sums to 1
				{
					double sum = state.coarse + state.middle + state.fine;
					if (sum > 1.00000001 || sum < 0.99999999999) {
						System.out.println("state invalid" + sum);
					}
				}

				//check product sums to 1
				{
					double sum = product.coarse + product.middle + product.fine;
					if (sum > 1.00000001 || sum < 0.99999999999) {
						System.out.println("product invalid" + sum);
					}
				}
			}
		} else {
			modelCoarse = null;
			modelMiddle = null;
			modelFine = null;
		}
		
		final XYSeries averageModelCoarse;
		final XYSeries averageModelMiddle;
		final XYSeries averageModelFine;
		if (1 == 2) {
			//plot the model
			averageModelCoarse = new XYSeries("Model average Coarse");
			averageModelMiddle = new XYSeries("Model average Middle");
			averageModelFine = new XYSeries("Model average Fine");

			State state = new State();
			state.coarse = dataOut.windowOutCoarse[0];
			state.middle = dataOut.windowOutMiddle[0];
			state.fine = dataOut.windowOutFine[0];
			Model model = new Model();
			ModelParameters parameters = new ModelParameters();
			Product product = new Product();

			//run the model
			for (int i = 0; i < dataOut.windowOutCoarse.length; i++) {
				double x = ((double) i) / dataOut.windowOutCoarse.length;
				model.updateProduct(state, product, parameters);
				state = model.newState(state, parameters, product, dataIn);

				averageModelCoarse.add(x, product.coarse);
				averageModelMiddle.add(x, product.middle);
				averageModelFine.add(x, product.fine);

				//check state sums to 1
				{
					double sum = state.coarse + state.middle + state.fine;
					if (sum > 1.00000001 || sum < 0.99999999999) {
						System.out.println("state invalid" + sum);
					}
				}

				//check product sums to 1
				{
					double sum = product.coarse + product.middle + product.fine;
					if (sum > 1.00000001 || sum < 0.99999999999) {
						System.out.println("product invalid" + sum);
					}
				}
			}
		} else {
			averageModelCoarse = null;
			averageModelMiddle = null;
			averageModelFine = null;
		}

		//create the graph
		final XYSeriesCollection data = new XYSeriesCollection();
		if (coarse != null) {
			data.addSeries(coarse);
			data.addSeries(middle);
			data.addSeries(fine);
		}
		if (averageCoarse != null) {
			data.addSeries(averageCoarse);
			data.addSeries(averageMiddle);
			data.addSeries(averageFine);
		}
		if (modelCoarse != null) {
			data.addSeries(modelCoarse);
			data.addSeries(modelMiddle);
			data.addSeries(modelFine);
		}
		if (averageModelCoarse != null) {
			data.addSeries(averageModelCoarse);
			data.addSeries(averageModelMiddle);
			data.addSeries(averageModelFine);
		}
		final JFreeChart chart = ChartFactory.createXYLineChart("Model", "time", "fraction", data,
				PlotOrientation.VERTICAL, true, true, false);
		chart.getXYPlot().getRangeAxis().setUpperBound(1);
		chart.getXYPlot().getDomainAxis().setUpperBound(1);

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);

	}
}

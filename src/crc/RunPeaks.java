package crc;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class RunPeaks extends ApplicationFrame {

	private static final long serialVersionUID = -7204395025278089343L;

	public static void main(final String[] args) {

		ExperimentParameters parameters = new ExperimentParameters();
		DataIn dataIn = new DataIn();
		DataOut dataOut = new DataOut(parameters.getDataOutFile(), parameters);

		final RunPeaks demo = new RunPeaks("Data peaks", dataIn, dataOut);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}

	public RunPeaks(final String title, DataIn dataIn, DataOut dataOut) {
		super(title);
		
		double max = 0.1;
		
		Bins bins = new Bins(200, max);
		for (int i = 0; i < dataOut.outCoarse.length; i++) {
			bins.add(Math.abs(dataOut.outCoarse[i] - dataOut.averageOutCoarse));
		}

		//create the graph
		final XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(bins.toXYSeries("bins"));
		final JFreeChart chart = ChartFactory.createXYLineChart("Model", "time", "fraction", data,
				PlotOrientation.VERTICAL, true, true, false);
		chart.getXYPlot().getRangeAxis().setUpperBound(bins.getMaxNumberPerBin());
		chart.getXYPlot().getDomainAxis().setUpperBound(max);

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);

	}
}

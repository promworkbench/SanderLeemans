package org.processmining.cohortanalysis.visualisation;

import java.util.Iterator;

import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DisplayType;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogInfo;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogMetrics;

import com.google.common.primitives.Doubles;

import gnu.trove.iterator.TIntIterator;

public class ProcessDifferencesParetoImpl implements ProcessDifferencesPareto {

	private final IvMModel model;
	private final int[] from;
	private final int[] to;
	private final double[] differencesAbsolute;
	private final double[] differencesRelative;
	private final double minAbsolute;
	private final double maxAbsolute;
	private final double minRelative;
	private final double maxRelative;

	public ProcessDifferencesParetoImpl(IvMModel model, IvMLogInfo logInfo, IvMLogInfo logInfoAnti) {
		assert model.isDfg();
		this.model = model;

		int size = 0;
		{
			//count nodes
			for (int node : model.getAllNodes()) {
				if (model.isActivity(node)) {
					size++;
				}
			}

			//count edges
			for (Iterator<Long> it = model.getDfg().getEdges().iterator(); it.hasNext();) {
				it.next();
				size++;
			}

			//empty trace edge
			if (model.getDfg().isEmptyTraces()) {
				size++;
			}

			//start edges
			for (TIntIterator it = model.getDfg().getStartNodes().iterator(); it.hasNext();) {
				it.next();
				size++;
			}

			//end edges
			for (TIntIterator it = model.getDfg().getEndNodes().iterator(); it.hasNext();) {
				it.next();
				size++;
			}
		}

		//initialise arrays
		from = new int[size];
		to = new int[size];
		differencesAbsolute = new double[size];
		differencesRelative = new double[size];
		{
			int index = 0;

			//nodes
			for (int node : model.getAllNodes()) {
				if (model.isActivity(node)) {
					from[index] = node;
					to[index] = -1;
					double cohort = IvMLogMetrics.getNumberOfTracesRepresented(model, node, false, logInfo)
							/ (logInfo.getNumberOfTraces() * 1.0);
					double cohortAnti = IvMLogMetrics.getNumberOfTracesRepresented(model, node, false, logInfoAnti)
							/ (logInfoAnti.getNumberOfTraces() * 1.0);
					differencesAbsolute[index] = Math.abs(cohort - cohortAnti);
					differencesRelative[index] = Math.abs(cohort / (cohort + cohortAnti) - 0.5) * 2;
					index++;
				}
			}

			//edges
			for (long edge : model.getDfg().getEdges()) {
				int source = model.getDfg().getEdgeSource(edge);
				int target = model.getDfg().getEdgeTarget(edge);

				from[index] = source;
				to[index] = target;
				double cohort = IvMLogMetrics.getNumberOfTracesRepresented(model, source, target, true, logInfo)
						/ (logInfo.getNumberOfTraces() * 1.0);
				double cohortAnti = IvMLogMetrics.getNumberOfTracesRepresented(model, source, target, true, logInfoAnti)
						/ (logInfoAnti.getNumberOfTraces() * 1.0);

				differencesAbsolute[index] = Math.abs(cohort - cohortAnti);
				differencesRelative[index] = Math.abs(cohort / (cohort + cohortAnti) - 0.5) * 2;
				index++;
			}

			//empty trace edge
			if (model.getDfg().isEmptyTraces()) {
				from[index] = -2;
				to[index] = -2;
				double cohort = IvMLogMetrics.getNumberOfTracesRepresented(model, -2, -2, true, logInfo)
						/ (logInfo.getNumberOfTraces() * 1.0);
				double cohortAnti = IvMLogMetrics.getNumberOfTracesRepresented(model, -2, -2, true, logInfoAnti)
						/ (logInfoAnti.getNumberOfTraces() * 1.0);

				differencesAbsolute[index] = Math.abs(cohort - cohortAnti);
				differencesRelative[index] = Math.abs(cohort / (cohort + cohortAnti) - 0.5) * 2;
				index++;
			}

			//start edges
			{
				for (TIntIterator it = model.getDfg().getStartNodes().iterator(); it.hasNext();) {
					int node = it.next();
					from[index] = -2;
					to[index] = node;
					double cohort = IvMLogMetrics.getNumberOfTracesRepresented(model, -1, node, true, logInfo)
							/ (logInfo.getNumberOfTraces() * 1.0);
					double cohortAnti = IvMLogMetrics.getNumberOfTracesRepresented(model, -1, node, true, logInfoAnti)
							/ (logInfoAnti.getNumberOfTraces() * 1.0);

					differencesAbsolute[index] = Math.abs(cohort - cohortAnti);
					differencesRelative[index] = Math.abs(cohort / (cohort + cohortAnti) - 0.5) * 2;
					index++;
				}
			}

			//end edges
			{
				for (TIntIterator it = model.getDfg().getEndNodes().iterator(); it.hasNext();) {
					int node = it.next();
					from[index] = node;
					to[index] = -2;
					double cohort = IvMLogMetrics.getNumberOfTracesRepresented(model, node, -1, true, logInfo)
							/ (logInfo.getNumberOfTraces() * 1.0);
					double cohortAnti = IvMLogMetrics.getNumberOfTracesRepresented(model, node, -1, true, logInfoAnti)
							/ (logInfoAnti.getNumberOfTraces() * 1.0);

					differencesAbsolute[index] = Math.abs(cohort - cohortAnti);
					differencesRelative[index] = Math.abs(cohort / (cohort + cohortAnti) - 0.5) * 2;
					index++;
				}
			}

			minAbsolute = Doubles.min(differencesAbsolute);
			maxAbsolute = Doubles.max(differencesAbsolute);
			minRelative = Doubles.min(differencesRelative);
			maxRelative = Doubles.max(differencesRelative);
		}
	}

	public double getMinAbsoluteDifference() {
		return minAbsolute;
	}

	public double getMaxAbsoluteDifference() {
		return maxAbsolute;
	}

	public double getMinRelativeDifference() {
		return minRelative;
	}

	public double getMaxRelativeDifference() {
		return maxRelative;
	}

	public int size() {
		return differencesAbsolute.length;
	}

	public double getAbsoluteDifference(int index) {
		return differencesAbsolute[index];
	}

	public double getRelativeDifference(int index) {
		return differencesRelative[index];
	}

	public DisplayType getFrom(int index) {
		if (from[index] == -2) {
			return DisplayType.literal("[start]");
		}
		return DisplayType.literal(model.getActivityName(from[index]));
	}

	public DisplayType getTo(int index) {
		if (to[index] == -2) {
			return DisplayType.literal("[end]");
		} else if (to[index] < 0) {
			return DisplayType.NA();
		}
		return DisplayType.literal(model.getActivityName(to[index]));
	}

}

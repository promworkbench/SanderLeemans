package org.processmining.cohortanalysis.visualisation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DisplayType;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogInfo;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogMetrics;

import gnu.trove.iterator.TIntIterator;

public class ProcessDifferencesImpl implements ProcessDifferences {

	private final IvMModel model;
	private final int[] from;
	private final int[] to;
	private final double[] cohort;
	private final double[] cohortAnti;

	private final List<Integer> row2index;

	public ProcessDifferencesImpl(IvMModel model, IvMLogInfo logInfo, IvMLogInfo logInfoAnti) {
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
		cohort = new double[size];
		cohortAnti = new double[size];
		{
			int index = 0;

			//nodes
			for (int node : model.getAllNodes()) {
				if (model.isActivity(node)) {
					from[index] = node;
					to[index] = -1;
					cohort[index] = IvMLogMetrics.getNumberOfTracesRepresented(model, node, false, logInfo)
							/ (logInfo.getNumberOfTraces() * 1.0);
					cohortAnti[index] = IvMLogMetrics.getNumberOfTracesRepresented(model, node, false, logInfoAnti)
							/ (logInfoAnti.getNumberOfTraces() * 1.0);
					index++;
				}
			}

			//edges
			for (long edge : model.getDfg().getEdges()) {
				int source = model.getDfg().getEdgeSource(edge);
				int target = model.getDfg().getEdgeTarget(edge);

				from[index] = source;
				to[index] = target;
				cohort[index] = IvMLogMetrics.getNumberOfTracesRepresented(model, source, target, true, logInfo)
						/ (logInfo.getNumberOfTraces() * 1.0);
				cohortAnti[index] = IvMLogMetrics.getNumberOfTracesRepresented(model, source, target, true, logInfoAnti)
						/ (logInfoAnti.getNumberOfTraces() * 1.0);
				index++;
			}

			//empty trace edge
			if (model.getDfg().isEmptyTraces()) {
				from[index] = -2;
				to[index] = -2;
				cohort[index] = IvMLogMetrics.getNumberOfTracesRepresented(model, -2, -2, true, logInfo)
						/ (logInfo.getNumberOfTraces() * 1.0);
				cohortAnti[index] = IvMLogMetrics.getNumberOfTracesRepresented(model, -2, -2, true, logInfoAnti)
						/ (logInfoAnti.getNumberOfTraces() * 1.0);
				index++;
			}

			//start edges
			{
				for (TIntIterator it = model.getDfg().getStartNodes().iterator(); it.hasNext();) {
					int node = it.next();
					from[index] = -2;
					to[index] = node;
					cohort[index] = IvMLogMetrics.getNumberOfTracesRepresented(model, -1, node, true, logInfo)
							/ (logInfo.getNumberOfTraces() * 1.0);
					cohortAnti[index] = IvMLogMetrics.getNumberOfTracesRepresented(model, -1, node, true, logInfoAnti)
							/ (logInfoAnti.getNumberOfTraces() * 1.0);
					index++;
				}
			}

			//end edges
			{
				for (TIntIterator it = model.getDfg().getEndNodes().iterator(); it.hasNext();) {
					int node = it.next();
					from[index] = node;
					to[index] = -2;
					cohort[index] = IvMLogMetrics.getNumberOfTracesRepresented(model, node, -1, true, logInfo)
							/ (logInfo.getNumberOfTraces() * 1.0);
					cohortAnti[index] = IvMLogMetrics.getNumberOfTracesRepresented(model, node, -1, true, logInfoAnti)
							/ (logInfoAnti.getNumberOfTraces() * 1.0);
					index++;
				}
			}

			//sort
			{
				//initialise
				row2index = new ArrayList<>();
				for (int i = 0; i < index; i++) {
					row2index.add(i);
				}

				Comparator<Integer> comp = new Comparator<Integer>() {
					public int compare(Integer k1, Integer k2) {
						double value1 = Math.abs(cohort[k1] / (cohort[k1] + cohortAnti[k1]) - 0.5) * 2
								* Math.abs(cohort[k1] - cohortAnti[k1]);
						double value2 = Math.abs(cohort[k2] / (cohort[k2] + cohortAnti[k2]) - 0.5) * 2
								* Math.abs(cohort[k2] - cohortAnti[k2]);
						return -Double.compare(value1, value2);
					}
				};
				Collections.sort(row2index, comp);
			}
		}
	}

	public int row2index(int row) {
		return row2index.get(row);
	}

	@Override
	public int size() {
		return from.length;
	}

	@Override
	public DisplayType getFrom(int row) {
		if (from[row2index(row)] == -2) {
			return DisplayType.literal("[start]");
		}
		return DisplayType.literal(model.getActivityName(from[row2index(row)]));
	}

	@Override
	public DisplayType getTo(int row) {
		if (to[row2index(row)] == -2) {
			return DisplayType.literal("[end]");
		} else if (to[row2index(row)] < 0) {
			return DisplayType.NA();
		}
		return DisplayType.literal(model.getActivityName(to[row2index(row)]));
	}

	@Override
	public DisplayType getCohort(int row) {
		return DisplayType.numeric(cohort[row2index(row)]);
	}

	@Override
	public DisplayType getAntiCohort(int row) {
		return DisplayType.numeric(cohortAnti[row2index(row)]);
	}

}
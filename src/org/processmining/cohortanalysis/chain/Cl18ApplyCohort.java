package org.processmining.cohortanalysis.chain;

import org.processmining.cohortanalysis.cohort.Cohort;
import org.processmining.cohortanalysis.visualisation.CohortsObject;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IteratorWithPosition;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.ivmfilter.tree.filters.FilterIvMTraceCohort;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogFilteredImpl;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogInfo;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogNotFiltered;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMTrace;

public class Cl18ApplyCohort extends DataChainLinkComputationAbstract<CohortsConfiguration> {

	@Override
	public String getName() {
		return "cohort filter";
	}

	@Override
	public String getStatusBusyMessage() {
		return "Filtering cohort..";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { IvMObject.aligned_log, CohortsObject.selected_cohort, IvMObject.model };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { CohortsObject.aligned_log_filtered, CohortsObject.aligned_log_info_filtered,
				CohortsObject.aligned_log_filtered_anti, CohortsObject.aligned_log_info_filtered_anti };
	}

	@Override
	public IvMObjectValues execute(CohortsConfiguration configuration, IvMObjectValues inputs, IvMCanceller canceller)
			throws Exception {
		IvMLogNotFiltered log = inputs.get(IvMObject.aligned_log);
		Cohort cohort = inputs.get(CohortsObject.selected_cohort);
		IvMModel model = inputs.get(IvMObject.model);

		IvMLogFilteredImpl cohortLog = new IvMLogFilteredImpl(log);
		IteratorWithPosition<IvMTrace> cohortIt = cohortLog.iterator();
		while (cohortIt.hasNext()) {
			IvMTrace trace = cohortIt.next();
			if (!FilterIvMTraceCohort.inCohort(trace, cohort)) {
				cohortIt.remove();
			}
		}

		IvMLogFilteredImpl antiCohortLog = cohortLog.clone();
		antiCohortLog.invert();

		//create the log infos
		IvMLogInfo cohortLogInfo = new IvMLogInfo(cohortLog, model);
		IvMLogInfo antiCohortLogInfo = new IvMLogInfo(antiCohortLog, model);

		return new IvMObjectValues().//
				s(CohortsObject.aligned_log_filtered, cohortLog).//
				s(CohortsObject.aligned_log_info_filtered, cohortLogInfo).//
				s(CohortsObject.aligned_log_filtered_anti, antiCohortLog).//
				s(CohortsObject.aligned_log_info_filtered_anti, antiCohortLogInfo);
	}
}
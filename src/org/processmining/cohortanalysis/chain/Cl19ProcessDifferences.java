package org.processmining.cohortanalysis.chain;

import org.processmining.cohortanalysis.visualisation.CohortsObject;
import org.processmining.cohortanalysis.visualisation.CohortsState;
import org.processmining.cohortanalysis.visualisation.ProcessDifferences;
import org.processmining.cohortanalysis.visualisation.ProcessDifferencesImpl;
import org.processmining.cohortanalysis.visualisation.ProcessDifferencesPareto;
import org.processmining.cohortanalysis.visualisation.ProcessDifferencesParetoImpl;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkComputationAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogInfo;

public class Cl19ProcessDifferences extends DataChainLinkComputationAbstract<CohortsConfiguration> {

	@Override
	public String getName() {
		return "differences";
	}

	@Override
	public String getStatusBusyMessage() {
		return "Computing differences..";
	}

	@Override
	public IvMObject<?>[] createInputObjects() {
		return new IvMObject<?>[] { IvMObject.model, CohortsObject.aligned_log_info_filtered,
				CohortsObject.aligned_log_info_filtered_anti };
	}

	@Override
	public IvMObject<?>[] createOutputObjects() {
		return new IvMObject<?>[] { CohortsObject.differences, CohortsObject.differences_pareto };
	}

	public IvMObjectValues execute(CohortsConfiguration configuration, IvMObjectValues inputs, IvMCanceller canceller)
			throws Exception {
		IvMModel model = inputs.get(IvMObject.model);
		IvMLogInfo logInfoCohort = inputs.get(CohortsObject.aligned_log_info_filtered);
		IvMLogInfo logInfoAnti = inputs.get(CohortsObject.aligned_log_info_filtered_anti);

		ProcessDifferencesImpl differences = new ProcessDifferencesImpl(model, logInfoCohort, logInfoAnti);
		ProcessDifferencesParetoImpl differencesPareto = new ProcessDifferencesParetoImpl(model, logInfoCohort,
				logInfoAnti);

		return new IvMObjectValues().//
				s(CohortsObject.differences, differences).//
				s(CohortsObject.differences_pareto, differencesPareto);
	}

	protected void processResult(Pair<ProcessDifferences, ProcessDifferencesPareto> result, CohortsState state) {
		state.setProcessDifferences(result);
	}

	protected void invalidateResult(CohortsState state) {
		state.setProcessDifferences(null);
	}

}
package org.processmining.cohortanalysis.visualisation;

import org.processmining.cohortanalysis.cohort.Cohort;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogFilteredImpl;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogInfo;
import org.processmining.plugins.inductiveVisualMiner.visualisation.ProcessTreeVisualisationInfo;

import com.kitfox.svg.SVGDiagram;

public class CohortsObject {
	public static final IvMObject<Cohort> selected_cohort = IvMObject.c("selected cohort", Cohort.class);

	public static final IvMObject<IvMLogFilteredImpl> aligned_log_filtered = IvMObject.c("aligned log filtered cohort",
			IvMLogFilteredImpl.class);
	public static final IvMObject<IvMLogInfo> aligned_log_info_filtered = IvMObject
			.c("aligned log info filtered cohort", IvMLogInfo.class);
	public static final IvMObject<IvMLogFilteredImpl> aligned_log_filtered_anti = IvMObject
			.c("aligned log filtered anti", IvMLogFilteredImpl.class);
	public static final IvMObject<IvMLogInfo> aligned_log_info_filtered_anti = IvMObject
			.c("aligned log info filtered anti", IvMLogInfo.class);

	public static final IvMObject<Dot> graph_dot_aligned = IvMObject.c("graph dot aligned cohort", Dot.class);
	public static final IvMObject<SVGDiagram> graph_svg_aligned = IvMObject.c("graph svg aligned cohort",
			SVGDiagram.class);
	public static final IvMObject<ProcessTreeVisualisationInfo> graph_visualisation_info_aligned = IvMObject
			.c("graph visualisation info aligned cohort", ProcessTreeVisualisationInfo.class);

	public static final IvMObject<Dot> graph_dot_aligned_anti = IvMObject.c("graph dot aligned anti cohort", Dot.class);
	public static final IvMObject<SVGDiagram> graph_svg_aligned_anti = IvMObject.c("graph svg aligned anti cohort",
			SVGDiagram.class);
	public static final IvMObject<ProcessTreeVisualisationInfo> graph_visualisation_info_aligned_anti = IvMObject
			.c("graph visualisation info aligned anti cohort", ProcessTreeVisualisationInfo.class);

	public static final IvMObject<ProcessDifferences> differences = IvMObject.c("differences",
			ProcessDifferences.class);
	public static final IvMObject<ProcessDifferencesPareto> differences_pareto = IvMObject.c("differences pareto",
			ProcessDifferencesPareto.class);
}

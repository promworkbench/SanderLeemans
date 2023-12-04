package org.processmining.cohortanalysis.visualisation;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.cohortanalysis.cohort.Cohort;
import org.processmining.cohortanalysis.cohort.Cohorts;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.visualisation.DotPanelUserSettings;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogFiltered;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogInfo;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogNotFiltered;
import org.processmining.plugins.inductiveVisualMiner.performance.XEventPerformanceClassifier;
import org.processmining.plugins.inductiveVisualMiner.visualisation.ProcessTreeVisualisationInfo;
import org.processmining.plugins.inductiveminer2.attributes.AttributesInfo;

import com.kitfox.svg.SVGDiagram;

public class CohortsState {

	private final XLog log;

	public CohortsState(XLog log) {
		this.log = log;
	}

	public XLog getLog() {
		return log;
	}

	//==attributes==
	private AttributesInfo attributesInfo;
	private AttributeClassifier initialClassifier;
	private AttributeClassifier[] classifiers;

	public AttributesInfo getAttributesInfo() {
		return attributesInfo;
	}

	public AttributeClassifier getInitialClassifier() {
		return initialClassifier;
	}

	public AttributeClassifier[] getClassifiers() {
		return classifiers;
	}

	public void setAttributesInfo(AttributesInfo info, AttributeClassifier initialClassifier,
			AttributeClassifier[] classifiers) {
		attributesInfo = info;
		this.initialClassifier = initialClassifier;
		this.classifiers = classifiers;
	}

	//==cohorts==
	private Cohorts cohorts;

	public Cohorts getCohorts() {
		return cohorts;
	}

	public void setCohorts(Cohorts cohorts) {
		this.cohorts = cohorts;
	}

	//==discovery settings==
	private double paths = 0.8;

	public double getPaths() {
		return paths;
	}

	public void setPaths(double paths) {
		this.paths = paths;
	}

	//==classifier==
	private XEventPerformanceClassifier performanceClassifier = new XEventPerformanceClassifier(
			new XEventNameClassifier());

	public XEventPerformanceClassifier getPerformanceClassifier() {
		return performanceClassifier;
	}

	public void setClassifier(XEventClassifier classifier) {
		this.performanceClassifier = new XEventPerformanceClassifier(classifier);
	}

	//==model==
	private IvMModel model;

	public IvMModel getModel() {
		return model;
	}

	public void setModel(IvMModel model) {
		this.model = model;
	}

	//==alignment==
	private IvMLogNotFiltered ivmLog;
	private IvMLogInfo ivmLogInfo;

	public void setIvMLog(IvMLogNotFiltered ivMLog, IvMLogInfo ivmLogInfo) {
		this.ivmLog = ivMLog;
		this.ivmLogInfo = ivmLogInfo;
	}

	public IvMLogNotFiltered getIvMLog() {
		return ivmLog;
	}

	public IvMLogInfo getIvMLogInfo() {
		return ivmLogInfo;
	}

	public boolean isAlignmentReady() {
		return ivmLog != null;
	}

	//==layout==
	private Dot dotCohort;
	private SVGDiagram svgDiagramCohort;
	private ProcessTreeVisualisationInfo visualisationInfoCohort;
	private Dot dotAntiCohort;
	private SVGDiagram svgDiagramAntiCohort;
	private ProcessTreeVisualisationInfo visualisationInfoAntiCohort;
	private DotPanelUserSettings graphUserSettings;

	public void setLayoutCohort(Dot dot, SVGDiagram svgDiagram, ProcessTreeVisualisationInfo visualisationInfo) {
		this.dotCohort = dot;
		this.svgDiagramCohort = svgDiagram;
		this.visualisationInfoCohort = visualisationInfo;
	}

	public void setLayoutAntiCohort(Dot dot, SVGDiagram svgDiagram, ProcessTreeVisualisationInfo visualisationInfo) {
		this.dotAntiCohort = dot;
		this.svgDiagramAntiCohort = svgDiagram;
		this.visualisationInfoAntiCohort = visualisationInfo;
	}

	public Dot getDotCohort() {
		return dotCohort;
	}

	public SVGDiagram getSVGDiagramCohort() {
		return svgDiagramCohort;
	}

	public ProcessTreeVisualisationInfo getVisualisationInfoCohort() {
		return visualisationInfoCohort;
	}

	public Dot getDotAntiCohort() {
		return dotAntiCohort;
	}

	public SVGDiagram getSVGDiagramAntiCohort() {
		return svgDiagramAntiCohort;
	}

	public ProcessTreeVisualisationInfo getVisualisationInfoAntiCohort() {
		return visualisationInfoAntiCohort;
	}

	public DotPanelUserSettings getGraphUserSettings() {
		return graphUserSettings;
	}

	public void setGraphUserSettings(DotPanelUserSettings graphUserSettings) {
		this.graphUserSettings = graphUserSettings;
	}

	//==selected cohort==
	private Cohort selectedCohort;

	public Cohort getSelectedCohort() {
		return selectedCohort;
	}

	public void setSelectedCohort(Cohort selectedCohort) {
		this.selectedCohort = selectedCohort;
	}

	//cohort log
	private IvMLogFiltered cohortLog;
	private IvMLogInfo cohortLogInfo;
	private IvMLogFiltered antiCohortLog;
	private IvMLogInfo antiCohortLogInfo;

	public void setCohortLogs(IvMLogFiltered cohortLog, IvMLogInfo cohortLogInfo, IvMLogFiltered antiCohortLog,
			IvMLogInfo antiCohortLogInfo) {
		this.cohortLog = cohortLog;
		this.cohortLogInfo = cohortLogInfo;
		this.antiCohortLog = antiCohortLog;
		this.antiCohortLogInfo = antiCohortLogInfo;
	}

	public IvMLogFiltered getCohortLog() {
		return cohortLog;
	}

	public IvMLogFiltered getAntiCohortLog() {
		return antiCohortLog;
	}

	public IvMLogInfo getCohortLogInfo() {
		return cohortLogInfo;
	}

	public IvMLogInfo getAntiCohortLogInfo() {
		return antiCohortLogInfo;
	}

	//==differences==
	private Pair<ProcessDifferences, ProcessDifferencesPareto> processDifferences;

	public Pair<ProcessDifferences, ProcessDifferencesPareto> getProcessDifferences() {
		return processDifferences;
	}

	public void setProcessDifferences(Pair<ProcessDifferences, ProcessDifferencesPareto> processDifferences) {
		this.processDifferences = processDifferences;
	}
}
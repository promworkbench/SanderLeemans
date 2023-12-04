package org.processmining.cohortanalysis.plugins;

import javax.swing.JComponent;

import org.deckfour.xes.model.XLog;
import org.processmining.cohortanalysis.visualisation.CohortsController;
import org.processmining.cohortanalysis.visualisation.CohortsLauncher;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginCategory;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

public class CohortsPlugin {

	@Plugin(name = "Interactive cohort analysis", level = PluginLevel.PeerReviewed, returnLabels = {
			"Cohort analysis launcher" }, returnTypes = { CohortsLauncher.class }, parameterLabels = {
					"Event log" }, userAccessible = true, categories = { PluginCategory.Discovery,
							PluginCategory.Analytics, PluginCategory.ConformanceChecking }, help = ".")
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Mine, dialog", requiredParameterLabels = { 0 })
	public CohortsLauncher mineGuiProcessTree(PluginContext context, XLog log) {
		return CohortsLauncher.fromLog(log);
	}

	@Plugin(name = "Cohort analysis visualisation", returnLabels = { "Visualisation" }, returnTypes = {
			JComponent.class }, parameterLabels = { "Cohort analysis launcher",
					"canceller" }, userAccessible = true, level = PluginLevel.Regular)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Cohort analysis visualisation", requiredParameterLabels = { 0, 1 })
	public JComponent fancy(PluginContext context, CohortsLauncher launcher, ProMCanceller canceller) {
		return new CohortsController(context, launcher.getLog(), canceller).getPanel();
	}
}
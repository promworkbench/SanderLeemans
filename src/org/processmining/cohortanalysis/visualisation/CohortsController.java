package org.processmining.cohortanalysis.visualisation;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.deckfour.xes.model.XLog;
import org.processmining.cohortanalysis.chain.Cl01GatherAttributes;
import org.processmining.cohortanalysis.chain.Cl05Mine;
import org.processmining.cohortanalysis.chain.Cl07Align;
import org.processmining.cohortanalysis.chain.Cl08LayoutAlignment;
import org.processmining.cohortanalysis.chain.Cl08LayoutAlignmentAnti;
import org.processmining.cohortanalysis.chain.Cl18ApplyCohort;
import org.processmining.cohortanalysis.chain.Cl19ProcessDifferences;
import org.processmining.cohortanalysis.chain.CohortsConfiguration;
import org.processmining.cohortanalysis.cohort.Cohort;
import org.processmining.cohortanalysis.cohort.Cohorts;
import org.processmining.cohortanalysis.feature.Features2String;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.plugins.InductiveMiner.Function;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.visualisation.listeners.GraphChangedListener;
import org.processmining.plugins.inductiveVisualMiner.InductiveVisualMinerSelectionColourer;
import org.processmining.plugins.inductiveVisualMiner.alignedLogVisualisation.data.AlignedLogVisualisationData;
import org.processmining.plugins.inductiveVisualMiner.chain.Cl02SortEvents;
import org.processmining.plugins.inductiveVisualMiner.chain.Cl03MakeLog;
import org.processmining.plugins.inductiveVisualMiner.chain.Cl18DataAnalysisCohort;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainImplNonBlocking;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChainLinkGuiAbstract;
import org.processmining.plugins.inductiveVisualMiner.chain.DataState;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObject;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMObjectValues;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogInfo;
import org.processmining.plugins.inductiveVisualMiner.mode.ModeRelativePaths;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.miners.DfgMiner;
import org.processmining.plugins.inductiveVisualMiner.visualisation.ProcessTreeVisualisationInfo;
import org.processmining.plugins.inductiveVisualMiner.visualisation.ProcessTreeVisualisationParameters;

import com.kitfox.svg.SVGDiagram;

/**
 * Constructs and visualises a directly follows graph.
 * 
 * @author sander
 *
 */
public class CohortsController {

	private final CohortsPanel panel;
	private final DataChainImplNonBlocking<CohortsConfiguration, CohortsPanel> chain;
	private final Cl02SortEvents<CohortsConfiguration> sortEvents;

	public CohortsController(PluginContext context, XLog log, ProMCanceller canceller) {
		DataState state = new DataState();
		CohortsConfiguration configuration = new CohortsConfiguration();
		panel = new CohortsPanel();

		chain = new DataChainImplNonBlocking<>(state,

				canceller, context.getExecutor(), configuration, getPanel());

		initGui(canceller, configuration);

		//set up the controller view
		chain.setOnChange(new Runnable() {
			public void run() {
				getPanel().getControllerView().pushCompleteChainLinks(chain);
			}
		});

		//state.setGraphUserSettings(panel.getCohortGraph().getUserSettings());
		//state.getGraphUserSettings().setDirection(GraphDirection.leftRight);

		//chain
		chain.register(new Cl01GatherAttributes());

		{
			sortEvents = new Cl02SortEvents<>();
			chain.register(sortEvents);
			sortEvents.setOnIllogicalTimeStamps(new Function<Object, Boolean>() {
				public Boolean call(Object input) throws Exception {
					String[] options = new String[] { "Continue with neither animation nor performance",
							"Reorder events" };
					int n = JOptionPane.showOptionDialog(panel,
							"The event log contains illogical time stamps,\n i.e. some time stamps contradict the order of events.\n\nInductive visual Miner can reorder the events and discover a new model.\nWould you like to do that?", //message
							"Illogical Time Stamps", //title
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, //do not use a custom Icon
							options, //the titles of buttons
							options[0]); //default button title
					if (n == 1) {
						//the user requested to reorder the events
						return true;
					}
					return false;
				}
			});
		}

		chain.register(new Cl03MakeLog<>());
		chain.register(new Cl05Mine<>());
		chain.register(new Cl07Align());
		chain.register(new Cl08LayoutAlignment());
		chain.register(new Cl08LayoutAlignmentAnti());
		chain.register(new Cl18DataAnalysisCohort<CohortsConfiguration>());
		chain.register(new Cl18ApplyCohort());
		chain.register(new Cl19ProcessDifferences());

		//start the chain
		chain.setFixedObject(IvMObject.input_log, log);
		chain.setFixedObject(IvMObject.selected_noise_threshold, 0.8);
		chain.setFixedObject(IvMObject.selected_miner, new DfgMiner());
		chain.setFixedObject(IvMObject.selected_visualisation_mode, new ModeRelativePaths());
		chain.setFixedObject(IvMObject.selected_cohort_analysis_enabled, true);
	}

	protected void initGui(final ProMCanceller canceller, CohortsConfiguration configuration) {
		initGuiGraph();
		initGuiCohorts();
		initGuiDifferences();

		//listen to ctrl c to show the controller view
		{
			panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
					.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK), "showControllerView"); // - key
			panel.getActionMap().put("showControllerView", new AbstractAction() {
				private static final long serialVersionUID = 1727407514105090094L;

				public void actionPerformed(ActionEvent arg0) {
					panel.getControllerView().setVisible(true);
					chain.getOnChange().run();
				}

			});
		}

		//graph direction changed
		panel.getCohortGraph().addGraphChangedListener(new GraphChangedListener() {

			public void graphChanged(GraphChangedReason reason, Object newState) {
				chain.setObject(IvMObject.selected_graph_user_settings, panel.getCohortGraph().getUserSettings());
			}
		});
		panel.getAntiCohortGraph().addGraphChangedListener(new GraphChangedListener() {
			public void graphChanged(GraphChangedReason reason, Object newState) {
				chain.setObject(IvMObject.selected_graph_user_settings, panel.getAntiCohortGraph().getUserSettings());
			}
		});
		panel.getCohortGraph().getUserSettings().setDirection(GraphDirection.leftRight);
		chain.setObject(IvMObject.selected_graph_user_settings, panel.getCohortGraph().getUserSettings());
	}

	protected void initGuiCohorts() {
		//cohorts to gui
		chain.register(new DataChainLinkGuiAbstract<CohortsConfiguration, CohortsPanel>() {

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { IvMObject.data_analysis_cohort };
			}

			public void updateGui(CohortsPanel panel, IvMObjectValues inputs) throws Exception {
				Cohorts cohorts = inputs.get(IvMObject.data_analysis_cohort);
				panel.setCohorts(cohorts);
				panel.getCohortsList().getSelectionModel().setSelectionInterval(0, 0);

				//state.setSelectedCohort(state.getCohorts().get(panel.getCohortsList().getSelectionModel().getAnchorSelectionIndex()));
				panel.getCohortsList().repaint();
			}

			public void invalidate(CohortsPanel panel) {
				panel.setCohorts(null);
				panel.getCohortsList().repaint();
			}

			public String getName() {
				return "cohorts";
			}

		});

		//respond to cohort selection
		panel.getCohortsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					//update selection
					try {
						Cohorts cohorts = chain.getObjectValues(IvMObject.data_analysis_cohort).get()
								.get(IvMObject.data_analysis_cohort);
						Cohort cohort = cohorts
								.get(panel.getCohortsList().getSelectionModel().getAnchorSelectionIndex());
						panel.getCohortLabel().setText(
								"<html>Cohort (" + Features2String.toString(cohort.getFeatures()) + ")</html>");
						chain.setObject(CohortsObject.selected_cohort, cohort);
						panel.repaint();
					} catch (InterruptedException e1) {
					}

				}
			}
		});

		//cohorts applied to graphs
		chain.register(new DataChainLinkGuiAbstract<CohortsConfiguration, CohortsPanel>() {

			public String getName() {
				return "cohorts to graphs";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { IvMObject.model, CohortsObject.graph_svg_aligned,
						CohortsObject.aligned_log_info_filtered, CohortsObject.graph_visualisation_info_aligned,
						CohortsObject.graph_svg_aligned_anti, CohortsObject.aligned_log_info_filtered_anti,
						CohortsObject.graph_visualisation_info_aligned_anti };
			}

			public void updateGui(CohortsPanel panel, IvMObjectValues inputs) throws Exception {
				IvMModel model = inputs.get(IvMObject.model);

				ProcessTreeVisualisationParameters visualisationParameters = new ModeRelativePaths()
						.getVisualisationParametersWithAlignments(null);
				{
					IvMLogInfo logInfoCohort = inputs.get(CohortsObject.aligned_log_info_filtered);
					ProcessTreeVisualisationInfo visualisationInfoCohort = inputs
							.get(CohortsObject.graph_visualisation_info_aligned);
					SVGDiagram svg = inputs.get(CohortsObject.graph_svg_aligned);

					AlignedLogVisualisationData cohortVisualisationData;
					{
						IvMObjectValues repack = new IvMObjectValues().//
						s(IvMObject.model, model).//
						s(IvMObject.aligned_log_info_filtered, logInfoCohort);
						cohortVisualisationData = new ModeRelativePaths().getVisualisationData(repack);
					}
					InductiveVisualMinerSelectionColourer.colourHighlighting(svg, visualisationInfoCohort, model,
							cohortVisualisationData, visualisationParameters, null);

					panel.getCohortGraph().repaint();
				}

				{
					IvMLogInfo logInfoCohort = inputs.get(CohortsObject.aligned_log_info_filtered_anti);
					ProcessTreeVisualisationInfo visualisationInfoCohort = inputs
							.get(CohortsObject.graph_visualisation_info_aligned_anti);
					SVGDiagram svg = inputs.get(CohortsObject.graph_svg_aligned_anti);

					AlignedLogVisualisationData cohortVisualisationData;
					{
						IvMObjectValues repack = new IvMObjectValues().//
						s(IvMObject.model, model).//
						s(IvMObject.aligned_log_info_filtered, logInfoCohort);
						cohortVisualisationData = new ModeRelativePaths().getVisualisationData(repack);
					}
					InductiveVisualMinerSelectionColourer.colourHighlighting(svg, visualisationInfoCohort, model,
							cohortVisualisationData, visualisationParameters, null);

					panel.getAntiCohortGraph().repaint();
				}
			}

			public void invalidate(CohortsPanel panel) {

			}
		});
	}

	protected void initGuiGraph() {
		//update layout
		chain.register(new DataChainLinkGuiAbstract<CohortsConfiguration, CohortsPanel>() {
			public String getName() {
				return "model dot";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { CohortsObject.graph_dot_aligned, CohortsObject.graph_svg_aligned };
			}

			public void updateGui(CohortsPanel panel, IvMObjectValues inputs) throws Exception {
				Dot dot = inputs.get(CohortsObject.graph_dot_aligned);
				SVGDiagram svg = inputs.get(CohortsObject.graph_svg_aligned);
				panel.getCohortGraph().changeDot(dot, svg, true);
			}

			public void invalidate(CohortsPanel panel) {
				//here, we could put the graph on blank, but that is annoying
			}
		});

		chain.register(new DataChainLinkGuiAbstract<CohortsConfiguration, CohortsPanel>() {
			public String getName() {
				return "model dot anti";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { CohortsObject.graph_dot_aligned_anti,
						CohortsObject.graph_svg_aligned_anti };
			}

			public void updateGui(CohortsPanel panel, IvMObjectValues inputs) throws Exception {
				Dot dot = inputs.get(CohortsObject.graph_dot_aligned_anti);
				SVGDiagram svg = inputs.get(CohortsObject.graph_svg_aligned_anti);
				panel.getAntiCohortGraph().changeDot(dot, svg, true);
			}

			public void invalidate(CohortsPanel panel) {
				//here, we could put the graph on blank, but that is annoying
			}
		});
	}

	protected void initGuiDifferences() {
		chain.register(new DataChainLinkGuiAbstract<CohortsConfiguration, CohortsPanel>() {
			public String getName() {
				return "diff to gui";
			}

			public IvMObject<?>[] createInputObjects() {
				return new IvMObject<?>[] { CohortsObject.differences, CohortsObject.differences_pareto };
			}

			public void updateGui(CohortsPanel panel, IvMObjectValues inputs) throws Exception {
				ProcessDifferences differences = inputs.get(CohortsObject.differences);
				ProcessDifferencesPareto differences_pareto = inputs.get(CohortsObject.differences_pareto);
				panel.setProcessDifferences(differences, differences_pareto);
				panel.getProcessDifferencesPareto().repaint();
			}

			public void invalidate(CohortsPanel panel) {
				panel.setProcessDifferences(null, null);
				panel.getProcessDifferencesPareto().repaint();
			}
		});

		//respond to difference selection
		panel.getProcessDifferences().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					int internalIndex = panel.getProcessDifferences().getSelectionModel().getAnchorSelectionIndex();
					if (internalIndex >= 0) {
						try {
							ProcessDifferences differences = chain.getObjectValues(CohortsObject.differences).get()
									.get(CohortsObject.differences);
							int selectedIndex = differences.row2index(internalIndex);
							panel.getProcessDifferencesPareto().setSelectedIndex(selectedIndex);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
					panel.repaint();
				}
			}
		});
	}

	public CohortsPanel getPanel() {
		return panel;
	}
}
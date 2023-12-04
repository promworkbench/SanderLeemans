package org.processmining.cohortanalysis.visualisation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.processmining.cohortanalysis.cohort.Cohorts;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.visualisation.DotPanel;
import org.processmining.plugins.graphviz.visualisation.listeners.ImageTransformationChangedListener;
import org.processmining.plugins.inductiveVisualMiner.chain.DataState;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.OnOffPanel;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.ControllerView;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMDecorator;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMDecoratorDefault;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMDecoratorI;

public class CohortsPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1995681192925928915L;

	private DotPanel cohortGraph;
	private DotPanel antiCohortGraph;
	private CohortsListPanel cohortsListPanel;
	private OnOffPanel<JScrollPane> cohortsListOnOff;
	private ProcessDifferencesPanel processDifferencesPanel;
	private ProcessDifferencesParetoPanel processDifferencesParetoPanel;
	private OnOffPanel<ResizableSplitPane> processDifferencesPanelOnOff;
	private JLabel cohortLabel;

	private final ControllerView<DataState> controllerView;

	private final static IvMDecoratorI decorator = new IvMDecoratorDefault();

	public CohortsPanel() {
		setLayout(new BorderLayout());

		JPanel modelsPanel = new JPanel();
		{
			modelsPanel.setLayout(new BoxLayout(modelsPanel, BoxLayout.PAGE_AXIS));

			cohortLabel = new JLabel("Cohort") {
				private static final long serialVersionUID = 1L;

				@Override
				public Dimension getMaximumSize() {
					Dimension d = super.getMaximumSize();
					return new Dimension(CohortsPanel.this.getWidth(), d.height);
				}
			};
			IvMDecorator.decorate(cohortLabel);
			cohortLabel.setOpaque(true);
			cohortLabel.setBackground(IvMDecorator.backGroundColour1);
			cohortLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			modelsPanel.add(cohortLabel);

			Dot cohortDot = new Dot();
			cohortDot.addNode("cohort");
			cohortGraph = new DotPanel(cohortDot);
			modelsPanel.add(cohortGraph);

			JLabel antiCohortLabel = new JLabel("Anti-cohort (other traces)") {
				private static final long serialVersionUID = 1L;

				@Override
				public Dimension getMaximumSize() {
					Dimension d = super.getMaximumSize();
					return new Dimension(CohortsPanel.this.getWidth(), d.height);
				}
			};
			IvMDecorator.decorate(antiCohortLabel);
			antiCohortLabel.setOpaque(true);
			antiCohortLabel.setBackground(IvMDecorator.backGroundColour1);
			antiCohortLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			modelsPanel.add(antiCohortLabel);

			Dot antiCohortDot = new Dot();
			antiCohortDot.addNode("anti-cohort");
			antiCohortGraph = new DotPanel(antiCohortDot);
			modelsPanel.add(antiCohortGraph);

			//synchronise the graphs
			cohortGraph.setImageTransformationChangedListener(new ImageTransformationChangedListener() {
				public void imageTransformationChanged(AffineTransform image2user, AffineTransform user2image) {
					antiCohortGraph.setTransformation(image2user, user2image);
					antiCohortGraph.repaint();
				}
			});
			antiCohortGraph.setImageTransformationChangedListener(new ImageTransformationChangedListener() {
				public void imageTransformationChanged(AffineTransform image2user, AffineTransform user2image) {
					cohortGraph.setTransformation(image2user, user2image);
					cohortGraph.repaint();
				}
			});
		}

		//cohorts list panel
		{
			cohortsListPanel = new CohortsListPanel();
			JScrollPane scrollPane = new JScrollPane(cohortsListPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.getViewport().setOpaque(false);
			scrollPane.setOpaque(false);
			cohortsListOnOff = new OnOffPanel<>(decorator, scrollPane);
			cohortsListOnOff.setOffMessage("Computing cohorts..");
			cohortsListOnOff.off();
		}

		//differences panel
		{
			{
				processDifferencesParetoPanel = new ProcessDifferencesParetoPanel();
			}

			JScrollPane processDifferencesPanelScrollPane;
			{
				processDifferencesPanel = new ProcessDifferencesPanel();
				processDifferencesPanelScrollPane = new JScrollPane(processDifferencesPanel,
						JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				processDifferencesPanelScrollPane.setOpaque(false);
				processDifferencesPanelScrollPane.getViewport().setOpaque(false);
			}

			ResizableSplitPane differencesSplitPane = new ResizableSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					processDifferencesPanelScrollPane, processDifferencesParetoPanel, this, 0.5);
			differencesSplitPane.setOpaque(false);
			flattenJSplitPane(differencesSplitPane);

			processDifferencesPanelOnOff = new OnOffPanel<>(decorator, differencesSplitPane);
			processDifferencesPanelOnOff.setOffMessage("Computing differences..");
			processDifferencesPanelOnOff.off();
		}

		//split pane between cohorts panel and graphs
		{
			ResizableSplitPane splitPane1 = new ResizableSplitPane(JSplitPane.HORIZONTAL_SPLIT, cohortsListOnOff,
					processDifferencesPanelOnOff, this, 0.33);
			flattenJSplitPane(splitPane1);
			ResizableSplitPane splitPane2 = new ResizableSplitPane(JSplitPane.VERTICAL_SPLIT, splitPane1, modelsPanel,
					this, 0.3);
			flattenJSplitPane(splitPane2);
			add(splitPane2, BorderLayout.CENTER);
		}

		//controller view
		{
			controllerView = new ControllerView<>(this);
			cohortGraph.getHelperControlsShortcuts().add("ctrl c");
			antiCohortGraph.getHelperControlsExplanations().add("show controller");
		}
	}

	public void removeNotify() {
		super.removeNotify();
		controllerView.setVisible(false);
	}

	public CohortsListPanel getCohortsList() {
		return cohortsListPanel;
	}

	public void setCohorts(Cohorts cohorts) {
		cohortsListPanel.setData(cohorts);
		cohortsListOnOff.set(cohorts != null);
	}

	public void setProcessDifferences(ProcessDifferences differences, ProcessDifferencesPareto differences_pareto) {
		if (differences != null && differences_pareto != null) {
			processDifferencesPanel.setData(differences);
			processDifferencesParetoPanel.setData(differences_pareto);
		}
		processDifferencesPanelOnOff.set(differences != null && differences_pareto != null);
	}

	public ProcessDifferencesPanel getProcessDifferences() {
		return processDifferencesPanel;
	}

	public ProcessDifferencesParetoPanel getProcessDifferencesPareto() {
		return processDifferencesParetoPanel;
	}

	public DotPanel getCohortGraph() {
		return cohortGraph;
	}

	public JLabel getCohortLabel() {
		return cohortLabel;
	}

	public DotPanel getAntiCohortGraph() {
		return antiCohortGraph;
	}

	/**
	 * Code from
	 * https://stackoverflow.com/questions/36067690/how-do-you-get-jsplitpane-to-keep-the-same-proportional-location-if-the-user-has
	 *
	 */
	public class ResizableSplitPane extends JSplitPane {

		private static final long serialVersionUID = 2458076807784153762L;

		//
		// instance variables
		//

		private boolean painted;

		private double defaultDividerLocation;

		private double dividerProportionalLocation;

		private int currentDividerLocation;

		private Component first;

		private Component second;

		private boolean dividerPositionCaptured = false;

		//
		// constructors
		//

		public ResizableSplitPane(int splitType, Component first, Component second, Component parent) {
			this(splitType, first, second, parent, 0.5);
		}

		public ResizableSplitPane(int splitType, Component first, Component second, Component parent,
				double defaultDividerLocation) {
			super(splitType, first, second);
			this.defaultDividerLocation = defaultDividerLocation;
			this.dividerProportionalLocation = defaultDividerLocation;
			this.setResizeWeight(defaultDividerLocation);
			this.first = first;
			this.second = second;
			parent.addComponentListener(new DividerLocator());
			second.addComponentListener(new DividerMovedByUserComponentAdapter());
		}

		//
		// trivial getters and setters
		//

		public double getDefaultDividerLocation() {
			return defaultDividerLocation;
		}

		public void setDefaultDividerLocation(double defaultDividerLocation) {
			this.defaultDividerLocation = defaultDividerLocation;
		}

		//
		// implementation
		//

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			if (painted == false) {
				painted = true;
				this.setDividerLocation(dividerProportionalLocation);
				this.currentDividerLocation = this.getDividerLocation();
			}
		}

		private class DividerLocator extends ComponentAdapter {
			@Override
			public void componentResized(ComponentEvent e) {
				setDividerLocation(dividerProportionalLocation);
				currentDividerLocation = getDividerLocation();
			}
		}

		private class DividerMovedByUserComponentAdapter extends ComponentAdapter {
			@Override
			public void componentResized(ComponentEvent e) {
				System.out.println("RESIZED: " + dividerPositionCaptured);
				int newDividerLocation = getDividerLocation();
				boolean dividerWasMovedByUser = newDividerLocation != currentDividerLocation;
				System.out.println(
						currentDividerLocation + "\t" + newDividerLocation + "\t" + dividerProportionalLocation);
				if (dividerPositionCaptured == false || dividerWasMovedByUser == true) {
					dividerPositionCaptured = true;
					painted = false;
					if (getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
						dividerProportionalLocation = (double) first.getWidth()
								/ (double) (first.getWidth() + second.getWidth());
					} else {
						dividerProportionalLocation = (double) first.getHeight()
								/ (double) (first.getHeight() + second.getHeight());

					}
					System.out.println(dividerProportionalLocation);
				}
			}
		}
	}

	/**
	 * Makes a split pane invisible. Only contained components are shown.
	 * Source:
	 * https://stackoverflow.com/questions/12799640/why-does-jsplitpane-add-a-border-to-my-components-and-how-do-i-stop-it
	 *
	 * @param splitPane
	 */
	public static void flattenJSplitPane(JSplitPane splitPane) {
		splitPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		BasicSplitPaneUI flatDividerSplitPaneUI = new BasicSplitPaneUI() {
			@Override
			public BasicSplitPaneDivider createDefaultDivider() {
				return new BasicSplitPaneDivider(this) {
					private static final long serialVersionUID = 3677107739793289059L;

					@Override
					public void setBorder(Border b) {
					}
				};
			}
		};
		splitPane.setUI(flatDividerSplitPaneUI);
		splitPane.setBorder(null);
	}

	public ControllerView<DataState> getControllerView() {
		return controllerView;
	}
}
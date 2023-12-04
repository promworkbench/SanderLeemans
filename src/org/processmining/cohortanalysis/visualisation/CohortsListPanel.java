package org.processmining.cohortanalysis.visualisation;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import org.processmining.cohortanalysis.cohort.Cohort;
import org.processmining.cohortanalysis.cohort.Cohorts;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DataAnalysisTable;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DataAnalysisTableCellRenderer;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DisplayType;

public class CohortsListPanel extends JTable {

	private static final long serialVersionUID = -8234094868198612004L;

	private AbstractTableModel model;
	private Cohorts cohorts;

	public CohortsListPanel() {
		setOpaque(false);
		setShowGrid(false);
		setRowMargin(DataAnalysisTable.rowMargin);
		setRowHeight(DataAnalysisTable.rowHeight);
		getColumnModel().setColumnMargin(DataAnalysisTable.columnMargin);
		setDefaultRenderer(Object.class, new DataAnalysisTableCellRenderer());

		model = new AbstractTableModel() {

			private static final long serialVersionUID = 6373287725995933319L;

			public String getColumnName(int column) {
				switch (column) {
					case 0 :
						return "Cohort attribute";
					case 1 :
						return "value range";
					case 2 :
						return "number of traces";
					default :
						return "distance with rest of log";
				}
			}

			public int getColumnCount() {
				if (cohorts == null) {
					return 0;
				}
				return 4;
			}

			public int getRowCount() {
				if (cohorts == null) {
					return 0;
				}
				return cohorts.size();
			}

			public Object getValueAt(int row, int column) {
				if (cohorts == null) {
					return "";
				}

				Cohort cohort = cohorts.get(row);

				switch (column) {
					case 0 :
						return cohort.getFeatures().iterator().next().getDescriptionField();
					case 1 :
						return DisplayType.html(cohort.getFeatures().iterator().next().getDescriptionSelector());
					case 2 :
						return DisplayType.numericUnpadded(cohort.getSize());
					default :
						return DisplayType.numeric(cohort.getDistance());
				}
			}
		};

		//set selections: one row only
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		setModel(model);
	}

	public void setData(Cohorts cohorts) {
		this.cohorts = cohorts;
		model.fireTableStructureChanged();
	}

}
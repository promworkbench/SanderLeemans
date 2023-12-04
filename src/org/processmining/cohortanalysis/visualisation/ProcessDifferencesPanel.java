package org.processmining.cohortanalysis.visualisation;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DataAnalysisTable;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DataAnalysisTableCellRenderer;

public class ProcessDifferencesPanel extends JTable {

	private static final long serialVersionUID = 7343907651107042880L;

	private AbstractTableModel model;
	private ProcessDifferences processDifferences;

	public ProcessDifferencesPanel() {
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
						return "From";
					case 1 :
						return "to";
					case 2 :
						return "cohort";
					default :
						return "anti-cohort";
				}
			}

			public int getColumnCount() {
				if (processDifferences == null) {
					return 0;
				}
				return 4;
			}

			public int getRowCount() {
				if (processDifferences == null) {
					return 0;
				}
				return processDifferences.size();
			}

			public Object getValueAt(int row, int column) {
				if (processDifferences == null) {
					return "";
				}

				switch (column) {
					case 0 :
						return processDifferences.getFrom(row);
					case 1 :
						return processDifferences.getTo(row);
					case 2 :
						return processDifferences.getCohort(row);
					default :
						return processDifferences.getAntiCohort(row);
				}
			}
		};

		//set selections: none
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(true);

		setModel(model);
	}

	public void setData(ProcessDifferences processDifferences) {
		this.processDifferences = processDifferences;
		model.fireTableStructureChanged();
	}
}
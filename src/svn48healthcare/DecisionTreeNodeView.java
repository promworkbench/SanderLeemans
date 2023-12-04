package svn48healthcare;

import java.awt.BorderLayout;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DataRow;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DisplayType;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.traceattributes.CorrelationDensityPlot;

public class DecisionTreeNodeView {

	public static final int rowHeight = 18;
	public static final int rowHeightImage = CorrelationDensityPlot.getHeight();

	private final DecisionTreeNode node;

	private SoftReference<State> stateRef;

	private class State {
		private final JPanel panel;
		private final JTable table;
		private final DecisionTableModel tableModel;
		private boolean tableFilledBasic = false;
		private boolean tableFilled = false;
		private boolean tableFilledCostModel = false;

		public State() {
			tableModel = new DecisionTableModel();
			table = new JTable(tableModel);

			panel = new JPanel();
			panel.setLayout(new BorderLayout());
			panel.add(new JLabel(node.getName()), BorderLayout.PAGE_START);

			//create table
			{
				table.setDefaultRenderer(Object.class, new DecisionTableCellRenderer());

				fillTableBasic(this);

				JScrollPane scrollPane = new JScrollPane(table);
				table.setFillsViewportHeight(true);
				panel.add(scrollPane, BorderLayout.CENTER);
			}
		}
	}

	public DecisionTreeNodeView(DecisionTreeNode node) {
		this.node = node;
		stateRef = new SoftReference<>(new State());
	}

	private State getState() {
		State state = stateRef.get();
		if (state == null) {
			state = new State();
			stateRef = new SoftReference<DecisionTreeNodeView.State>(state);
		}
		return state;
	}

	private static void tableChanged(JTable table, DecisionTableModel tableModel) {
		tableModel.fireTableStructureChanged();
		setSorting(table);
		setRowHeights(table, tableModel);
	}

	public static void setSorting(JTable table) {
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
		table.setRowSorter(sorter);
		List<RowSorter.SortKey> sortKeys = new ArrayList<>(25);
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
	}

	public void fillTableBasic(State state) {
		if (!state.tableFilledBasic) {
			state.tableFilledBasic = true;
			state.tableModel
					.addRow(new DataRow<Object>(DisplayType.numeric(node.getOccurrences()), "log", "number of traces"));

			tableChanged(state.table, state.tableModel);
		}
	}

	public void fillTable() {
		State state = getState();

		if (!state.tableFilled) {
			state.tableFilled = true;

			List<DataRow<Object>> data = TraceAttributes.createAttributeData(node.getTraces(),
					IvMCanceller.neverCancel);
			data = addPropertyToAll("log", data);
			state.tableModel.addRows(data);

			tableChanged(state.table, state.tableModel);
		}
	}

	public void fillTableCostModel(CostModel costModel) {
		State state = getState();

		if (!state.tableFilledCostModel) {
			state.tableFilledCostModel = true;

			state.tableModel.addRows(costModel.getModelRepresentation(node.getTraces()));

			tableChanged(state.table, state.tableModel);
		}
	}

	private static void setRowHeights(JTable table, DecisionTableModel tableModel) {
		for (int modelRow = 0; modelRow < tableModel.getRowCount(); modelRow++) {
			int viewRow = table.convertRowIndexToView(modelRow);
			if (viewRow >= 0) {
				boolean hasImage = false;
				for (int column = 0; column < tableModel.getColumnCount(); column++) {
					Object value = tableModel.getValueAt(modelRow, column);
					if (value instanceof DisplayType && ((DisplayType) value).getType() == DisplayType.Type.image
							&& ((DisplayType.Image) value).getImage() != null) {
						hasImage = true;
						break;
					}
				}

				if (hasImage) {
					table.setRowHeight(table.convertRowIndexToView(modelRow), rowHeightImage);
				} else {
					table.setRowHeight(table.convertRowIndexToView(modelRow), rowHeight);
				}
			}
		}
	}

	public List<DataRow<Object>> addPropertyToAll(String property, List<DataRow<Object>> list) {
		List<DataRow<Object>> result = new ArrayList<>();
		for (DataRow<Object> row : list) {
			result.add(new DataRow<Object>(property, row));
		}
		return result;
	}

	public JPanel getPanel() {
		return getState().panel;
	}
}

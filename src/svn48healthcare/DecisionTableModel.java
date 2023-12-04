package svn48healthcare;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DataRow;

public class DecisionTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -8087680994607139925L;

	private int maxNameColumns = 0;
	private int maxValueColumns = 0;
	private List<DataRow<Object>> rows = new ArrayList<>();

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public int getColumnCount() {
		return maxNameColumns + maxValueColumns;
	}

	@Override
	public String getColumnName(int column) {
		if (column == 0) {
			return "Source";
		} else if (column < maxNameColumns) {
			return "property";
		} else {
			return "value";
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		DataRow<Object> row = rows.get(rowIndex);

		if (columnIndex < row.getNumberOfNames()) {
			//name
			return row.getName(columnIndex);
		} else if (columnIndex < maxNameColumns) {
			//name empty
			return "";
		} else if (columnIndex < row.getNumberOfValues() + maxNameColumns) {
			//value
			return row.getValue(columnIndex - maxNameColumns);
		} else {
			//empty value
			return "";
		}
	}

	public void addRows(Iterable<DataRow<Object>> rows) {
		for (DataRow<Object> row : rows) {
			addRow(row);
		}
	}

	public void addRow(DataRow<Object> row) {
		maxNameColumns = Math.max(maxNameColumns, row.getNumberOfNames());
		maxValueColumns = Math.max(maxValueColumns, row.getNumberOfValues());
		rows.add(row);
	}

}
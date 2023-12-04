package thesis.cooStem;

import java.util.Arrays;

public class TruthTable {

	private final int n;
	private final boolean[] values;

	public TruthTable(int n) {
		this.n = n;
		values = new boolean[(int) Math.pow(2, n)];
	}

	public int getRow(boolean... keys) {
		int rowNr = 0;
		for (int i = 0; i < n; i++) {
			if (keys[i]) {
				rowNr += Math.pow(2, i);
			}
		}
		return rowNr;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(values);
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TruthTable other = (TruthTable) obj;
		if (!Arrays.equals(values, other.values))
			return false;
		return true;
	}

	public boolean[] getKeys(int row) {
		boolean[] result = new boolean[n];
		for (int i = n - 1; i >= 0; i--) {
			if (row >= Math.pow(2, i)) {
				result[i] = true;
				row -= Math.pow(2, i);
			}
		}
		return result;
	}

	public void set(boolean value, boolean... keys) {
		assert (keys.length == n);
		values[getRow(keys)] = value;
	}

	public boolean get(boolean... keys) {
		assert (keys.length == n);
		return values[getRow(keys)];
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		for (int row = 0; row < Math.pow(2, n); row++) {
			boolean[] keys = getKeys(row);
			for (int j = 0; j < n; j++) {
				if (keys[j]) {
					result.append("true  ");
				} else {
					result.append("false ");
				}
			}
			result.append("- ");
			result.append(values[row]);
			result.append("\n");
		}
		return result.toString();
	}

	public int getNumberOfRows() {
		return n;
	}
}

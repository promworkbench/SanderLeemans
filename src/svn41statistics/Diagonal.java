package svn41statistics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public abstract class Diagonal {
	
	public long getTimesMax(List<Integer> as, List<Integer> bs) throws IOException {
		long result = Long.MIN_VALUE;
	
		int size = getMax(as, bs);

		if (size <= 0) {
			return Long.MIN_VALUE;
		}
		
		for (int iA = 0; iA < size; iA++) {
			for (int iB = 0; iB < size; iB++) {
				BufferedReader reader = new BufferedReader(new FileReader(getFile(as.get(iA), bs.get(iB))));
				String line = reader.readLine();
				reader.close();
				result = Math.max(result, Long.valueOf(line));
			}
		}
		
		return result;
	}

	public double[][] getMeasures(List<Integer> as, List<Integer> bs) throws IOException {
		int size = getMax(as, bs);

		if (size <= 0) {
			return null;
		}

		double[][] result = new double[size][size];

		for (int iA = 0; iA < size; iA++) {
			for (int iB = 0; iB < size; iB++) {
				BufferedReader reader = new BufferedReader(new FileReader(getFile(as.get(iA), bs.get(iB))));
				String line = reader.readLine();
				reader.close();
				result[iA][iB] = Double.valueOf(line);
			}
		}

		return result;
	}

	public int getMax(List<Integer> as, List<Integer> bs) throws IOException {
		int size = Math.min(as.size(), bs.size());

		if (!isDone(getFile(as.get(0), bs.get(0)))) {
			return 0;
		}

		for (int i = 1; i < size; i++) {

			for (int iA = 0; iA < i; iA++) {
				int a = as.get(iA);
				int b = bs.get(i - 1);
				File file = getFile(a, b);
				if (!isDone(file)) {
					return i - 1;
				}
			}

			for (int iB = 0; iB < i; iB++) {
				int a = as.get(i - 1);
				int b = bs.get(iB);
				File file = getFile(a, b);
				if (!isDone(file)) {
					return i - 1;
				}
			}

		}

		return size;
	}

	public abstract File getFile(int a, int b);

	public static boolean isAttempted(File file) {
		return file.exists();
	}

	public static boolean isDone(File file) throws IOException {
		if (file.exists()) {
			return !isError(file);
		} else {
			return false;
		}
	}

	public static boolean isError(File file) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(file));
		String firstLine = r.readLine();
		r.close();
		return firstLine.startsWith("error");
	}
}
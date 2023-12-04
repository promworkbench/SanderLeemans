package thesis.helperClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ParetoOptimality<T> {
	private final List<double[]> measures;
	private final List<T> identifiers;

	public ParetoOptimality() {
		measures = new ArrayList<>();
		identifiers = new ArrayList<>();
	}

	/**
	 * Add a measure to the array and update pareto-optimality.
	 * 
	 * @param m
	 */
	public void add(T identifier, double... m) {
		Iterator<T> it2 = this.identifiers.iterator();
		for (Iterator<double[]> it = this.measures.iterator(); it.hasNext();) {
			it2.next();
			double[] m1 = it.next();
			if (m.length != m1.length) {
				throw new RuntimeException("wrong number of measures");
			}

			switch (compare(m, m1)) {
				case equal :
					/*
					 * If both are equal, by the invariant this is
					 * pareto-optimal and no other value from the array becomes
					 * pareto-suboptimal. Just add it.
					 */
					measures.add(m);
					identifiers.add(identifier);
					return;
				case incomparable :
					/*
					 * If the measures are incomparable, we have no new
					 * information.
					 */
					continue;
				case larger :
					/*
					 * The new measure is larger than the existing one. Remove
					 * the existing one and add the new one (which will be done
					 * once at the end).
					 */
					it.remove();
					it2.remove();
					break;
				case smaller :
					/*
					 * If the new measure is smaller, then the new measure
					 * cannot be pareto-larger than anything in the array by the
					 * invariant. Do not add and return.
					 */
					return;
				default :
					break;
			}
		}
		measures.add(m);
		identifiers.add(identifier);
	}

	public List<T> getParetoOptimalIdentifiers() {
		return Collections.unmodifiableList(identifiers);
	}
	
	public List<double[]> getParetoOptimalMeasures() {
		return Collections.unmodifiableList(measures);
	}

	public enum ParetoComparability {
		incomparable, larger, smaller, equal
	}

	/**
	 * Compare two measures. Return their pareto-comparability.
	 * 
	 * @param a
	 * @param b
	 * @return -1 =
	 */
	public ParetoComparability compare(double[] a, double[] b) {
		if (a.length != b.length) {
			throw new RuntimeException("wrong number of measures");
		}

		boolean allBigger = true;
		boolean allSmaller = true;
		for (int i = 0; i < a.length; i++) {
			if (a[i] < b[i]) {
				allBigger = false;
			} else if (a[i] > b[i]) {
				allSmaller = false;
			}
		}

		if (allBigger && allSmaller) {
			return ParetoComparability.equal;
		}
		if (allBigger && !allSmaller) {
			return ParetoComparability.larger;
		}
		if (!allBigger && allSmaller) {
			return ParetoComparability.smaller;
		}
		return ParetoComparability.incomparable;
	}
}

package org.processmining.cohortanalysis.visualisation;

import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DisplayType;

public interface ProcessDifferencesPareto {

	public double getMinAbsoluteDifference();

	public double getMaxAbsoluteDifference();

	public double getMinRelativeDifference();

	public double getMaxRelativeDifference();

	public int size();

	public double getAbsoluteDifference(int index);

	public double getRelativeDifference(int index);

	public DisplayType getFrom(int index);

	public DisplayType getTo(int index);

}
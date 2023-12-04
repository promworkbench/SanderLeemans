package org.processmining.cohortanalysis.visualisation;

import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DisplayType;

public interface ProcessDifferences {

	int size();

	public DisplayType getFrom(int row);

	public DisplayType getTo(int row);

	public DisplayType getCohort(int row);

	public 	DisplayType getAntiCohort(int row);
	
	public int row2index(int row);

}
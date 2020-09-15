package org.processmining.lousycohortanalysis.visualisation;

import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DisplayType;

public interface ProcessDifferences {

	int size();

	DisplayType getFrom(int row);

	DisplayType getTo(int row);

	DisplayType getCohort(int row);

	DisplayType getAntiCohort(int row);

}
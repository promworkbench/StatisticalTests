package org.processmining.statisticaltests.helperclasses;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;

/**
 * Avoid ProM-caching and threading issues of XEventClasses, as we need to
 * change these objects. This simply provides a copy.
 * 
 * @author sleemans
 *
 */
public class SEventClasses extends XEventClasses {

	public SEventClasses(XEventClasses classes) {
		super(classes.getClassifier());

		for (XEventClass a : classes.getClasses()) {
			register(a.getId());
		}
	}
}
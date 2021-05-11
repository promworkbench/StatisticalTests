package org.processmining.statisticaltests.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.SpringLayout;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.ClassifierChooser;
import org.processmining.statisticaltests.loglogunknownprocesstest.LogLogUnknownProcessTestParametersAbstract;
import org.processmining.statisticaltests.loglogunknownprocesstest.LogLogUnknownProcessTestParametersDefault;

public class LogLogUnknownProcessTestDialog extends StatisticalTestDialog<LogLogUnknownProcessTestParametersAbstract> {

	private static final long serialVersionUID = -1097548788215530060L;

	private LogLogUnknownProcessTestParametersAbstract parameters;

	private ClassifierChooser classifiersA;
	private ClassifierChooser classifiersB;

	public LogLogUnknownProcessTestDialog(XLog logA, XLog logB) {

		//first group
		{
			JLabel classifierLabel = factory.createLabel("Event classifier first log");
			add(classifierLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, classifierLabel, rowHeight, SpringLayout.VERTICAL_CENTER,
					alpha);
			layout.putConstraint(SpringLayout.EAST, classifierLabel, leftColumnWidth, SpringLayout.WEST, this);

			classifiersA = new ClassifierChooser(logA);
			add(classifiersA);
			layout.putConstraint(SpringLayout.WEST, classifiersA, columnMargin, SpringLayout.EAST, classifierLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, classifiersA, 0, SpringLayout.VERTICAL_CENTER,
					classifierLabel);
		}

		//second group
		{
			JLabel classifierLabel = factory.createLabel("Event classifier second log");
			add(classifierLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, classifierLabel, rowHeight, SpringLayout.VERTICAL_CENTER,
					classifiersA);
			layout.putConstraint(SpringLayout.EAST, classifierLabel, leftColumnWidth, SpringLayout.WEST, this);

			classifiersB = new ClassifierChooser(logB);
			add(classifiersB);
			layout.putConstraint(SpringLayout.WEST, classifiersB, columnMargin, SpringLayout.EAST, classifierLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, classifiersB, 0, SpringLayout.VERTICAL_CENTER,
					classifierLabel);
		}

		classifiersA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getParameters().setClassifierA(classifiersA.getSelectedClassifier());
			}
		});
		getParameters().setClassifierA(classifiersA.getSelectedClassifier());

		classifiersB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getParameters().setClassifierA(classifiersB.getSelectedClassifier());
			}
		});
		getParameters().setClassifierA(classifiersB.getSelectedClassifier());
	}

	public LogLogUnknownProcessTestParametersAbstract getParameters() {
		if (parameters == null) {
			parameters = new LogLogUnknownProcessTestParametersDefault();
		}
		return parameters;
	}
}
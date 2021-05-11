package org.processmining.statisticaltests.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.SpringLayout;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.ClassifierChooser;
import org.processmining.statisticaltests.modelmodellogtest.ModelModelLogTestParametersAbstract;
import org.processmining.statisticaltests.modelmodellogtest.ModelModelLogTestParametersDefault;

public class ModelModelLogTestDialog extends StatisticalTestDialog<ModelModelLogTestParametersAbstract> {

	private static final long serialVersionUID = -1097548788215530060L;

	private ModelModelLogTestParametersAbstract parameters;

	private ClassifierChooser classifiers;

	public ModelModelLogTestDialog(XLog log) {

		//first group
		{
			JLabel classifierLabel = factory.createLabel("Event classifier first log");
			add(classifierLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, classifierLabel, rowHeight, SpringLayout.VERTICAL_CENTER,
					alpha);
			layout.putConstraint(SpringLayout.EAST, classifierLabel, leftColumnWidth, SpringLayout.WEST, this);

			classifiers = new ClassifierChooser(log);
			add(classifiers);
			layout.putConstraint(SpringLayout.WEST, classifiers, columnMargin, SpringLayout.EAST, classifierLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, classifiers, 0, SpringLayout.VERTICAL_CENTER,
					classifierLabel);
		}

		classifiers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getParameters().setClassifier(classifiers.getSelectedClassifier());
			}
		});
		getParameters().setClassifier(classifiers.getSelectedClassifier());
	}

	public ModelModelLogTestParametersAbstract getParameters() {
		if (parameters == null) {
			parameters = new ModelModelLogTestParametersDefault();
		}
		return parameters;
	}
}
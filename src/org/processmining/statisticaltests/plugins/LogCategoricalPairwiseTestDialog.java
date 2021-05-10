package org.processmining.statisticaltests.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.function.Predicate;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.ClassifierChooser;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.plugins.inductiveminer2.attributes.AttributesInfoImpl;
import org.processmining.statisticaltests.logcategoricaltest.LogCategoricalPairwiseTestParametersDefault;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class LogCategoricalPairwiseTestDialog
		extends StatisticalTestDialog<LogCategoricalPairwiseTestParametersDefault> {

	private static final long serialVersionUID = -5059650442590207291L;

	private ClassifierChooser classifiers;
	private JComboBox<Attribute> attributesc;

	private LogCategoricalPairwiseTestParametersDefault parameters;

	@SuppressWarnings("unchecked")
	public LogCategoricalPairwiseTestDialog(XLog log) {

		//first group
		{
			JLabel classifierLabel = factory.createLabel("Event classifier");
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

		//second group
		{
			JLabel classifierLabel = factory.createLabel("Attribute");
			add(classifierLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, classifierLabel, rowHeight, SpringLayout.VERTICAL_CENTER,
					classifiers);
			layout.putConstraint(SpringLayout.EAST, classifierLabel, leftColumnWidth, SpringLayout.WEST, this);

			Collection<Attribute> attributes = new AttributesInfoImpl(log).getTraceAttributes();
			attributes.removeIf(new Predicate<Attribute>() {
				public boolean test(Attribute t) {
					return !t.isLiteral();
				}
			});

			attributesc = SlickerFactory.instance().createComboBox(attributes.toArray());
			add(attributesc);
			layout.putConstraint(SpringLayout.WEST, attributesc, columnMargin, SpringLayout.EAST, classifierLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, attributesc, 0, SpringLayout.VERTICAL_CENTER,
					classifierLabel);
		}

		//set up the controller
		getParameters().setAttribute((Attribute) attributesc.getSelectedItem());
		attributesc.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				getParameters().setAttribute((Attribute) attributesc.getSelectedItem());
			}
		});

		classifiers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getParameters().setClassifierA(classifiers.getSelectedClassifier());
				getParameters().setClassifierB(classifiers.getSelectedClassifier());
			}
		});
		getParameters().setClassifierA(classifiers.getSelectedClassifier());
		getParameters().setClassifierB(classifiers.getSelectedClassifier());
	}

	public LogCategoricalPairwiseTestParametersDefault getParameters() {
		if (parameters == null) {
			parameters = new LogCategoricalPairwiseTestParametersDefault(null);
		}
		return parameters;
	}

}

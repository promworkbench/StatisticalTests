package org.processmining.statisticaltests.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.ClassifierChooser;
import org.processmining.statisticaltests.loglogunknownprocesstest.LogLogUnknownProcessTestParametersAbstract;
import org.processmining.statisticaltests.loglogunknownprocesstest.LogLogUnknownProcessTestParametersDefault;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class LogLogUnknownProcessTestDialog extends JPanel {

	private static final long serialVersionUID = -1097548788215530060L;

	public static final int leftColumnWidth = 200;
	public static final int columnMargin = 20;
	public static final int rowHeight = 40;

	private LogLogUnknownProcessTestParametersAbstract parameters;

	private final SpringLayout layout;
	private ClassifierChooser classifiersA;
	private ClassifierChooser classifiersB;

	public LogLogUnknownProcessTestDialog(XLog logA, XLog logB) {
		SlickerFactory factory = SlickerFactory.instance();

		layout = new SpringLayout();
		setLayout(layout);

		//first group
		{
			JLabel classifierLabel = factory.createLabel("Event classifier first log");
			add(classifierLabel);
			layout.putConstraint(SpringLayout.NORTH, classifierLabel, 5, SpringLayout.NORTH, this);
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

		//set up the controller
		parameters = new LogLogUnknownProcessTestParametersDefault();

		classifiersA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				parameters.setClassifierA(classifiersA.getSelectedClassifier());
			}
		});
		parameters.setClassifierA(classifiersA.getSelectedClassifier());

		classifiersB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				parameters.setClassifierA(classifiersB.getSelectedClassifier());
			}
		});
		parameters.setClassifierA(classifiersB.getSelectedClassifier());
	}

	public LogLogUnknownProcessTestParametersAbstract getParameters() {
		return parameters;
	}
}
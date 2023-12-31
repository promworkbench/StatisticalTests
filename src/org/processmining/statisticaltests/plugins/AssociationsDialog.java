package org.processmining.statisticaltests.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.ClassifierChooser;
import org.processmining.statisticaltests.association.AssociationsParameters;
import org.processmining.statisticaltests.association.AssociationsParametersAbstract;
import org.processmining.statisticaltests.association.AssociationsParametersDefault;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class AssociationsDialog extends JPanel {

	private static final long serialVersionUID = -1097548788215530060L;

	public static final int leftColumnWidth = 200;
	public static final int columnMargin = 20;
	public static final int rowHeight = 40;

	private AssociationsParametersAbstract parameters = new AssociationsParametersDefault();

	private final SpringLayout layout;
	private ClassifierChooser classifiers;

	public AssociationsDialog(XLog log) {
		SlickerFactory factory = SlickerFactory.instance();

		layout = new SpringLayout();
		setLayout(layout);

		//first group
		{
			JLabel classifierLabel = factory.createLabel("Event classifier");
			add(classifierLabel);
			layout.putConstraint(SpringLayout.NORTH, classifierLabel, 5, SpringLayout.NORTH, this);
			layout.putConstraint(SpringLayout.EAST, classifierLabel, leftColumnWidth, SpringLayout.WEST, this);

			classifiers = new ClassifierChooser(log);
			add(classifiers);
			layout.putConstraint(SpringLayout.WEST, classifiers, columnMargin, SpringLayout.EAST, classifierLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, classifiers, 0, SpringLayout.VERTICAL_CENTER,
					classifierLabel);
		}

		//set up the controller
		classifiers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				parameters.setClassifier(classifiers.getSelectedClassifier());
			}
		});
		parameters.setClassifier(classifiers.getSelectedClassifier());

	}

	public AssociationsParameters getParameters() {
		return parameters;
	}
}
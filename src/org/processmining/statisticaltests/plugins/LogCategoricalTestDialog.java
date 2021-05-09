package org.processmining.statisticaltests.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.function.Predicate;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.ClassifierChooser;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.plugins.inductiveminer2.attributes.AttributesInfoImpl;
import org.processmining.statisticaltests.CategoricalComparisonParameters;
import org.processmining.statisticaltests.CategoricalComparisonParametersAbstract;
import org.processmining.statisticaltests.CategoricalComparisonParametersDefault;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class LogCategoricalTestDialog extends JPanel {

	private static final long serialVersionUID = -1097548788215530060L;

	public static final int leftColumnWidth = 200;
	public static final int columnMargin = 20;
	public static final int rowHeight = 40;

	private CategoricalComparisonParametersAbstract parameters;

	private final SpringLayout layout;
	private ClassifierChooser classifiers;
	private JComboBox<Attribute> attributesc;

	@SuppressWarnings("unchecked")
	public LogCategoricalTestDialog(XLog log) {
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
		parameters = new CategoricalComparisonParametersDefault((Attribute) attributesc.getSelectedItem());
		attributesc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				parameters.setAttribute((Attribute) attributesc.getSelectedItem());
			}
		});

		classifiers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				parameters.setClassifier(classifiers.getSelectedClassifier());
			}
		});
		parameters.setClassifier(classifiers.getSelectedClassifier());
	}

	public CategoricalComparisonParameters getParameters() {
		return parameters;
	}
}
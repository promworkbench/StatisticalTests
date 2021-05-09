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
import org.processmining.statisticaltests.logcategoricaltest.LogCategoricalTestParameters;
import org.processmining.statisticaltests.logcategoricaltest.LogCategoricalTestParametersAbstract;
import org.processmining.statisticaltests.logcategoricaltest.LogCategoricalTestParametersDefault;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class LogCategoricalTestDialog extends JPanel {

	private static final long serialVersionUID = -1097548788215530060L;

	public static final int leftColumnWidth = 200;
	public static final int columnMargin = 20;
	public static final int rowHeight = 40;

	private LogCategoricalTestParametersAbstract parameters;

	private final SpringLayout layout;
	private ClassifierChooser classifiers;
	private JComboBox<Attribute> attributesc;
	private JComboBox<String> comparec;
	private String[] compares = new String[] { "one-against-all", "pairwise" };

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

		//third group
		{
			JLabel classifierLabel = factory.createLabel("Compare");
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, classifierLabel, rowHeight, SpringLayout.VERTICAL_CENTER,
					attributesc);
			layout.putConstraint(SpringLayout.EAST, classifierLabel, leftColumnWidth, SpringLayout.WEST, this);

			comparec = SlickerFactory.instance().createComboBox(compares);
			add(comparec);
			layout.putConstraint(SpringLayout.WEST, comparec, columnMargin, SpringLayout.EAST, classifierLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, comparec, 0, SpringLayout.VERTICAL_CENTER,
					classifierLabel);
		}

		//set up the controller
		parameters = new LogCategoricalTestParametersDefault((Attribute) attributesc.getSelectedItem());
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

	public LogCategoricalTestParameters getParameters() {
		return parameters;
	}

	public String getCompare() {
		return (String) comparec.getSelectedItem();
	}
}
package org.processmining.statisticaltests.plugins;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.correlation.Associations;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public class AssociationsVisualiserPlugin {
	@Plugin(name = "Associations visualisation", returnLabels = { "Associations visualisation" }, returnTypes = {
			JComponent.class }, parameterLabels = { "Associations" }, userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Visualise process tree", requiredParameterLabels = { 0 })
	public JComponent fancy(PluginContext context, Associations associations) throws UnknownTreeNodeException {
		String[] columnNames = new String[] { "attribute", "type", "correlation", "plot" };

		Object[][] data = new Object[associations.getNumberOfAttributes()][4];

		for (int att = 0; att < associations.getNumberOfAttributes(); att++) {
			Attribute attribute = associations.getAttribute(att);
			data[att][0] = attribute.getName();
			if (attribute.isNumeric()) {
				data[att][1] = "numeric";
			} else if (attribute.isLiteral()) {
				data[att][1] = "literal";
			} else if (attribute.isTime()) {
				data[att][1] = "time";
			}
			if (associations.getCorrelation(att) != -Double.MAX_VALUE) {
				data[att][2] = associations.getCorrelation(att);
			} else {
				data[att][2] = "coming soon";
			}
			data[att][4] = "coming soon";
		}

		JTable table = new JTable(data, columnNames) {
			private static final long serialVersionUID = 2893739120646013376L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setColumnSelectionAllowed(false);
		table.setCellSelectionEnabled(false);
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		return scrollPane;
	}
}

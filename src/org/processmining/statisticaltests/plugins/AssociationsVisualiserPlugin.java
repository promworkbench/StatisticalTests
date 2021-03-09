package org.processmining.statisticaltests.plugins;

import java.awt.Component;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.statisticaltests.association.Associations;

public class AssociationsVisualiserPlugin {
	@Plugin(name = "Associations visualisation", returnLabels = { "Associations visualisation" }, returnTypes = {
			JComponent.class }, parameterLabels = { "Associations" }, userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Visualise process tree", requiredParameterLabels = { 0 })
	public JComponent fancy(PluginContext context, Associations associations) throws UnknownTreeNodeException {
		String[] columnNames = new String[] { "attribute", "type", "association/correlation", "plot" };

		Object[][] data = new Object[associations.getNumberOfAttributes()][4];

		int height = 0;
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
			if (associations.getAssociation(att) != -Double.MAX_VALUE) {
				data[att][2] = associations.getAssociation(att);
			} else {
				data[att][2] = "n/a";
			}
			if (associations.getImage(att) != null) {
				data[att][3] = associations.getImage(att);
				height = Math.max(height, associations.getImage(att).getIconHeight());
			} else {
				data[att][3] = "";
			}
		}

		DefaultTableModel model = new DefaultTableModel(data, columnNames) {
			private static final long serialVersionUID = -2755705459511414388L;

			public Class<?> getColumnClass(int column) {
				if (column == 3) {
					return Icon.class;
				} else if (column == 2) {
					return Double.class;
				} else {
					return String.class;
				}
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		JTable table = new JTable(model);
		table.setRowHeight(height);
		table.setAutoCreateRowSorter(true);
		table.setFillsViewportHeight(true);

		// Tell the table what to use to render our column of doubles
		final DecimalFormat formatter = new DecimalFormat("#.000000");
		table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 6360996816579695382L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				// First format the cell value as required
				if (value instanceof Double) {
					if (Double.isNaN((double) value)) {
						value = "NaN";
					} else {
						value = formatter.format(value);
					}
				}
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		});

		//sort on startup
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
		table.setRowSorter(sorter);
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		int columnIndexToSort = 2;
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();

		JScrollPane scrollPane = new JScrollPane(table);
		return scrollPane;
	}
}

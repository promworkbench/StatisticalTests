package org.processmining.statisticaltests.plugins;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.Quadruple;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.statisticaltests.CategoricalComparisonResult;

public class LogCategoricalPairwiseVisualiserPlugin {
	@Plugin(name = "Associations visualisation", returnLabels = { "Associations visualisation" }, returnTypes = {
			JComponent.class }, parameterLabels = { "Categorical comparison" }, userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Visualise process tree", requiredParameterLabels = { 0 })
	public JComponent fancy(PluginContext context, CategoricalComparisonResult comparison)
			throws UnknownTreeNodeException {
		String[] columnNames = new String[] { comparison.getAttribute().getName(), "p-value", "result" };

		Object[][] data = new Object[comparison.get().size()][3];

		int i = 0;
		for (Quadruple<Double, Boolean, String, String> t : comparison.get()) {

			data[i][0] = t.getC();
			if (t.getA() != -Double.MAX_VALUE) {
				data[i][1] = t.getA();
			} else {
				data[i][1] = "n/a";
			}
			if (t.getB()) {
				//reject hypothesis
				data[i][2] = "Reject hypothesis that sub-log was derived from the same underlying process as the remainder of the log."
						+ //
						"\n" + //
						"Traces with " + comparison.getAttribute().getName() + " = `" + t.getC()
						+ "` are from a statistically significantly different underlying process than the other traces of the log, with α = "
						+ comparison.getAlpha() + ".";
			} else {
				//do not reject hypothesis
				data[i][2] = "Not enough evidence to reject hypothesis that sub-log was derived from the same underlying process as the remainder of the log.";
			}

			i++;
		}

		DefaultTableModel model = new DefaultTableModel(data, columnNames) {
			private static final long serialVersionUID = -2755705459511414388L;

			public Class<?> getColumnClass(int column) {
				if (column == 1) {
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
		table.setAutoCreateRowSorter(true);
		table.setFillsViewportHeight(true);

		// Tell the table what to use to render our column of doubles
		final DecimalFormat formatter = new DecimalFormat("#.000000");
		table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
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
		table.getColumnModel().getColumn(2).setCellRenderer(new WordWrapCellRenderer());

		//sort on startup
		//		TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
		//		table.setRowSorter(sorter);
		//		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		//		int columnIndexToSort = 2;
		//		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
		//		sorter.setSortKeys(sortKeys);
		//		sorter.sort();

		JScrollPane scrollPane = new JScrollPane(table);
		return scrollPane;
	}

	static class WordWrapCellRenderer extends JTextArea implements TableCellRenderer {
		private static final long serialVersionUID = -4593852262900715388L;

		WordWrapCellRenderer() {
			setLineWrap(true);
			setWrapStyleWord(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			setText(value.toString());
			setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
			if (table.getRowHeight(row) != getPreferredSize().height) {
				table.setRowHeight(row, getPreferredSize().height);
			}
			return this;
		}
	}
}

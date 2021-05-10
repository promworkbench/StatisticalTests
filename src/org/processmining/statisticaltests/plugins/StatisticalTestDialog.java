package org.processmining.statisticaltests.plugins;

import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.processmining.statisticaltests.StatisticalTestParametersAbstract;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public abstract class StatisticalTestDialog<P extends StatisticalTestParametersAbstract> extends JPanel {

	private static final long serialVersionUID = -8702068256031465807L;

	public static final int leftColumnWidth = 200;
	public static final int columnMargin = 20;
	public static final int rowHeight = 40;

	protected final SpringLayout layout;
	protected SlickerFactory factory = SlickerFactory.instance();

	protected final JSlider alpha;

	public StatisticalTestDialog() {
		layout = new SpringLayout();
		setLayout(layout);

		//first group: alpha
		{
			JLabel alphaLabel = factory.createLabel("Statistical confidence (α)");
			add(alphaLabel);
			layout.putConstraint(SpringLayout.NORTH, alphaLabel, 5, SpringLayout.NORTH, this);
			layout.putConstraint(SpringLayout.EAST, alphaLabel, leftColumnWidth, SpringLayout.WEST, this);

			alpha = factory.createSlider(SwingConstants.HORIZONTAL);
			alpha.setMinimum(0);
			alpha.setMaximum(100);
			add(alpha);
			layout.putConstraint(SpringLayout.WEST, alpha, columnMargin, SpringLayout.EAST, alphaLabel);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, alpha, 0, SpringLayout.VERTICAL_CENTER, alphaLabel);

			final JLabel alphaValue = factory.createLabel("--");
			add(alphaValue);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, alphaValue, 0, SpringLayout.VERTICAL_CENTER, alphaLabel);
			layout.putConstraint(SpringLayout.WEST, alphaValue, 5, SpringLayout.EAST, alpha);

			DecimalFormat formatter = new java.text.DecimalFormat("0.00");
			alpha.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					getParameters().setAlpha(alpha.getValue() / 100.0);
					alphaValue.setText(formatter.format(alpha.getValue() / 100.0));
				}
			});
			alpha.setValue((int) Math.round(getParameters().getAlpha() * 100));
			alphaValue.setText(formatter.format(alpha.getValue() / 100.0));
		}
	}

	protected abstract P getParameters();
}

package org.processmining.cohortanalysis.visualisation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DisplayType;
import org.processmining.plugins.inductiveVisualMiner.dataanalysis.DisplayType.Type;

public class ProcessDifferencesParetoPanel extends JPanel {

	private static final long serialVersionUID = 2551557353855744026L;

	public final static int blockSize = 10;
	public final static int margin = 10;

	private ProcessDifferencesPareto processDifferences;

	public ProcessDifferencesParetoPanel() {
		setOpaque(false);

		setToolTipText("blabla");
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		double minx = processDifferences.getMinAbsoluteDifference();
		double maxx = processDifferences.getMaxAbsoluteDifference();
		double miny = processDifferences.getMinRelativeDifference();
		double maxy = processDifferences.getMaxRelativeDifference();

		for (int index = 0; index < processDifferences.size(); index++) {
			int x = getX(minx, maxx, index);
			int y = getY(miny, maxy, index);
			g.setColor(Color.blue);
			g.fillRect(x - blockSize / 2, y - blockSize / 2, blockSize, blockSize);
			g.drawLine(x - blockSize / 2, y, x + blockSize / 2, y);
			g.drawLine(x, y - 5, x, y + 5);
		}
	}

	public int getX(double minx, double maxx, int index) {
		return margin + (int) (((processDifferences.getAbsoluteDifference(index) - minx) / (maxx - minx))
				* (this.getWidth() - 2 * margin));
	}

	public int getY(double miny, double maxy, int index) {
		int y = (this.getHeight() - 2 * margin)
				- (margin + ((int) (((processDifferences.getRelativeDifference(index) - miny) / (maxy - miny))
						* (this.getHeight() - 2 * margin))));
		return y;
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		Point p = event.getPoint();
		double x = p.getX();
		double y = p.getY();

		double minx = processDifferences.getMinAbsoluteDifference();
		double maxx = processDifferences.getMaxAbsoluteDifference();
		double miny = processDifferences.getMinRelativeDifference();
		double maxy = processDifferences.getMaxRelativeDifference();

		ArrayList<String> result = new ArrayList<>();

		for (int index = 0; index < processDifferences.size(); index++) {
			int indexX = getX(minx, maxx, index);
			int indexY = getY(miny, maxy, indexX);

			if (Math.abs(x - indexX) <= blockSize / 2 && Math.abs(y - indexY) <= blockSize / 2) {
				DisplayType from = processDifferences.getFrom(index);
				DisplayType to = processDifferences.getTo(index);
				String r = from + " ";
				if (to.getType() != Type.NA) {
					r += "-> " + to + " ";
				}
				result.add(r + "&#916;abs " + processDifferences.getAbsoluteDifference(index) + " &#916;rel "
						+ processDifferences.getRelativeDifference(index));
			}
		}
		if (result.isEmpty()) {
			return null;
		}

		return "<html>" + String.join("<br>", result) + "</html>";
	}

	public void setData(ProcessDifferencesPareto processDifferences) {
		this.processDifferences = processDifferences;
		repaint();
	}
}
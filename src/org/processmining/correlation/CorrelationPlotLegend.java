package org.processmining.correlation;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.processmining.plugins.graphviz.colourMaps.ColourMap;
import org.processmining.plugins.graphviz.colourMaps.ColourMapViridis;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.decoration.IvMDecorator;

public class CorrelationPlotLegend {

	private int width = 500;
	private int plotHeight = 12;
	private int marginPlotText = 12;

	private ColourMap colourMap = new ColourMapViridis();
	private Color backgroundFigure = Color.white;
	private Color backgroundPlot = Color.white;

	public BufferedImage create(String name, String min, String max) {
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = image.createGraphics();

		//set background
		{
			g.setBackground(backgroundFigure);
			g.clearRect(0, 0, getWidth(), getHeight());
		}

		//draw legend
		for (int x = 0; x < getWidth(); x++) {
			Color colour = colourMap.colour(x, 0, getWidth());
			for (int y = 0; y < getPlotHeight(); y++) {
				image.setRGB(x, y, colour.getRGB());
			}
		}

		g.setColor(IvMDecorator.textColour);

		//draw min text
		{
			g.drawString(min, 0, getHeight() - 2);
		}

		//draw max text
		{
			Font font = IvMDecorator.font;
			g.setFont(font);
			FontMetrics metrics = g.getFontMetrics(font);
			int width = metrics.stringWidth(max);

			g.drawString(max, getWidth() - width, getHeight() - 2);
		}

		//draw name text
		{
			Font font = IvMDecorator.font;
			g.setFont(font);
			FontMetrics metrics = g.getFontMetrics(font);
			int width = metrics.stringWidth(name);

			g.drawString(name, getWidth() / 2 - width / 2, getHeight() - 2);
		}

		return image;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return getPlotHeight() + getMarginPlotText();
	}

	public ColourMap getColourMap() {
		return colourMap;
	}

	public void setColourMap(ColourMap colourMap) {
		this.colourMap = colourMap;
	}

	public Color getBackgroundFigure() {
		return backgroundFigure;
	}

	public void setBackgroundFigure(Color backgroundFigure) {
		this.backgroundFigure = backgroundFigure;
	}

	public Color getBackgroundPlot() {
		return backgroundPlot;
	}

	public void setBackgroundPlot(Color backgroundPlot) {
		this.backgroundPlot = backgroundPlot;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getPlotHeight() {
		return plotHeight;
	}

	public void setPlotHeight(int plotHeight) {
		this.plotHeight = plotHeight;
	}

	public int getMarginPlotText() {
		return marginPlotText;
	}

	public void setMarginPlotText(int marginPlotText) {
		this.marginPlotText = marginPlotText;
	}
}
package org.processmining.correlation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;

public class Associations {

	private final List<Attribute> attributes;
	private final double[] associations;
	private final Icon[] images;

	public Associations(Collection<Attribute> attributes) {
		this.attributes = new ArrayList<>(attributes);
		associations = new double[attributes.size()];
		images = new Icon[attributes.size()];
		Arrays.fill(associations, -Double.MAX_VALUE);
	}

	public void setAssociation(Attribute attribute, Pair<Double, BufferedImage> pair) {
		associations[attributes.indexOf(attribute)] = pair.getA();
		if (pair.getB() != null) {
			images[attributes.indexOf(attribute)] = new ImageIcon(pair.getB());
		}
	}

	public int getNumberOfAttributes() {
		return attributes.size();
	}

	public Attribute getAttribute(int att) {
		return attributes.get(att);
	}

	public double getAssociation(int att) {
		return associations[att];
	}

	public Icon getImage(int att) {
		return images[att];
	}
}
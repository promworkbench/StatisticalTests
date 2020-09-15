package org.processmining.cohortanalysis.chain;

import java.util.Collection;
import java.util.Iterator;

import org.deckfour.xes.model.XLog;
import org.processmining.cohortanalysis.visualisation.LousyCohortsState;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveminer2.attributes.Attribute;
import org.processmining.plugins.inductiveminer2.attributes.AttributesInfo;
import org.processmining.plugins.inductiveminer2.attributes.AttributesInfoImpl;

public class Cl01GatherAttributes
		extends LousyCohortsChainLink<XLog, Triple<AttributesInfo, AttributeClassifier, AttributeClassifier[]>> {

	protected XLog generateInput(LousyCohortsState state) {
		return state.getLog();
	}

	protected Triple<AttributesInfo, AttributeClassifier, AttributeClassifier[]> executeLink(XLog input,
			IvMCanceller canceller) throws Exception {
		AttributesInfo info = new AttributesInfoImpl(input);
		Collection<Attribute> attributes = info.getEventAttributes();

		String[] names = new String[attributes.size()];
		Iterator<Attribute> it = attributes.iterator();
		for (int i = 0; i < names.length; i++) {
			names[i] = it.next().getName();
		}
		Pair<AttributeClassifier[], AttributeClassifier> p = AttributeClassifiers.getAttributeClassifiers(input, names,
				true);
		AttributeClassifier[] attributeClassifiers = p.getA();
		AttributeClassifier firstClassifier = p.getB();

		return Triple.of(info, firstClassifier, attributeClassifiers);
	}

	protected void processResult(Triple<AttributesInfo, AttributeClassifier, AttributeClassifier[]> result,
			LousyCohortsState state) {
		state.setAttributesInfo(result.getA(), result.getB(), result.getC());
		state.setClassifier(AttributeClassifiers.constructClassifier(result.getB()));
	}

	protected void invalidateResult(LousyCohortsState state) {
		state.setAttributesInfo(null, null, null);
		state.setClassifier(null);
	}

	public String getName() {
		return "gather attributes";
	}

	public String getStatusBusyMessage() {
		return "Gathering attributes..";
	}

}

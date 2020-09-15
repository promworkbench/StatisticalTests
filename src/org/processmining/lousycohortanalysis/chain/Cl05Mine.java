package org.processmining.lousycohortanalysis.chain;

import org.deckfour.xes.model.XLog;
import org.processmining.lousycohortanalysis.visualisation.LousyCohortsState;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.InductiveMiner.mining.IMLogInfo;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLog;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLogImpl;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.performance.XEventPerformanceClassifier;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.VisualMinerParameters;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.VisualMinerWrapper;
import org.processmining.plugins.inductiveVisualMiner.visualMinerWrapper.miners.DfgMiner;

public class Cl05Mine
		extends LousyCohortsChainLink<Triple<XLog, XEventPerformanceClassifier, VisualMinerParameters>, IvMModel> {

	protected Triple<XLog, XEventPerformanceClassifier, VisualMinerParameters> generateInput(LousyCohortsState state) {
		VisualMinerParameters minerParameters = new VisualMinerParameters(state.getPaths());
		return Triple.of(state.getLog(), state.getPerformanceClassifier(), minerParameters);
	}

	protected IvMModel executeLink(Triple<XLog, XEventPerformanceClassifier, VisualMinerParameters> input,
			IvMCanceller canceller) throws UnknownTreeNodeException {
		XLog xLog = input.getA();
		XEventPerformanceClassifier classifier = input.getB();
		VisualMinerParameters parameters = input.getC();

		VisualMinerWrapper miner = new DfgMiner();

		IMLog iLog = new IMLogImpl(xLog, classifier.getActivityClassifier(), miner.getLifeCycleClassifier());
		IMLogInfo iLogInfo = miner.getLog2logInfo().createLogInfo(iLog);
		IvMModel model = miner.mine(iLog, iLogInfo, parameters, canceller);
		
		if (model != null) {
			return model;
		} else {
			assert (canceller.isCancelled());
			return null;
		}
	}

	protected void processResult(IvMModel result, LousyCohortsState state) {
		state.setModel(result);
	}

	protected void invalidateResult(LousyCohortsState state) {
		state.setModel(null);
	}

	public String getName() {
		return "mine";
	}

	public String getStatusBusyMessage() {
		return "Mining..";
	}
}

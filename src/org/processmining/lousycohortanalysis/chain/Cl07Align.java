package org.processmining.lousycohortanalysis.chain;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.lousycohortanalysis.visualisation.LousyCohortsState;
import org.processmining.plugins.InductiveMiner.Pair;
import org.processmining.plugins.InductiveMiner.Triple;
import org.processmining.plugins.inductiveVisualMiner.alignment.AlignmentComputerImpl;
import org.processmining.plugins.inductiveVisualMiner.alignment.AlignmentPerformance;
import org.processmining.plugins.inductiveVisualMiner.chain.IvMCanceller;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.IvMModel;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogInfo;
import org.processmining.plugins.inductiveVisualMiner.ivmlog.IvMLogNotFiltered;
import org.processmining.plugins.inductiveVisualMiner.performance.XEventPerformanceClassifier;

public class Cl07Align extends
		LousyCohortsChainLink<Triple<IvMModel, XEventPerformanceClassifier, XLog>, Pair<IvMLogNotFiltered, IvMLogInfo>> {

	private static ConcurrentHashMap<Triple<IvMModel, XEventPerformanceClassifier, XLog>, SoftReference<IvMLogNotFiltered>> cache = new ConcurrentHashMap<>();

	protected Triple<IvMModel, XEventPerformanceClassifier, XLog> generateInput(LousyCohortsState state) {
		return Triple.of(state.getModel(), state.getPerformanceClassifier(), state.getLog());
	}

	protected Pair<IvMLogNotFiltered, IvMLogInfo> executeLink(Triple<IvMModel, XEventPerformanceClassifier, XLog> input,
			IvMCanceller canceller) throws Exception {
		IvMModel model = input.getA();
		XEventPerformanceClassifier performanceClassifier = input.getB();
		XLog log = input.getC();

		AlignmentComputerImpl alignmentComputer = new AlignmentComputerImpl();
		XLogInfo xLogInfo = XLogInfoFactory.createLogInfo(log, performanceClassifier.getActivityClassifier());
		XLogInfo xLogInfoPerformance = XLogInfoFactory.createLogInfo(log, performanceClassifier);

		//attempt to get the alignment from cache
		Triple<IvMModel, XEventPerformanceClassifier, XLog> cacheKey = Triple.of(model, input.getB(), input.getC());
		SoftReference<IvMLogNotFiltered> fromCacheReference = cache.get(cacheKey);
		if (fromCacheReference != null) {
			IvMLogNotFiltered fromCache = fromCacheReference.get();
			if (fromCache != null) {
				System.out.println("obtain alignment from cache");
				return Pair.of(fromCache, new IvMLogInfo(fromCache, model));
			}
		}

		IvMLogNotFiltered aLog = AlignmentPerformance.align(alignmentComputer, model, input.getB(), input.getC(),
				xLogInfo.getEventClasses(), xLogInfoPerformance.getEventClasses(), canceller);
		if (aLog == null && !canceller.isCancelled()) {
			throw new Exception("alignment failed");
		}
		if (canceller.isCancelled()) {
			return null;
		}
		IvMLogInfo logInfo = new IvMLogInfo(aLog, model);

		//cache the alignment
		cache.put(cacheKey, new SoftReference<IvMLogNotFiltered>(aLog));

		return Pair.of(aLog, logInfo);
	}

	protected void processResult(Pair<IvMLogNotFiltered, IvMLogInfo> result, LousyCohortsState state) {
		state.setIvMLog(result.getA(), result.getB());
	}

	protected void invalidateResult(LousyCohortsState state) {
		state.setIvMLog(null, null);
	}

	public String getName() {
		return "align";
	}

	public String getStatusBusyMessage() {
		return "Aligning log and model..";
	}
}
package org.processmining.statisticaltests.helperclasses;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.framework.plugin.Progress;

public abstract class ConcurrentSamples<I> {

	/**
	 * Called once per thread.
	 * 
	 * @return
	 */
	protected abstract I createThreadConstants(int threadNumber);

	/**
	 * 
	 * @param input
	 * @param sampleNumber
	 * @return whether the sample was successful
	 */
	protected abstract boolean performSample(I input, int sampleNumber, ProMCanceller canceller);

	private final AtomicBoolean error = new AtomicBoolean(false);

	public ConcurrentSamples(int numberOfThreads, int numberOfSamples, ProMCanceller canceller, Progress progress)
			throws InterruptedException {
		this(numberOfThreads, numberOfSamples, 0, canceller, progress);
	}

	public ConcurrentSamples(int numberOfThreads, int numberOfSamples, int firstSampleNumber,
			final ProMCanceller canceller, final Progress progress) throws InterruptedException {
		Thread[] threads = new Thread[numberOfThreads];
		final AtomicInteger nextSampleNumber = new AtomicInteger(firstSampleNumber);

		final ProMCanceller innerCanceller = new ProMCanceller() {
			public boolean isCancelled() {
				return canceller.isCancelled() || isError();
			}
		};

		if (progress != null) {
			progress.setMinimum(firstSampleNumber);
			progress.setMaximum(numberOfSamples);
		}

		for (int thread = 0; thread < threads.length; thread++) {
			final int thread2 = thread;
			threads[thread] = new Thread(new Runnable() {
				public void run() {
					final I input = createThreadConstants(thread2);

					int sampleNumber = nextSampleNumber.getAndIncrement();
					while (sampleNumber < numberOfSamples && !error.get()) {

						if (innerCanceller.isCancelled()) {
							return;
						}

						if (!performSample(input, sampleNumber, innerCanceller)) {
							error.set(true);
							return;
						}

						sampleNumber = nextSampleNumber.getAndIncrement();

						if (progress != null) {
							progress.setValue(Math.min(progress.getMaximum(), sampleNumber));
						}
					}
				}
			}, "statistical test thread " + thread);
			threads[thread].start();
		}

		//join
		for (Thread thread : threads) {
			thread.join();
		}
	}

	public boolean isError() {
		return error.get();
	}
}

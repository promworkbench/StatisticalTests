package org.processmining.statisticaltests.helperclasses;

import java.util.concurrent.atomic.AtomicInteger;

import org.processmining.framework.plugin.ProMCanceller;

public abstract class ConcurrentSamples<I> {

	/**
	 * Called once per thread.
	 * 
	 * @return
	 */
	protected abstract I createThreadConstants(int threadNumber);

	protected abstract void performSample(I input, int sampleNumber);

	public ConcurrentSamples(int numberOfThreads, int numberOfSamples, ProMCanceller canceller)
			throws InterruptedException {
		this(numberOfThreads, numberOfSamples, 0, canceller);
	}

	public ConcurrentSamples(int numberOfThreads, int numberOfSamples, int firstSampleNumber, ProMCanceller canceller)
			throws InterruptedException {
		Thread[] threads = new Thread[numberOfThreads];
		final AtomicInteger nextSampleNumber = new AtomicInteger(firstSampleNumber);

		for (int thread = 0; thread < threads.length; thread++) {
			final int thread2 = thread;
			threads[thread] = new Thread(new Runnable() {
				public void run() {
					final I input = createThreadConstants(thread2);

					int sampleNumber = nextSampleNumber.getAndIncrement();
					while (sampleNumber < numberOfSamples) {

						if (canceller.isCancelled()) {
							return;
						}

						performSample(input, sampleNumber);

						sampleNumber = nextSampleNumber.getAndIncrement();
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
}

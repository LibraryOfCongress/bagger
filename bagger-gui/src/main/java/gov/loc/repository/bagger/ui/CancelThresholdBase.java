package gov.loc.repository.bagger.ui;

import java.util.concurrent.atomic.AtomicInteger;

import gov.loc.repository.bagit.Cancellable;

public class CancelThresholdBase
{
	protected Cancellable processToCancel;
	private AtomicInteger count = new AtomicInteger(0);
	private int threshold;

	public CancelThresholdBase(int threshold, Cancellable processToCancel)
	{
		this.threshold = threshold;
		this.processToCancel = processToCancel;
	}

	protected void increment()
	{
		if (this.count.incrementAndGet() >= this.threshold)
		{
			this.processToCancel.cancel();
		}
	}

}
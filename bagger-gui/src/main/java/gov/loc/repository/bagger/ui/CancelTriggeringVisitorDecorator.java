package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.BagVisitor;
import gov.loc.repository.bagit.Cancellable;

public class CancelTriggeringVisitorDecorator extends CancelThresholdBase implements BagVisitor
{
	private BagVisitor realVisitor;
	
	public CancelTriggeringVisitorDecorator(BagVisitor realVisitor, int threshold, Cancellable processToCancel)
	{
		super(threshold, processToCancel);
		this.realVisitor = realVisitor;
	}

	public void endBag()
	{
		this.increment();
		realVisitor.endBag();
	}

	public void endPayload()
	{
		this.increment();
		realVisitor.endPayload();
	}

	public void endTags()
	{
		this.increment();
		realVisitor.endTags();
	}

	public void startBag(Bag bag)
	{
		this.increment();
		realVisitor.startBag(bag);
	}

	public void startPayload()
	{
		this.increment();
		realVisitor.startPayload();
	}

	public void startTags()
	{
		this.increment();
		realVisitor.startTags();
	}

	public void visitPayload(BagFile bagFile)
	{
		this.increment();
		realVisitor.visitPayload(bagFile);
	}

	public void visitTag(BagFile bagFile)
	{
		this.increment();
		realVisitor.visitTag(bagFile);
	}
}

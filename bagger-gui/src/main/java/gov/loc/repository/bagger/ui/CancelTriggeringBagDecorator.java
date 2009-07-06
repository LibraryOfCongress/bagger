package gov.loc.repository.bagger.ui;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.BagItTxt;
import gov.loc.repository.bagit.BagVisitor;
import gov.loc.repository.bagit.Cancellable;
import gov.loc.repository.bagit.FetchTxt;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.Bag.BagConstants;
import gov.loc.repository.bagit.Bag.BagPartFactory;
import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.BagFactory.Version;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.transformer.Completer;
import gov.loc.repository.bagit.transformer.HolePuncher;
import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.loc.repository.bagit.verify.Verifier;
import gov.loc.repository.bagit.writer.Writer;

/**
 * A decorator for {@link Bag} that keeps an internal count
 * of method accesses, and calls the {@link Cancellable#cancel() cancel}
 * method on a given object when the given threshold is exceeded.  Used
 * for unit testing.
 *
 * @version $Id$
 */
public class CancelTriggeringBagDecorator extends CancelThresholdBase implements Bag
{
	private Bag realBag;
	
	public CancelTriggeringBagDecorator(Bag bag, int threshold, Cancellable processToCancel)
	{
		super(threshold, processToCancel);
		this.realBag = bag;
	}
	
	public void accept(BagVisitor visitor)
	{
		this.increment();
		realBag.accept(new CancelTriggeringVisitorDecorator(visitor, 10, processToCancel));
	}

	public void addFileAsTag(File file)
	{
		this.increment();
		realBag.addFileAsTag(file);
	}

	public void addFilesToPayload(List<File> files)
	{
		this.increment();
		realBag.addFilesToPayload(files);
	}

	public void addFileToPayload(File file)
	{
		this.increment();
		realBag.addFileToPayload(file);
	}

	public BagConstants getBagConstants()
	{
		this.increment();
		return realBag.getBagConstants();
	}

	public BagFile getBagFile(String filepath)
	{
		this.increment();
		return realBag.getBagFile(filepath);
	}

	public BagInfoTxt getBagInfoTxt()
	{
		this.increment();
		return realBag.getBagInfoTxt();
	}

	public BagItTxt getBagItTxt()
	{
		this.increment();
		return realBag.getBagItTxt();
	}

	public BagPartFactory getBagPartFactory()
	{
		this.increment();
		return realBag.getBagPartFactory();
	}

	public Map<Algorithm, String> getChecksums(String filepath)
	{
		this.increment();
		return realBag.getChecksums(filepath);
	}

	public FetchTxt getFetchTxt()
	{
		this.increment();
		return realBag.getFetchTxt();
	}

	public File getFile()
	{
		this.increment();
		return realBag.getFile();
	}

	public Format getFormat()
	{
		this.increment();
		return realBag.getFormat();
	}

	public Collection<BagFile> getPayload()
	{
		this.increment();
		return realBag.getPayload();
	}

	public Manifest getPayloadManifest(Algorithm algorithm)
	{
		this.increment();
		return realBag.getPayloadManifest(algorithm);
	}

	public List<Manifest> getPayloadManifests()
	{
		this.increment();
		return realBag.getPayloadManifests();
	}

	public Manifest getTagManifest(Algorithm algorithm)
	{
		this.increment();
		return realBag.getTagManifest(algorithm);
	}

	public List<Manifest> getTagManifests()
	{
		this.increment();
		return realBag.getTagManifests();
	}

	public Collection<BagFile> getTags()
	{
		this.increment();
		return realBag.getTags();
	}

	public Version getVersion()
	{
		this.increment();
		return realBag.getVersion();
	}

	public void loadFromPayloadFiles()
	{
		this.increment();
		realBag.loadFromPayloadFiles();
	}

	public void loadFromPayloadManifests()
	{
		this.increment();
		realBag.loadFromPayloadManifests();
	}

	public Bag makeComplete()
	{
		this.increment();
		return realBag.makeComplete();
	}

	public Bag makeComplete(Completer completer)
	{
		this.increment();
		return realBag.makeComplete(completer);
	}

	public Bag makeHoley(HolePuncher holePuncher, String baseUrl,
			boolean includePayloadDirectoryInUrl, boolean includeTags)
	{
		this.increment();
		return realBag.makeHoley(holePuncher, baseUrl,
				includePayloadDirectoryInUrl, includeTags);
	}

	public Bag makeHoley(String baseUrl, boolean includePayloadDirectoryInUrl,
			boolean includeTags)
	{
		this.increment();
		return realBag.makeHoley(baseUrl, includePayloadDirectoryInUrl,
				includeTags);
	}

	public void putBagFile(BagFile bagFile)
	{
		this.increment();
		realBag.putBagFile(bagFile);
	}

	public void putBagFiles(Collection<BagFile> bagFiles)
	{
		this.increment();
		realBag.putBagFiles(bagFiles);
	}

	public void removeBagFile(String filepath)
	{
		this.increment();
		realBag.removeBagFile(filepath);
	}

	public void removePayloadDirectory(String filepath)
	{
		this.increment();
		realBag.removePayloadDirectory(filepath);
	}

	public void setFile(File file)
	{
		this.increment();
		realBag.setFile(file);
	}

	public SimpleResult verify(Verifier verifier)
	{
		this.increment();
		return realBag.verify(verifier);
	}

	public SimpleResult verifyComplete()
	{
		this.increment();
		return realBag.verifyComplete();
	}

	public SimpleResult verifyPayloadManifests()
	{
		this.increment();
		return realBag.verifyPayloadManifests();
	}

	public SimpleResult verifyTagManifests()
	{
		this.increment();
		return realBag.verifyTagManifests();
	}

	public SimpleResult verifyValid()
	{
		this.increment();
		return realBag.verifyValid();
	}

	public Bag write(Writer writer, File file)
	{
		this.increment();
		return realBag.write(writer, file);
	}
}

package gov.loc.repository.packagemodeler.packge;


public interface ExternalFileLocation extends FileLocation {

	public enum MediaType {
		EXTERNAL_HARDDRIVE, DVD, CD
	}
	
	public abstract void setMediaType(MediaType mediaType);
	
	public abstract MediaType getMediaType();
	
	public abstract void setExternalIdentifier(ExternalIdentifier identifier);
	
	public abstract ExternalIdentifier getExternalIdentifier();

}

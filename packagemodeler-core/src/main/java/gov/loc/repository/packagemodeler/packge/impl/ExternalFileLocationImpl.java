package gov.loc.repository.packagemodeler.packge.impl;

import java.text.MessageFormat;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.packge.ExternalFileLocation;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier;

@Entity(name="ExternalFileLocation")
@Table(name = "external_filelocation", schema="core")
public class ExternalFileLocationImpl extends FileLocationImpl implements ExternalFileLocation {

	@Column(name = "base_path", nullable=true)	
	private String basePath;

	@Embedded
	private ExternalIdentifier externalIdentifier;

	@Enumerated(EnumType.STRING)
	@Column(name="media_type", nullable = true)		
	private MediaType mediaType;

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setExternalIdentifier(ExternalIdentifier externalIdentifier) {
		this.externalIdentifier = externalIdentifier;
	}

	public ExternalIdentifier getExternalIdentifier() {
		return externalIdentifier;
	}

	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}

	public MediaType getMediaType() {
		return mediaType;
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("External File Location that is identified by {0} and is part of {1}", this.externalIdentifier.toString(), this.getPackage().toString());
	}
}

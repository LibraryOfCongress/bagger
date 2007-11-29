package gov.loc.repository.packagemodeler.packge.impl;

import javax.persistence.*;

import gov.loc.repository.packagemodeler.packge.ExternalFileLocation;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier;

@Entity(name="ExternalFileLocation")
@Table(name = "external_filelocation", schema="core", uniqueConstraints={@UniqueConstraint(columnNames={"identifier_value", "identifier_type","media_type","base_path"})})
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
		
}

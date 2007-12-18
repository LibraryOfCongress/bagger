package gov.loc.repository.packagemodeler.packge;

import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.utilities.FilenameHelper;

import javax.persistence.*;

@Embeddable
public class FileName {
	
	public FileName()
	{		
	}
	
	public FileName(String filename)
	{
		this.setFileName(filename);
	}
	
	@Column(name="relative_path", nullable = false)
	private String relativePath;

	@Column(name="base_name", nullable = false)
	private String baseName;
	
	@Column(name="extension", length=10, nullable=true)
	private String extension = null;
	
	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}
	
	public String getRelativePath() {
		return relativePath;
	}

	public void setBaseName(String baseName)
	{
		this.baseName = baseName;
	}
	
	public String getBaseName() {
		return baseName;
	}

	public void setExtension(String extension)
	{
		this.extension = extension;
	}
	
	public String getExtension() {
		return extension;
	}
	
	public void setFileName(String filename) {
		this.relativePath = FilenameHelper.getPath(filename);
		this.baseName = FilenameHelper.getBaseName(filename);
		if (FilenameHelper.hasExtension(filename))
		{
			this.extension = FilenameHelper.getExtension(filename);
		}
	}
	
	public String getFilename()
	{
		return FilenameHelper.getFileName(this.relativePath, this.baseName, this.extension);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FileName))
		{
			return false;
		}
		FileName fileName = (FileName)obj;
		return FilenameHelper.equals(fileName.getFilename(), this.getFilename());
	}

	@Override
	public String toString() {
		return this.getFilename();
	}
}

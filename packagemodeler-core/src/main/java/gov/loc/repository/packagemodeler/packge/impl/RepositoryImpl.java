package gov.loc.repository.packagemodeler.packge.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;

import javax.persistence.*;

@Entity(name="Repository")
@Table(name = "repository", schema="core", uniqueConstraints={@UniqueConstraint(columnNames={"repository_id"})})
public class RepositoryImpl implements Repository {

	@Id @GeneratedValue
	@Column(name = "pkey", nullable = false)
	private Long key;
	
	@Column(name = "repository_id", nullable = false)
	private String identifier;

	@OneToMany(mappedBy="repository", targetEntity=PackageImpl.class)
	public Set<Package> packageSet = new HashSet<Package>();
	
	public void setId(String repositoryId) {
		this.identifier = repositoryId;
	}

	public String getId() {
		return identifier;
	}

	public Long getKey() {
		return key;
	}
		
	public Set<Package> getPackages()
	{
		return Collections.unmodifiableSet(this.packageSet);
	}
	
	public void addPackage(Package packge)
	{
		if (packge.getRepository() != null)
		{
			packge.getRepository().removePackage(packge);
		}
		packge.setRepository(this);
		this.packageSet.add(packge);
	}
	
	public void removePackage(Package packge)
	{
		packge.setRepository(null);
		this.packageSet.remove(packge);
	}
	
	@Override
	public String toString() {
		return "Repository with id " + this.getId();
	}
}

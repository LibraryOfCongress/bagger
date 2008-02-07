package gov.loc.repository.utilities.ndnp;

import java.util.List;
import java.util.ArrayList;

public class BatchCharacterization {
	private List<String> reelNumberList = new ArrayList<String>();
	private List<String> lccnList = new ArrayList<String>();
	private String packageId = null;
	
	public void setReelNumberList(List<String> reelNumberList) {
		this.reelNumberList = reelNumberList;
	}
	public List<String> getReelNumberList() {
		return reelNumberList;
	}
	public void setLccnList(List<String> lccnList) {
		this.lccnList = lccnList;
	}
	public List<String> getLccnList() {
		return lccnList;
	}
	
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
	public String getPackageId() {
		return packageId;
	}
	@Override
	public String toString() {
		String lccnString = null;
		if (this.lccnList.size() > 0)
		{
			for(String lccn : this.lccnList)
			{
				if (lccnString == null)
				{
					lccnString = "The lccns are " + lccn;
				}
				else
				{
					lccnString += ", " + lccn;
				}
			}
			lccnString += ".";
		}
		else
		{
			lccnString = "There are no lccns.";
		}
		
		String reelNumberString = null;
		if (this.reelNumberList.size() > 0)
		{
			for(String reelNumber : this.reelNumberList)
			{
				if (reelNumberString == null)
				{
					reelNumberString = "The reel numbers are " + reelNumber;
				}
				else
				{
					reelNumberString += ", " + reelNumber;
				}
			}
			reelNumberString += ".";
		}
		else
		{
			reelNumberString = "There are no reel numbers.";
		}
		
		String packageIdString = "PackageId is not supplied.";
		if (this.packageId != null)
		{
			packageIdString = "PackageId is " + this.packageId;
		}
		
		return packageIdString + " " + lccnString + " " + reelNumberString;
	}
}

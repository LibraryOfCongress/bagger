package gov.loc.repository.bagger;

import gov.loc.repository.bagger.json.JSONException;
import gov.loc.repository.bagger.json.JSONObject;
import gov.loc.repository.bagger.json.JSONTokener;
import gov.loc.repository.bagger.json.JSONWriter;

import java.io.StringWriter;


public class Organization {
	private ProfileField name;
	private ProfileField address;
	
	public static String FIELD_SOURCE_ORGANIZATION = "Source-Organization";
	public static String FIELD_ORGANIZATION_ADDRESS = "Organization-Address";
	
	public Organization()
	{
		name = new ProfileField();
		name.setFieldName(FIELD_SOURCE_ORGANIZATION);
		address = new ProfileField();
		address.setFieldName(FIELD_ORGANIZATION_ADDRESS);
	}
	
	public void setName(ProfileField n) {
		this.name = n;
	}
	
	public ProfileField getName() {
		return this.name;
	}
	
	public void setAddress(ProfileField a) {
		this.address = a;
	}
	
	public ProfileField getAddress() {
		return this.address;
	}
	
	public String toString() {
		return toString(false);
	}
	
	public String toString(boolean inline) {
		String delim;
		if (inline) delim = ", ";
		else delim = "\n";
		StringBuffer sb = new StringBuffer();
		sb.append(this.getName());
		sb.append(delim);
		sb.append(this.getAddress());
		
		return sb.toString();
	}
	
	public static Organization createOrganization(JSONObject organizationJson) throws JSONException
	{
		Organization organization = new Organization();
		ProfileField name = null;
		ProfileField address  = null;
		if(organizationJson != null)
		{
			if(organizationJson.has(FIELD_SOURCE_ORGANIZATION))
			{
				JSONObject jsonObjectOrgName = (JSONObject)organizationJson.get(FIELD_SOURCE_ORGANIZATION);
				if(jsonObjectOrgName != null)
					name = ProfileField.createProfileField(jsonObjectOrgName, FIELD_SOURCE_ORGANIZATION);
			}

			if(organizationJson.has(FIELD_ORGANIZATION_ADDRESS))
			{
				JSONObject jsonObjectOrgAddr = (JSONObject)organizationJson.get(FIELD_ORGANIZATION_ADDRESS);
				if(jsonObjectOrgAddr != null)
					address = ProfileField.createProfileField(jsonObjectOrgAddr, FIELD_ORGANIZATION_ADDRESS);
			}
		}
		
		if(name == null)
		{
			name = new ProfileField();
			name.setFieldName(FIELD_SOURCE_ORGANIZATION);
		}
		if(address == null)
		{
			address = new ProfileField();
			address.setFieldName(FIELD_ORGANIZATION_ADDRESS);
		}
		organization.setName(name);
		organization.setAddress(address);
		return organization;
	}

	public String serialize() throws JSONException {
		StringWriter writer = new StringWriter();
		JSONWriter orgStringer = new JSONWriter(writer);
		
		orgStringer.object().key(FIELD_ORGANIZATION_ADDRESS).value(new JSONObject(new JSONTokener(
				this.getAddress().seralize())));
		orgStringer.key(FIELD_SOURCE_ORGANIZATION).value(new JSONObject(new JSONTokener
				(getName().seralize().toString())));
		orgStringer.endObject();
		return writer.toString();
	}
}

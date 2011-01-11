package gov.loc.repository.bagger;

import gov.loc.repository.bagger.json.JSONException;
import gov.loc.repository.bagger.json.JSONObject;
import gov.loc.repository.bagger.json.JSONTokener;
import gov.loc.repository.bagger.json.JSONWriter;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile {
	public static String NO_PROFILE_NAME = "<no profile>";
	
	public static String FIELD_NAME="name";
	public static String FIELD_ORGANIZATION="Organization";
	public static String FIELD_SENDTO="Send-To";
	public static String FIELD_SENDFROM="Send-From";
	public static String FIELD_CUSTOM_INFO="Custom-info";
	public static String FIELD_STANDARD_INFO="Standard-info";
	
	private Contact sendToContact = new Contact(true);
	private Contact sendFromContact = new Contact(false);
	private Organization organization = new Organization();
	private String name;
	private boolean isDefault = false;
	private HashMap<String,ProfileField> customFields =  new HashMap<String, ProfileField>();
	private HashMap<String,ProfileField> standardFields =  new HashMap<String, ProfileField>();
	
	
	
	public void setSendToContact(Contact sendToContact) {
		this.sendToContact = sendToContact;
	}

	public Contact getSendToContact() {
		return sendToContact;
	}

	public void setSendFromContact(Contact sendFromContact) {
		this.sendFromContact = sendFromContact;
	}

	public Contact getSendFromContact() {
		return sendFromContact;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setName(String profileName) {
		this.name = profileName;
	}

	public String getName() {
		return name;
	}

	public void setCustomFields(HashMap<String,ProfileField> fields) {
		this.customFields = fields;
	}

	public HashMap<String,ProfileField> getCustomFields() {
		return customFields;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		return sb.toString();
	}
	
	public static Profile createProfile(JSONObject profileJson, String profileName) throws JSONException
	{
		Profile profile = new Profile();
		profile.setName(profileName);
		
		JSONObject organizationJson = null;
		if(profileJson.has(Profile.FIELD_ORGANIZATION))
			organizationJson = (JSONObject)profileJson.get(Profile.FIELD_ORGANIZATION);
		
		Organization organization = Organization.createOrganization(organizationJson);
		profile.setOrganization(organization);
		
		JSONObject contactSendToJson = null;
	     if(profileJson.has(Profile.FIELD_SENDTO))
			contactSendToJson = (JSONObject)profileJson.get(Profile.FIELD_SENDTO);
		
		Contact sendToContact = Contact.createContact(contactSendToJson,true);
		profile.setSendToContact(sendToContact);
		
		JSONObject contactSendFromJson = null;
		if(profileJson.has(Profile.FIELD_SENDFROM))
		  contactSendFromJson = (JSONObject)profileJson.get(Profile.FIELD_SENDFROM);
		  
		Contact sendFromContact = Contact.createContact(contactSendFromJson,false);
		profile.setSendFromContact(sendFromContact);
		
		JSONObject customInfoJson = null;
		if(profileJson.has(Profile.FIELD_CUSTOM_INFO))
		   customInfoJson = (JSONObject)profileJson.get(Profile.FIELD_CUSTOM_INFO);
		HashMap<String, ProfileField> fields = getFields(customInfoJson);
		profile.setCustomFields(fields);
				
		HashMap<String, ProfileField> profileFields = getFields(profileJson);
		profile.setStandardFields(profileFields);
		
		return profile;
	}
	
	public static HashMap<String, ProfileField> getFields(JSONObject fieldsJson) throws JSONException
	{
		HashMap<String, ProfileField> profileFields = new HashMap<String, ProfileField>();
		if(fieldsJson != null) 
		{
			String[] names = JSONObject.getNames(fieldsJson);
			if(names == null)
				return profileFields;
			
			for(String name: names)
			{
				JSONObject jsonObject = (JSONObject) fieldsJson.get(name);
				ProfileField profileField = ProfileField.createProfileField(jsonObject, name);
				profileFields.put(profileField.getFieldName(),profileField);
			}
		}
		return profileFields;
	}

	public void setStandardFields(HashMap<String,ProfileField> standardFields) {
		this.standardFields = standardFields;
	}

	public HashMap<String,ProfileField> getStandardFields() {
		return standardFields;
	}

	public void setIsDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public boolean getIsDefault() {
		return isDefault;
	}

	public void serialize(JSONWriter jsonWriter) throws JSONException {
		
		JSONWriter writer = jsonWriter.object().key(Profile.FIELD_NAME).value(getName());
		String orgStringer = getOrganization().serialize();
		String fromContact = getSendFromContact().serialize();
		String toContact = getSendToContact().serialize();
		String customFields = seralizeFields(this.getCustomFields().values());
		String standardFields = seralizeFields(this.getStandardFields().values());
		writer.key(FIELD_ORGANIZATION).value(new JSONObject(new JSONTokener(orgStringer.toString())));
		writer.key(FIELD_SENDFROM).value(new JSONObject(new JSONTokener(fromContact.toString())));
		writer.key(FIELD_SENDTO).value(new JSONObject(new JSONTokener(toContact)));
		writer.key(FIELD_CUSTOM_INFO).value(new JSONObject(new JSONTokener(customFields)));
		writer.key(FIELD_STANDARD_INFO).value(new JSONObject(new JSONTokener(standardFields)));
		writer.endObject();
	}
	
	private String seralizeFields(Collection<ProfileField> profileFields ) throws JSONException
	{
		StringWriter writer = new StringWriter();
		JSONWriter filedWriter = new JSONWriter(writer);
		filedWriter.object();
		for(ProfileField field: profileFields )
		{
			String fieldStringer = field.seralize();
			filedWriter.key(field.getFieldName()).value(new JSONObject(new JSONTokener(fieldStringer)));
		}
		filedWriter.endObject();
		return writer.toString();
	}
	
	public boolean isNoProfile() {
		return NO_PROFILE_NAME.equals(getName());
	}
	
	public List<ProfileField> getProfileFields() {
		ArrayList<ProfileField> fields = new ArrayList<ProfileField>();
		
		for (Map.Entry<String, ProfileField> entry : standardFields.entrySet()) {
			fields.add(entry.getValue());
		}
		
		for (Map.Entry<String, ProfileField> entry : customFields.entrySet()) {
			fields.add(entry.getValue());
		}
		
		return fields;
	}
}

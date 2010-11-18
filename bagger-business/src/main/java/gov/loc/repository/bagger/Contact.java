package gov.loc.repository.bagger;

import gov.loc.repository.bagger.json.JSONException;
import gov.loc.repository.bagger.json.JSONObject;
import gov.loc.repository.bagger.json.JSONTokener;
import gov.loc.repository.bagger.json.JSONWriter;

import java.io.StringWriter;

/**
 *
|    (Contact-Name: Edna Janssen                                          )
|    (Contact-Phone: +1 408-555-1212                                      )
|    (Contact-Nmail: ej@spengler.edu                                      )
 *
 * @author Jon Steinbach
 */
public class Contact {
	private ProfileField contactName;
	private ProfileField telephone;
	private ProfileField email;
	
	public static String FIELD_CONTACT_NAME = "Contact-Name";
	public static String FIELD_CONTACT_PHONE = "Contact-Phone";
	public static String FIELD_CONTACT_EMAIL = "Contact-Email";
	
	public static String FIELD_TO_CONTACT_NAME = "To-Contact-Name";
	public static String FIELD_TO_CONTACT_PHONE = "To-Contact-Phone";
	public static String FIELD_TO_CONTACT_EMAIL = "To-Contact-Email";
	
	public static String FIELD_JSON_NAME = "name";
	public static String FIELD_JSON_PHONE = "phone";
	public static String FIELD_JSON_EMAIL = "email";
	
	public Contact(){}
	
	public Contact(boolean isSentTo)
	{
		String name = isSentTo?FIELD_TO_CONTACT_NAME:FIELD_CONTACT_NAME;
		String phone = isSentTo?FIELD_TO_CONTACT_PHONE:FIELD_CONTACT_PHONE;
		String mail = isSentTo?FIELD_TO_CONTACT_EMAIL:FIELD_CONTACT_EMAIL;
		
		contactName = new ProfileField();
		contactName.setFieldName(name);

		telephone = new ProfileField();
		telephone.setFieldName(phone);

		email = new ProfileField();
		email.setFieldName(mail);
	}
	
	public ProfileField getContactName() {
		return this.contactName;
	}
	
	public void setContactName(ProfileField name) {
		this.contactName = name;
	}

	public ProfileField getTelephone() {
		return this.telephone;
	}
	
	public void setTelephone(ProfileField telephone) {
		this.telephone = telephone;
	}

	public ProfileField getEmail() {
		return this.email;
	}
	
	public void setEmail(ProfileField email) {
		this.email = email;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
//		sb.append(this.getContactName());
//		sb.append('\n');
//		sb.append(this.getTelephone());
//		sb.append('\n');
//		sb.append(this.getEmail());
//		sb.append('\n');
		
		return sb.toString();
	}

	public static Contact createContact(JSONObject contactSendToJson,
			boolean sendTo) throws JSONException {
		Contact contact = new Contact();
		String name = sendTo?FIELD_TO_CONTACT_NAME:FIELD_CONTACT_NAME;
		String phone = sendTo?FIELD_TO_CONTACT_PHONE:FIELD_CONTACT_PHONE;
		String email = sendTo?FIELD_TO_CONTACT_EMAIL:FIELD_CONTACT_EMAIL;
		
		ProfileField namefield = null;
		ProfileField phonefield = null;
		ProfileField emailfield = null;
		
		
		if(contactSendToJson != null)
		{
			if(contactSendToJson.has(FIELD_JSON_NAME))
			{
				JSONObject nameJson = (JSONObject)contactSendToJson.get(FIELD_JSON_NAME);
				if(nameJson != null)
					namefield = ProfileField.createProfileField(nameJson, name);
			}

			if(contactSendToJson.has(FIELD_JSON_PHONE))
			{
				JSONObject phoneJson = (JSONObject)contactSendToJson.get(FIELD_JSON_PHONE);
				if(phoneJson != null)
					phonefield = ProfileField.createProfileField(phoneJson, phone);
			}

			if(contactSendToJson.has(FIELD_JSON_EMAIL))
			{
				JSONObject emailJson= (JSONObject)contactSendToJson.get(FIELD_JSON_EMAIL);
				if(emailJson != null)
					emailfield = ProfileField.createProfileField(emailJson, email);
			}
		}
		
		if(namefield == null)
		{
			namefield = new ProfileField();
			namefield.setFieldName(name);
		}
		
		if(phonefield == null)
		{
			phonefield = new ProfileField();
			phonefield.setFieldName(phone);
		}
		
		if(emailfield == null)
		{
			emailfield = new ProfileField();
			emailfield.setFieldName(email);
		}
		
		contact.setContactName(namefield);
		contact.setTelephone(phonefield);
		contact.setEmail(emailfield);
		
		return contact;
	}
	
	public String serialize() throws JSONException {

		StringWriter writer = new StringWriter();
		JSONWriter contactWriter = new JSONWriter(writer);
		
		contactWriter.object().key(FIELD_JSON_NAME).value(new JSONObject(new JSONTokener(getContactName().seralize())));
		contactWriter.key(FIELD_JSON_PHONE).value(new JSONObject(new JSONTokener(getTelephone().seralize())));
		contactWriter.key(FIELD_JSON_EMAIL).value(new JSONObject(new JSONTokener(getEmail().seralize().toString())));
		contactWriter.endObject();
		
		return writer.toString();
	}
	
}

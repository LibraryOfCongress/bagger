package gov.loc.repository.bagger;

import gov.loc.repository.bagger.json.JSONArray;
import gov.loc.repository.bagger.json.JSONException;
import gov.loc.repository.bagger.json.JSONObject;
import gov.loc.repository.bagger.json.JSONWriter;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ProfileField {

	private String fieldName = "";
	private String fieldValue = "";
	private String fieldType = "";
	private boolean isReadOnly = false;
	private List<String> elements = new ArrayList<String>();
	private boolean isRequired;
	private boolean isValueRequired;
	
	final static String FIELD_REQUIRED_VALUE = "requiredValue";
	final static String FIELD_REQUIRED = "fieldRequired";
	final static String FIELD_TYPE ="fieldType";
	final static String FIELD_READ_ONLY ="isReadOnly";
	final static String FIELD_DEFAULT_VALUE="defaultValue";
	final static String FIELD_VALUE_LIST="valueList";
 
	public void setFieldName(String s) {
		this.fieldName = s;
	}

	public String getFieldName() {
		return this.fieldName;
	}

	public void setIsRequired(boolean b) {
		this.isRequired = b;
	}

	public boolean getIsRequired() {
		return this.isRequired;
	}

	public void setFieldValue(String s) {
		this.fieldValue = s;
	}

	public String getFieldValue() {
		return this.fieldValue;
	}
	
	public void setElements(List<String> s) {
		this.elements = s;
	}
	
	public List<String> getElements() {
		return this.elements;
	}

	public void setFieldType(String s) {
		this.fieldType = s;
	}
	
	public String getFieldType() {
		return this.fieldType;
	}

	public void setIsValueRequired(boolean b) {
		this.isValueRequired = b;
	}

	public boolean getIsValueRequired() {
		return this.isValueRequired;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.fieldName + '=' + this.fieldValue);
		sb.append('\n');
		return sb.toString();
	}

	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}
	
	public static ProfileField createProfileField(JSONObject profileFieldJson,String name) throws JSONException
	{
		ProfileField profileField = new ProfileField();
		profileField.setFieldName(name);
		if(profileFieldJson != null)
		{
			if(profileFieldJson.has(FIELD_TYPE))
			{
				String fieldType =   (String)profileFieldJson.get(FIELD_TYPE);
				profileField.setFieldType(fieldType);
			}

			if(profileFieldJson.has(FIELD_REQUIRED_VALUE))
			{
				String fieldValue =   (String)profileFieldJson.get(FIELD_REQUIRED_VALUE);
				profileField.setFieldValue(fieldValue);
			}

			if(profileFieldJson.has(FIELD_READ_ONLY))
			{
				boolean isreadOnly =   (Boolean)profileFieldJson.get(FIELD_READ_ONLY);
				profileField.setReadOnly(isreadOnly);
			}

			if(profileFieldJson.has(FIELD_VALUE_LIST))
			{
				JSONArray jsonArray =   (JSONArray)profileFieldJson.get(FIELD_VALUE_LIST);
				List<String> valueList = new ArrayList<String>();
				for(int i=0; i<jsonArray.length(); i++ )
				{
					String value= (String)jsonArray.get(i);
					valueList.add(value);
				}
				profileField.setElements(valueList);
				profileField.setFieldValue(valueList.get(0));
			}
			// Default value selected from value list
			if(profileFieldJson.has(FIELD_DEFAULT_VALUE))
			{
				String defaultValue = (String)profileFieldJson.get(FIELD_DEFAULT_VALUE);
				profileField.setFieldValue(defaultValue);
			}

			if(profileFieldJson.has(FIELD_REQUIRED))
			{
				boolean isRequired =   (Boolean)profileFieldJson.get(FIELD_REQUIRED);
				profileField.setIsRequired(isRequired);
			}
		}
		return profileField;
	}
	
	public static ProfileField createProfileField(String name,String value)
	{
		ProfileField profileField = new ProfileField();
		profileField.setFieldName(name);
		profileField.setFieldValue(value);
		return profileField;
	}
	
	public String seralize() throws JSONException
	{
		StringWriter writer = new StringWriter();
		JSONWriter profileWriter = new JSONWriter(writer);
		 profileWriter.object().key(FIELD_REQUIRED_VALUE).value(this.getFieldValue());
		 profileWriter.key(FIELD_REQUIRED).value(getIsRequired());
		 profileWriter.key(FIELD_TYPE).value(getFieldType());
		 profileWriter.key(FIELD_READ_ONLY).value(isReadOnly());
		if(this.getElements().size()>0)
			profileWriter.key(FIELD_VALUE_LIST).value(this.getElements());
		profileWriter.endObject();
		return  writer.toString();
	}
}

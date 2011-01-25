
package gov.loc.repository.bagger.bag;

import gov.loc.repository.bagger.ProfileField;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BagInfoField {
	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(BagInfoField.class);
	public static final int TEXTFIELD_COMPONENT = 1;
	public static final int TEXTAREA_COMPONENT = 2;
	public static final int LIST_COMPONENT = 3;
	public static final String TEXTFIELD_CODE = "TF";
	public static final String TEXTAREA_CODE = "TA";
	public static final int MAX_VALUE = 32;

	private String name = "";
	private String label = "";
	private String value = "";
	private List<String> elements = new ArrayList<String>();
	private int componentType = TEXTFIELD_COMPONENT;
	private boolean isEnabled = true;
	private boolean isRequired = false;
	private boolean isRequiredvalue = false;
	private boolean isEditable = true;
	private boolean isProfile = false;

    public BagInfoField() {
    	
    }
    
    public BagInfoField(ProfileField projectProfile)
    {
		this.setLabel(projectProfile.getFieldName());
		this.setName(this.getLabel());
		this.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
		this.isEnabled(!projectProfile.isReadOnly());
		this.isEditable(!projectProfile.isReadOnly());
		this.isRequiredvalue(projectProfile.getIsValueRequired());
		this.isRequired(projectProfile.getIsRequired());
		this.setValue(projectProfile.getFieldValue());
		//field.setValue("");
		if(projectProfile.isReadOnly())
			this.isEnabled(false);
		this.buildElements(projectProfile.getElements());
		if (projectProfile.getFieldType().equalsIgnoreCase(BagInfoField.TEXTFIELD_CODE)) {
			this.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
		} else if (projectProfile.getFieldType().equalsIgnoreCase(BagInfoField.TEXTAREA_CODE)) {
			this.setComponentType(BagInfoField.TEXTAREA_COMPONENT);
		}	else if (!(projectProfile.getElements().isEmpty())) {
			this.setComponentType(BagInfoField.LIST_COMPONENT);
		}
    }

    public void setName(String n) {
    	this.name = n;
    }

    public String getName() {
    	return this.name;
    }
    
    public void setLabel(String l) {
    	this.label = l;
    }
    
    public String getLabel() {
    	return this.label;
    }
    
    public void setValue(String v) {
    	this.value = v;
    }
    
    public String getValue() {
    	return this.value;
    }
    
    public void setElements(List<String> e) {
    	this.elements = e;
    }
    
    public List<String> getElements() {
    	return this.elements;
    }
        
    public void setComponentType(int type) {
    	this.componentType = type;
    }

    public int getComponentType() {
    	return this.componentType;
    }
    
    public void isEditable(boolean b) {
    	this.isEditable = b;
    }
    
    public boolean isEditable() {
    	return this.isEditable;
    }
    
    public void isEnabled(boolean b) {
    	this.isEnabled = b;
    }

    public boolean isEnabled() {
    	return this.isEnabled;
    }

    public void isRequired(boolean b) {
    	this.isRequired = b;
    }

    public boolean isRequired() {
    	return this.isRequired;
    }
        
    public void isRequiredvalue(boolean b) {
    	this.isRequiredvalue = b;
    }

    public boolean isRequiredvalue() {
    	return this.isRequiredvalue;
    }

    public void isProfile(boolean b) {
    	this.isProfile = b;
    }
    
    public boolean isProfile() {
    	return this.isProfile;
    }
    
    public void buildElements(List<String> elements) {
		
		this.elements = elements;
    }
    
   
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append("\n");
    	sb.append("Label: " + getLabel() + "\n");
    	sb.append("Name: " + getName() + "\n");
    	sb.append("Value: " + getValue() + "\n");
    	sb.append("Type: " + getComponentType() + "\n");
    	sb.append("Elements: " + getElements() + "\n");
    	sb.append("isRequired: " + isRequired() + "\n");
    	sb.append("isRequiredvalue: " + isRequiredvalue() + "\n");
    	sb.append("isEnabled: " + isEnabled() + "\n");
    	sb.append("isEditable: " + isEditable() + "\n");
    	return sb.toString();
    }

	
}
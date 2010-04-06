package gov.loc.repository.bagger.bag.impl;

import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.Organization;
import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.ProfileField;
import gov.loc.repository.bagger.bag.BagInfoField;
import gov.loc.repository.bagger.bag.BaggerOrganization;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.impl.BagInfoTxtImpl;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * @author Jon Steinbach
 */
public class DefaultBagInfo extends BagInfoTxtImpl {
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(DefaultBagInfo.class);
	public static final String FIELD_NEW_COMPONENT = "New";
	public static final String FIELD_LC_PROJECT = "LC-Project";
	

	
	protected DefaultBag baggerBag = null;
	public BagInfoTxt standardBagInfo = null;
	private String bagName = new String();
	private BaggerOrganization baggerOrganization = new BaggerOrganization();
	private HashMap<String, BagInfoField> profileMap = new HashMap<String, BagInfoField>();
	private HashMap<String, BagInfoField> fieldMap = new HashMap<String, BagInfoField>();
	//TODO
	public static final String[] profileStrings = {FIELD_SOURCE_ORGANIZATION, FIELD_ORGANIZATION_ADDRESS, FIELD_CONTACT_NAME, FIELD_CONTACT_PHONE, FIELD_CONTACT_EMAIL, 
		                                       Contact.FIELD_TO_CONTACT_NAME,  Contact.FIELD_TO_CONTACT_PHONE,  Contact.FIELD_TO_CONTACT_EMAIL};
	
	public static final ArrayList<String> ignoreFields =  new ArrayList(Arrays.asList(profileStrings));
	public static final HashSet<String> profileSet = new HashSet<String>(Arrays.asList(profileStrings));	
	public static String[] readOnlyStrings = { FIELD_LC_PROJECT, FIELD_PAYLOAD_OXUM };
	public static final HashSet<String> readOnlySet = new HashSet<String>(Arrays.asList(readOnlyStrings));
	public static final String[] textAreaStrings = {FIELD_EXTERNAL_DESCRIPTION, FIELD_INTERNAL_SENDER_DESCRIPTION};
	public static final HashSet<String> textAreaSet = new HashSet<String>(Arrays.asList(textAreaStrings));
	private Object[] requiredStrings = {}; //{FIELD_EXTERNAL_DESCRIPTION, FIELD_BAGGING_DATE, FIELD_EXTERNAL_IDENTIFIER, FIELD_BAG_SIZE, FIELD_EDEPOSIT_PUBLISHER, FIELD_NDNP_AWARDEE_PHASE, FIELD_LC_PROJECT};
	private HashSet<Object> requiredSet = new HashSet<Object>();

	private String name;

	private String profileName ="";

	public DefaultBagInfo(DefaultBag baggerBag) {
		super(baggerBag.getBag().getBagConstants());
		standardBagInfo = baggerBag.getBag().getBagPartFactory().createBagInfoTxt();
		this.setBagName(baggerBag.getName());
		this.baggerBag = baggerBag;
		this.setRequiredSet(new HashSet<Object>(Arrays.asList(requiredStrings)));
		log.debug("DefaultBagInfo");
	}
	
	public void setBag(DefaultBag baggerBag) {
		this.baggerBag = baggerBag;
	}
	
	public DefaultBag getBag() {
		return this.baggerBag;
	}

	public void setBagName(String name) {
		this.bagName = name;
	}
	
	public String getBagName() {
		return this.bagName;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

	public Object[] getRequiredStrings() {
		return this.requiredStrings;
	}
	
	public void setRequiredStrings(Object[] s) {
		this.requiredStrings = s;
		if (this.requiredStrings == null) return;
		this.setRequiredSet(new HashSet<Object>(Arrays.asList(requiredStrings)));
	}

	public HashSet<Object> getRequiredSet() {
		return this.requiredSet;
	}
	
	public void setRequiredSet(HashSet<Object> s) {
		this.requiredSet = s;
	}
	
	public void setBagOrganization(BaggerOrganization baggerOrganization) {
		try {
			this.setSourceOrganization(baggerOrganization.getSourceOrganization());
			this.setOrganizationAddress(baggerOrganization.getOrganizationAddress());
			this.baggerOrganization.setSourceOrganization(this.getSourceOrganization());
			this.baggerOrganization.setOrganizationAddress(this.getOrganizationAddress());

			Contact contact = baggerOrganization.getContact();
			this.setContactName(contact.getContactName().getFieldValue());
			this.setContactPhone(contact.getTelephone().getFieldValue());
			this.setContactEmail(contact.getEmail().getFieldValue());
			this.baggerOrganization.getContact().setContactName(
					 ProfileField.createProfileField(Contact.FIELD_CONTACT_NAME,this.getContactName()));
			this.baggerOrganization.getContact().setTelephone(
					ProfileField.createProfileField(Contact.FIELD_CONTACT_PHONE,this.getContactPhone()));
			this.baggerOrganization.getContact().setEmail(
					ProfileField.createProfileField(Contact.FIELD_CONTACT_EMAIL,this.getContactEmail()));
		} catch (Exception e) {
		}
	}

	public BaggerOrganization getBagOrganization() {
		return this.baggerOrganization;
	}

	public void copy(DefaultBagInfo bagInfo) {
		try {
			BaggerOrganization baggerOrganization = bagInfo.getBagOrganization();
			this.setBagOrganization(baggerOrganization);
			
			for(String key : bagInfo.keySet())
			{
				if(ignoreFields.contains(key))
					continue;
				
				String value = this.get(key);
				String bagValue = bagInfo.get(key);
				if (value != null && bagValue != null && !bagValue.isEmpty())
					this.put(key,value);
				else
					this.put(key,"");
				
			}
			
		} catch (Exception e) {
		}
	}

	@Override
	public String toString() {
		StringBuffer content = new StringBuffer();
		for(String key: this.keySet())
		{
			content.append(key + ": ");
			String value = this.get(key);
			if (value != null && !value.isEmpty())
				content.append(value + "\n");
			else
				content.append("\n");
		}
		return content.toString();
	}

	public HashMap<String, BagInfoField> getFieldMap() {
		return this.fieldMap;
	}

	public void setFieldMap(HashMap<String, BagInfoField> map) {
		this.fieldMap = map;
	}
	
	public HashMap<String, BagInfoField> getProfileMap() {
		return this.profileMap;
	}
	
	public void setProfileMap(HashMap<String, BagInfoField> map) {
		this.profileMap = map;
	}

	public void createExistingFieldMap(boolean enabled) {
		this.fieldMap = new HashMap<String, BagInfoField>();
		this.profileMap = new HashMap<String, BagInfoField>();
//		BagInfoTxt bagInfoTxt = baggerBag.getBag().getBagInfoTxt();
//		if (bagInfoTxt != null) {
//			Set<String> keys = bagInfoTxt.keySet();
			Set<String> keys = this.keySet();
			if (keys != null) {
				for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
					String label = (String) iter.next();
					label = label.trim();
					if (label.isEmpty()) continue;
					BagInfoField field = createField(label, enabled);
					if (profileSet.contains(label)) {
						profileMap.put(label, field);
					} else {
						fieldMap.put(label, field);
					}
				}
			}
//		}
	}
	
	public void createStandardFieldMap(boolean enabled) {
		if (this.fieldMap == null) this.fieldMap = new HashMap<String, BagInfoField>();
		log.debug("createStandardFieldMap: " + enabled);
		List<String> ls = getStandardBagFields();
		log.info("createStandardFieldMap: " + ls.size() + "::" + ls);
		for (int i=0; i<ls.size(); i++) {
			String label = ls.get(i);
			label = label.trim();
			if (label.isEmpty()) continue;
			log.debug("StandardFields: " + label);
			if (!profileSet.contains(label)) {
				BagInfoField field = createField(label, enabled);
		    	if (label.equalsIgnoreCase(DefaultBagInfo.FIELD_LC_PROJECT)) {
		    		field.setValue(baggerBag.getProfile().getName());
		    		field.isEnabled(false);
		    	} else if (label.equalsIgnoreCase(DefaultBagInfo.FIELD_BAGGING_DATE)) {
		    		field.setValue(DefaultBagInfo.getTodaysDate());
		    	} else if (DefaultBagInfo.readOnlySet.contains(label)) {
		    		field.isEnabled(false);
		    	} else if (DefaultBagInfo.readOnlySet.contains(label)) {
		    		field.isEnabled(false);
		    	}
		    	fieldMap.put(label, field);
			}
		}
	}
	
	public void createProfileFieldList(boolean enabled) {
		List<String> ls = getStandardBagFields();
		for (int i=0; i<ls.size(); i++) {
			String label = ls.get(i);
			label = label.trim();
			if (label.isEmpty()) continue;
			if (profileSet.contains(label)) {
				BagInfoField field = createField(label, enabled);
				profileMap.put(label, field);
			}
		}
	}
	
	public static String getTodaysDate() {
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		Date todaysDate = new Date();
		String baggingDate = formatter.format(todaysDate);
		
		return baggingDate;
	}
	
	private BagInfoField createField(String label, boolean enabled) {
		String name = getMethodFromLabel(label);

		label = label.trim();
		BagInfoField field = new BagInfoField();
		if (textAreaSet.contains(label)) {
			field.setComponentType(BagInfoField.TEXTAREA_COMPONENT);
		} else {
			field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
		}
		field.setName(name);
		field.setLabel(label);
		if (readOnlySet.contains(label)) {
			field.isEnabled(false);
		}else 
		{
			Profile profile = this.baggerBag.getProfile();
			HashMap<String,ProfileField> standardFields = profile.getStandardFields();

			if(standardFields != null && standardFields.get(label) != null)
			{
				if(standardFields.get(label).isReadOnly())
				{
					field.isEnabled(false);	
				}
				else
				{
					field.isEnabled(enabled);
				}

			}
			else
			{
				field.isEnabled(enabled);
			}
		}

		if (!baggerBag.isNoProject() && requiredSet.contains(label)) {
			field.isRequired(true);
		} else {
			field.isRequired(false);
		}
		

		
		return field;
	}
	
	public static String getMethodFromLabel(String label) {
		String delimeter = new String("-");
		String methodName = new String();

		try {
			label = label.trim();
			String[] list = label.split(delimeter);
			if (list != null && list.length > 0) {
				for (int i=0; i < list.length; i++) {
					log.debug("[" + list[i] + "]");
					String s = list[i];
					if (i == 0) methodName = s.toLowerCase();
					else methodName += list[i];
				}
				log.debug("\n");
			}
		} catch (Exception e) {
		}
		return methodName;
	}

	// TODO: return standard fields and project specific fields
	public List<String> getStandardBagFields() {
		ArrayList<String> list = new ArrayList<String>();
		try {
	        list.add("");
	        
			if (!this.baggerBag.isNoProject()) {
		        list.add(FIELD_LC_PROJECT);
			}
			Profile profile = this.baggerBag.getProfile();
			HashMap<String,ProfileField> standardFields = profile.getStandardFields();
			if(standardFields != null)
			{
				list.addAll(standardFields.keySet());
			}
			
			//Standard Fields from BagInfoTxt
			List<String> ls = getFieldList();
			for (int i=0; i<ls.size(); i++) {
				String label = ls.get(i);
				label = label.trim();
				if (label.isEmpty()) continue;
				if (!profileSet.contains(label)) {
					list.add(label);
					log.info("getStandardBagFields["+i+"] " + label);
				}
			}
			log.info("getStandardBagFields: " + list.size() + "::" + list);
		} catch (Exception e) {
		}
		return list;
	}

	//Returns Standard BagInfo fields
	private List<String> getFieldList() {
		List<String> standardFields = new ArrayList<String>();
		Field[] fields = BagInfoTxtImpl.class.getFields();

		log.info("getFieldList: " + fields.length);
		try {
			for(Field field : fields) {
				log.debug("getField: " + field.getName());
				if (field.getName().startsWith("FIELD_")) {
//				if (field.getName().startsWith("FIELD_") && this.containsKey(field.get(this))) {
					String fieldName = (String)field.get(this);
					log.debug("add: " + fieldName);
					standardFields.add(fieldName);
				}
			}
			//TODO BNP
			standardFields.add(Contact.FIELD_TO_CONTACT_NAME);
			standardFields.add(Contact.FIELD_TO_CONTACT_PHONE);
			standardFields.add(Contact.FIELD_TO_CONTACT_EMAIL);
			
		} catch (Exception e) {
			log.error("getFieldList: " + e);
		}

		log.info("DefaultBagInfo.getStandardBagFields: " + standardFields.size());
		return standardFields;
	}
	
	public String getToContactName() {
		return this.getCaseInsensitive(Contact.FIELD_TO_CONTACT_NAME);
	}

	public void setToContactName(String toContactName) {
		this.put(Contact.FIELD_TO_CONTACT_NAME, toContactName);
	}
	
	public String getToContactPhone() {
		return this.getCaseInsensitive(Contact.FIELD_TO_CONTACT_PHONE);
	}

	public void setToContactPhone(String toContactPhone) {
		this.put(Contact.FIELD_TO_CONTACT_PHONE, toContactPhone);
	}
	
	public String getToContactEmail() {
		return this.getCaseInsensitive(Contact.FIELD_TO_CONTACT_EMAIL);
	}

	public void setToContactEmail(String toContactEmail) {
		this.put(Contact.FIELD_TO_CONTACT_EMAIL, toContactEmail);
	}
	

	public void setLcProject(String value) {
		this.profileName = value;
	}
	
	public String getLcProject() {
		return this.profileName;
	}

}
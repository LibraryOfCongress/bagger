package gov.loc.repository.bagger.bag.impl;

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

import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.bag.BagInfoField;
import gov.loc.repository.bagger.bag.BaggerOrganization;

import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.impl.BagInfoTxtImpl;

/*
 * @author Jon Steinbach
 */
public class DefaultBagInfo extends BagInfoTxtImpl {
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(DefaultBagInfo.class);
	
	public static final String FIELD_NEW_COMPONENT = "New";
	public static final String FIELD_EDEPOSIT_PUBLISHER = "Publisher";
	public static final String FIELD_NDNP_AWARDEE_PHASE = "Awardee-Phase";
	public static final String FIELD_LC_PROJECT = "LC-Project";

	protected DefaultBag baggerBag = null;
	public BagInfoTxt standardBagInfo = null;
	private String bagName = new String();
	private BaggerOrganization baggerOrganization = new BaggerOrganization();
	private HashMap<String, BagInfoField> profileMap = new HashMap<String, BagInfoField>();
	private HashMap<String, BagInfoField> fieldMap = new HashMap<String, BagInfoField>();
	public static final String[] profileStrings = {FIELD_SOURCE_ORGANIZATION, FIELD_ORGANIZATION_ADDRESS, FIELD_CONTACT_NAME, FIELD_CONTACT_PHONE, FIELD_CONTACT_EMAIL};
	public static final HashSet<String> profileSet = new HashSet<String>(Arrays.asList(profileStrings));
	public static String[] readOnlyStrings = {FIELD_PAYLOAD_OXUM, FIELD_LC_PROJECT};
	public static final HashSet<String> readOnlySet = new HashSet<String>(Arrays.asList(readOnlyStrings));
	public static final String[] textAreaStrings = {FIELD_EXTERNAL_DESCRIPTION, FIELD_INTERNAL_SENDER_DESCRIPTION};
	public static final HashSet<String> textAreaSet = new HashSet<String>(Arrays.asList(textAreaStrings));
	private Object[] requiredStrings = {}; //{FIELD_EXTERNAL_DESCRIPTION, FIELD_BAGGING_DATE, FIELD_EXTERNAL_IDENTIFIER, FIELD_BAG_SIZE, FIELD_EDEPOSIT_PUBLISHER, FIELD_NDNP_AWARDEE_PHASE, FIELD_LC_PROJECT};
	private HashSet<Object> requiredSet = new HashSet<Object>();

	private String name;
	private String content;
	private String publisher = "";
	private String awardeePhase = "";
	private String lcProject = "";

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

	public void setContent(String data) {
		this.content = data;
	}

	public String getContent() {
		return this.content;
	}

	public Object[] getRequiredStrings() {
		return this.requiredStrings;
	}
	
	public void setRequiredStrings(Object[] s) {
		this.requiredStrings = s;
		this.setRequiredSet(new HashSet<Object>(Arrays.asList(requiredStrings)));
	}

	public HashSet<Object> getRequiredSet() {
		return this.requiredSet;
	}
	
	public void setRequiredSet(HashSet<Object> s) {
		this.requiredSet = s;
	}
	
	public void setPublisher(String publisher) {
		if (publisher != null) this.publisher = publisher;
	}
	
	public String getPublisher() {
		return this.publisher;
	}

	public void setAwardeePhase(String phase) {
		if (phase != null) this.awardeePhase = phase;
	}
	
	public String getAwardeePhase() {
		return this.awardeePhase;
	}
	
	public void setLcProject(String project) {
		if (project != null) this.lcProject = project;
	}
	
	public String getLcProject() {
		return this.lcProject;
	}

	public void setBagOrganization(BaggerOrganization baggerOrganization) {
		this.setSourceOrganization(baggerOrganization.getSourceOrganization());
		this.setOrganizationAddress(baggerOrganization.getOrganizationAddress());
		this.baggerOrganization.setSourceOrganization(this.getSourceOrganization());
		this.baggerOrganization.setOrganizationAddress(this.getOrganizationAddress());

		Contact contact = baggerOrganization.getContact();
		this.setContactName(contact.getContactName());
		this.setContactPhone(contact.getTelephone());
		this.setContactEmail(contact.getEmail());
		this.baggerOrganization.getContact().setContactName(this.getContactName());
		this.baggerOrganization.getContact().setTelephone(this.getContactPhone());
		this.baggerOrganization.getContact().setEmail(this.getContactEmail());
	}

	public BaggerOrganization getBagOrganization() {
		return this.baggerOrganization;
	}

	public void copy(DefaultBagInfo bagInfo) {
		BaggerOrganization baggerOrganization = bagInfo.getBagOrganization();
		this.setBagOrganization(baggerOrganization);
		if (bagInfo.getExternalDescription() != null && !bagInfo.getExternalDescription().isEmpty())
			this.setExternalDescription(bagInfo.getExternalDescription());
		else
			this.setExternalDescription("");
		if (bagInfo.getBaggingDate() != null && !bagInfo.getBaggingDate().isEmpty()) {
			this.setBaggingDate(bagInfo.getBaggingDate());
		} else {
			this.setBaggingDate(DefaultBagInfo.getTodaysDate());
		}
		if (bagInfo.getExternalIdentifier() != null && !bagInfo.getExternalIdentifier().isEmpty())
			this.setExternalIdentifier(bagInfo.getExternalIdentifier());
		else
			this.setExternalIdentifier("");
		if (bagInfo.getBagSize() != null && !bagInfo.getBagSize().isEmpty())
			this.setBagSize(bagInfo.getBagSize());
		else
			this.setBagSize("");
		if (bagInfo.getPayloadOxum() != null && !bagInfo.getPayloadOxum().isEmpty())
			this.setPayloadOxum(bagInfo.getPayloadOxum());
		else
			this.setPayloadOxum("");
		if (bagInfo.getBagGroupIdentifier() != null && !bagInfo.getBagGroupIdentifier().isEmpty())
			this.setBagGroupIdentifier(bagInfo.getBagGroupIdentifier());
		else
			this.setBagGroupIdentifier("");
		if (bagInfo.getBagCount() != null && !bagInfo.getBagCount().isEmpty())
			this.setBagCount(bagInfo.getBagCount());
		else
			this.setBagCount("");
		if (bagInfo.getInternalSenderIdentifier() != null && !bagInfo.getInternalSenderIdentifier().equalsIgnoreCase("null"))
			this.setInternalSenderIdentifier(bagInfo.getInternalSenderIdentifier());
		else
			this.setInternalSenderIdentifier("");
		if (bagInfo.getInternalSenderDescription() != null && !bagInfo.getInternalSenderDescription().equalsIgnoreCase("null"))
			this.setInternalSenderDescription(bagInfo.getInternalSenderDescription());
		else
			this.setInternalSenderDescription("");
		if (this.baggerBag.isEdeposit()) {
			if (bagInfo.getPublisher() != null && !bagInfo.getPublisher().isEmpty())
				this.setPublisher(bagInfo.getPublisher());
			else
				this.setPublisher("");			
		}
		if (this.baggerBag.isNdnp()) {
			if (bagInfo.getAwardeePhase() != null && !bagInfo.getAwardeePhase().isEmpty())
				this.setAwardeePhase(bagInfo.getAwardeePhase());
			else
				this.setAwardeePhase("");			
		}
		if (!this.baggerBag.isNoProject()) {
			if (bagInfo.getLcProject() != null && !bagInfo.getLcProject().isEmpty())
				this.setLcProject(bagInfo.getLcProject());
			else
				this.setLcProject("");
		}
	}

	@Override
	public String toString() {
		StringBuffer content = new StringBuffer();
		content.append(FIELD_SOURCE_ORGANIZATION + ": ");
		if (this.getSourceOrganization() != null && !this.getSourceOrganization().isEmpty())
			content.append(this.getSourceOrganization() + "\n");
		else
			content.append("\n");
		content.append(FIELD_ORGANIZATION_ADDRESS + ": ");
		if (this.getOrganizationAddress() != null && !this.getOrganizationAddress().isEmpty())
			content.append(this.getOrganizationAddress() + "\n");
		else
			content.append("\n");
		content.append(FIELD_CONTACT_NAME + ": ");
		if (this.getContactName() != null && !this.getContactName().isEmpty())
			content.append(this.getContactName() + "\n");
		else
			content.append("\n");
		content.append(FIELD_CONTACT_PHONE + ": ");
		if (this.getContactPhone() != null && !this.getContactPhone().isEmpty())
			content.append(this.getContactPhone() + "\n");
		else
			content.append("\n");
		content.append(FIELD_CONTACT_EMAIL + ": ");
		if (this.getContactEmail() != null && !this.getContactEmail().isEmpty())
			content.append(this.getContactEmail() + "\n");
		else
			content.append("\n");
		content.append(FIELD_EXTERNAL_DESCRIPTION + ": ");
		if (this.getExternalDescription() != null && !this.getExternalDescription().isEmpty())
			content.append(this.getExternalDescription() + "\n");
		else
			content.append("\n");
		content.append(FIELD_BAGGING_DATE + ": ");
		if (this.getBaggingDate() != null && !this.getBaggingDate().isEmpty())
			content.append(this.getBaggingDate() + "\n");
		else
			content.append("\n");
		content.append(FIELD_EXTERNAL_IDENTIFIER + ": ");
		if (this.getExternalIdentifier() != null && !this.getExternalIdentifier().isEmpty())
			content.append(this.getExternalIdentifier() + "\n");
		else
			content.append("\n");
		content.append(FIELD_BAG_SIZE + ": ");
		if (this.getBagSize() != null && !this.getBagSize().isEmpty())
			content.append(this.getBagSize() + "\n");
		else
			content.append("\n");
		content.append(FIELD_PAYLOAD_OXUM + ": ");
		if (this.getPayloadOxum() != null && !this.getPayloadOxum().isEmpty())
			content.append(this.getPayloadOxum() + "\n");
		else
			content.append("\n");
		content.append(FIELD_BAG_GROUP_IDENTIFIER + ": ");
		if (this.getBagGroupIdentifier() != null && !this.getBagGroupIdentifier().isEmpty())
			content.append(this.getBagGroupIdentifier() + "\n");
		else
			content.append("\n");
		content.append(FIELD_BAG_COUNT + ": ");
		if (this.getBagCount() != null && !this.getBagCount().isEmpty())
			content.append(this.getBagCount() + "\n");
		else
			content.append("\n");
		content.append(FIELD_INTERNAL_SENDER_IDENTIFIER + ": ");
		if (this.getInternalSenderIdentifier() != null && !this.getInternalSenderIdentifier().isEmpty())
			content.append(this.getInternalSenderIdentifier() + "\n");
		else
			content.append("\n");
		content.append(FIELD_INTERNAL_SENDER_DESCRIPTION + ": ");
		if (this.getInternalSenderDescription() != null && !this.getInternalSenderDescription().isEmpty())
			content.append(this.getInternalSenderDescription() + "\n");
		else
			content.append("\n");
		if (this.baggerBag.isEdeposit()) {
			content.append(FIELD_EDEPOSIT_PUBLISHER + ": ");
			if (this.getPublisher() != null && !this.getPublisher().isEmpty())
				content.append(this.getPublisher() + "\n");
			else
				content.append("\n");			
		}
		if (this.baggerBag.isNdnp()) {
			content.append(FIELD_NDNP_AWARDEE_PHASE + ": ");
			if (this.getAwardeePhase() != null && !this.getAwardeePhase().isEmpty())
				content.append(this.getAwardeePhase() + "\n");
			else
				content.append("\n");			
		}
		if (!this.baggerBag.isNoProject()) {
			content.append(FIELD_LC_PROJECT + ": ");
			if (this.getLcProject() != null && !this.getLcProject().isEmpty())
				content.append(this.getLcProject() + "\n");
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
		BagInfoTxt bagInfoTxt = baggerBag.getBag().getBagInfoTxt();
		if (bagInfoTxt != null) {
			Set<String> keys = bagInfoTxt.keySet();
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
		    		field.setValue(baggerBag.getProject().getName());
		    		field.isEnabled(false);
		    	} else if (label.equalsIgnoreCase(DefaultBagInfo.FIELD_BAGGING_DATE)) {
		    		field.setValue(DefaultBagInfo.getTodaysDate());
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
		} else {
			field.isEnabled(enabled);					
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
		return methodName;
	}

	// TODO: return standard fields and project specific fields
	public List<String> getStandardBagFields() {
		ArrayList<String> list = new ArrayList<String>();

        list.add("");
		if (this.baggerBag.isEdeposit()) {
	        list.add(FIELD_EDEPOSIT_PUBLISHER);
		} else if (this.baggerBag.isNdnp()) {
	        list.add(FIELD_NDNP_AWARDEE_PHASE);
		}
		if (!this.baggerBag.isNoProject()) {
	        list.add(FIELD_LC_PROJECT);
		}

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
		return list;
	}

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
		} catch (Exception e) {
			log.error("getFieldList: " + e);
		}

		log.info("DefaultBagInfo.getStandardBagFields: " + standardFields.size());
		return standardFields;
	}
}
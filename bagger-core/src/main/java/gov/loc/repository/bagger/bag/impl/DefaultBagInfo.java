package gov.loc.repository.bagger.bag.impl;

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

	protected DefaultBag baggerBag;
	private String bagName = new String();
	private BaggerOrganization baggerOrganization = new BaggerOrganization();
	private HashMap<String, BagInfoField> profileMap = new HashMap<String, BagInfoField>();
	private HashMap<String, BagInfoField> fieldMap = new HashMap<String, BagInfoField>();
	public static final String[] profileStrings = {FIELD_SOURCE_ORGANIZATION, FIELD_ORGANIZATION_ADDRESS, FIELD_CONTACT_NAME, FIELD_CONTACT_PHONE, FIELD_CONTACT_EMAIL};
	public static final HashSet<String> profileSet = new HashSet<String>(Arrays.asList(profileStrings));
	public static final String[] readOnlyStrings = {FIELD_PAYLOAD_OXUM, FIELD_LC_PROJECT};
	public static final HashSet<String> readOnlySet = new HashSet<String>(Arrays.asList(readOnlyStrings));
	public static final String[] textAreaStrings = {FIELD_EXTERNAL_DESCRIPTION, FIELD_INTERNAL_SENDER_DESCRIPTION};
	public static final HashSet<String> textAreaSet = new HashSet<String>(Arrays.asList(textAreaStrings));
	public static final String[] requiredStrings = {FIELD_EXTERNAL_DESCRIPTION, FIELD_BAGGING_DATE, FIELD_EXTERNAL_IDENTIFIER, FIELD_BAG_SIZE, FIELD_EDEPOSIT_PUBLISHER, FIELD_NDNP_AWARDEE_PHASE, FIELD_LC_PROJECT};
	public static final HashSet<String> requiredSet = new HashSet<String>(Arrays.asList(requiredStrings));

	private String name;
	private String content;
	private String publisher = "";
	private String awardeePhase = "";
	private String lcProject = "";

	public DefaultBagInfo(DefaultBag baggerBag) {
		super(baggerBag.getBag().getBagConstants());
		this.setBagName(baggerBag.getName());
		this.baggerBag = baggerBag;
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
		if (this.baggerBag.getIsEdeposit()) {
			if (bagInfo.getPublisher() != null && !bagInfo.getPublisher().isEmpty())
				this.setPublisher(bagInfo.getPublisher());
			else
				this.setPublisher("");			
		}
		if (this.baggerBag.getIsNdnp()) {
			if (bagInfo.getAwardeePhase() != null && !bagInfo.getAwardeePhase().isEmpty())
				this.setAwardeePhase(bagInfo.getAwardeePhase());
			else
				this.setAwardeePhase("");			
		}
		if (!this.baggerBag.getIsNoProject()) {
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
		if (this.baggerBag.getIsEdeposit()) {
			content.append(FIELD_EDEPOSIT_PUBLISHER + ": ");
			if (this.getPublisher() != null && !this.getPublisher().isEmpty())
				content.append(this.getPublisher() + "\n");
			else
				content.append("\n");			
		}
		if (this.baggerBag.getIsNdnp()) {
			content.append(FIELD_NDNP_AWARDEE_PHASE + ": ");
			if (this.getAwardeePhase() != null && !this.getAwardeePhase().isEmpty())
				content.append(this.getAwardeePhase() + "\n");
			else
				content.append("\n");			
		}
		if (!this.baggerBag.getIsNoProject()) {
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
				BagInfoField field = createField(label, enabled);
				if (profileSet.contains(label)) {
					if (profileMap.isEmpty() || !profileMap.containsKey(label)) profileMap.put(label, field);
				} else {
					if (fieldMap.isEmpty() || !fieldMap.containsKey(label)) fieldMap.put(label, field);
				}
			}
		}
	}
	
	public void createStandardFieldMap(boolean enabled) {
		if (this.fieldMap == null) this.fieldMap = new HashMap<String, BagInfoField>();
		BagInfoTxt bagInfoTxt = baggerBag.getBag().getBagInfoTxt();
		if (bagInfoTxt == null) bagInfoTxt = baggerBag.getBag().getBagPartFactory().createBagInfoTxt();
		List<String> ls = bagInfoTxt.getStandardFields();
		for (int i=0; i<ls.size(); i++) {
			String label = ls.get(i);
			label = label.trim();
			if (!profileSet.contains(label)) {
				BagInfoField field = createField(label, enabled);
				if (fieldMap.isEmpty() || !fieldMap.containsKey(label)) {
					fieldMap.put(label, field);
				}
		    	if (label.equalsIgnoreCase(DefaultBagInfo.FIELD_LC_PROJECT)) {
		    		field.setValue(baggerBag.getProject().getName());
		    		field.isEnabled(false);
		    	} else if (label.equalsIgnoreCase(DefaultBagInfo.FIELD_BAGGING_DATE)) {
		    		field.setValue(DefaultBagInfo.getTodaysDate());
		    	} else if (DefaultBagInfo.readOnlySet.contains(label)) {
		    		field.isEnabled(false);
		    	}
			}
		}
	}
	
	public void createProfileFieldList(boolean enabled) {
		BagInfoTxt bagInfoTxt = baggerBag.getBag().getBagInfoTxt();
		if (bagInfoTxt == null) bagInfoTxt = baggerBag.getBag().getBagPartFactory().createBagInfoTxt();
		List<String> ls = bagInfoTxt.getStandardFields();
		for (int i=0; i<ls.size(); i++) {
			String label = ls.get(i);
			label = label.trim();
			if (profileSet.contains(label)) {
				BagInfoField field = createField(label, enabled);
				if (profileMap.isEmpty() || !this.profileMap.containsKey(label)) this.profileMap.put(label, field);
			}
		}
	}
	
	public List<BagInfoField> createNonStandardFieldList(boolean enabled) {
		ArrayList<BagInfoField> list = new ArrayList<BagInfoField>();

		BagInfoTxt bagInfoTxt = baggerBag.getBag().getBagInfoTxt();
		if (bagInfoTxt == null) bagInfoTxt = baggerBag.getBag().getBagPartFactory().createBagInfoTxt();
		List<String> nls = bagInfoTxt.getNonstandardFields();
		for (int i=0; i<nls.size(); i++) {
			String label = nls.get(i);
			//log.info("createNonStandardFieldList non-stnd: " + getMethodFromLabel(label));
		}

        if (baggerBag.getIsEdeposit()) {
    		BagInfoField publisher = createField("publisher", enabled);
    		list.add(publisher);
        }
        if (baggerBag.getIsNdnp()) {
    		BagInfoField awardeePhase = createField("awardeePhase", enabled);
    		list.add(awardeePhase);
        }
        if (!baggerBag.getIsNoProject()) {
    		BagInfoField lcProject = createField("lcProject", enabled);
    		list.add(lcProject);
        }
		return list;
	}

	public void updateExistingFieldList(boolean enabled) {
		BagInfoTxt bagInfoTxt = baggerBag.getBag().getBagInfoTxt();
		List<String>ls = bagInfoTxt.getStandardFields();
		for (int i=0; i<ls.size(); i++) {
			String label = ls.get(i);
			label = label.trim();
			if (!profileSet.contains(label)) {
				BagInfoField field = createField(label, enabled);
				if (fieldMap.isEmpty() || !fieldMap.containsKey(label)) fieldMap.put(label, field);
			}
		}
		// TODO: if project has changed, remove old project fields
		// and add new project fields based on profile
        if (baggerBag.getIsEdeposit()) {
    		BagInfoField publisher = createField("publisher", enabled);
    		fieldMap.put("publisher", publisher);
        }
        if (baggerBag.getIsNdnp()) {
    		BagInfoField awardeePhase = createField("awardeePhase", enabled);
    		fieldMap.put("awardeePhase", awardeePhase);
        }
        if (!baggerBag.getIsNoProject()) {
    		BagInfoField lcProject = createField("lcProject", enabled);
    		fieldMap.put("lcProject", lcProject);
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
		field.isRequired(true);

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
	public List<String> getStandardFields() {
		ArrayList<String> list = new ArrayList<String>();

        list.add("");
        list.add(FIELD_NEW_COMPONENT);
		if (this.baggerBag.getIsEdeposit()) {
	        list.add(FIELD_EDEPOSIT_PUBLISHER);
		} else if (this.baggerBag.getIsNdnp()) {
	        list.add(FIELD_NDNP_AWARDEE_PHASE);
		}
		if (!this.baggerBag.getIsNoProject()) {
	        list.add(FIELD_LC_PROJECT);
		}

        BagInfoTxt bagInfoTxt = baggerBag.getBag().getBagInfoTxt();
		if (bagInfoTxt == null) bagInfoTxt = baggerBag.getBag().getBagPartFactory().createBagInfoTxt();
		List<String> ls = bagInfoTxt.getStandardFields();
		for (int i=0; i<ls.size(); i++) {
			String label = ls.get(i);
			label = label.trim();
			if (!profileSet.contains(label)) {
				list.add(label);
			}
		}
		return list;
	}
}
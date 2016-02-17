package gov.loc.repository.bagger.bag.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.Organization;
import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.ProfileField;
import gov.loc.repository.bagger.bag.BagInfoField;
import gov.loc.repository.bagger.bag.BaggerSourceOrganization;
import gov.loc.repository.bagger.profile.BaggerProfileStore;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.impl.BagInfoTxtImpl;

public class DefaultBagInfo implements Serializable {
  private static final long serialVersionUID = 1L;

  protected static final Logger log = LoggerFactory.getLogger(DefaultBagInfo.class);

  public static final String FIELD_LC_PROJECT = "Profile Name";

  private static final String[] ORGANIZATION_CONTACT_FIELDS = { BagInfoTxtImpl.FIELD_SOURCE_ORGANIZATION, BagInfoTxtImpl.FIELD_ORGANIZATION_ADDRESS,
      BagInfoTxtImpl.FIELD_CONTACT_NAME, BagInfoTxtImpl.FIELD_CONTACT_PHONE, BagInfoTxtImpl.FIELD_CONTACT_EMAIL, Contact.FIELD_TO_CONTACT_NAME,
      Contact.FIELD_TO_CONTACT_PHONE, Contact.FIELD_TO_CONTACT_EMAIL };

  private static final HashSet<String> ORGANIZATION_CONTACT_FIELD_SET = new HashSet<String>(Arrays.asList(ORGANIZATION_CONTACT_FIELDS));

  private BaggerSourceOrganization sourceOrganization = new BaggerSourceOrganization();
  private Contact toContact = new Contact(true);
  private LinkedHashMap<String, BagInfoField> fieldMap = new LinkedHashMap<>();

  public BaggerSourceOrganization getBagOrganization() {
    return this.sourceOrganization;
  }

  public HashMap<String, BagInfoField> getFieldMap() {
    return this.fieldMap;
  }

  public void addField(BagInfoField field) {
    fieldMap.put(field.getName(), field);
  }

  public void update(BagInfoTxt bagInfoTxt) {
    updateBagInfoFieldMapFromBilBag(bagInfoTxt);
    sourceOrganization = new BaggerSourceOrganization(bagInfoTxt);
    toContact = new Contact(true);
    if (bagInfoTxt.containsKey(Contact.FIELD_TO_CONTACT_NAME)) {
      toContact.setContactName(ProfileField.createProfileField(Contact.FIELD_TO_CONTACT_NAME, bagInfoTxt.get(Contact.FIELD_TO_CONTACT_NAME)));
    }
    else {
      toContact.setContactName(ProfileField.createProfileField(Contact.FIELD_TO_CONTACT_NAME, ""));
    }

    if (bagInfoTxt.containsKey(Contact.FIELD_TO_CONTACT_PHONE)) {
      toContact.setTelephone(ProfileField.createProfileField(Contact.FIELD_TO_CONTACT_PHONE, bagInfoTxt.get(Contact.FIELD_TO_CONTACT_PHONE)));
    }
    else {
      toContact.setTelephone(ProfileField.createProfileField(Contact.FIELD_TO_CONTACT_PHONE, ""));
    }

    if (bagInfoTxt.containsKey(Contact.FIELD_TO_CONTACT_EMAIL)) {
      toContact.setEmail(ProfileField.createProfileField(Contact.FIELD_TO_CONTACT_EMAIL, bagInfoTxt.get(Contact.FIELD_TO_CONTACT_EMAIL)));
    }
    else {
      toContact.setEmail(ProfileField.createProfileField(Contact.FIELD_TO_CONTACT_EMAIL, ""));
    }

    for (String key : bagInfoTxt.keySet()) {
      BagInfoField infoField = new BagInfoField();
      infoField.setLabel(key);
      infoField.setName(key);
      infoField.setValue(bagInfoTxt.get(key));
      infoField.isEditable(true);
      infoField.isEnabled(true);
      fieldMap.put(key, infoField);
    }

  }

  private void updateBagInfoFieldMapFromBilBag(BagInfoTxt bagInfoTxt) {
    if (fieldMap != null) {
      for(Entry<String, BagInfoField> entry : fieldMap.entrySet()){
        String value = bagInfoTxt.get(entry.getKey());
        entry.getValue().setValue(value);
      }
    }
  }

  public void setProfile(Profile profile, boolean newBag) {
    if (newBag) {
      // if this is a new bag, populate organization and contacts with profile
      // info
      Contact person = profile.getSendToContact();
      if (person == null){
        person = new Contact(true);
      }
      Contact contact = profile.getSendFromContact();
      if (contact == null) {
        contact = new Contact(false);
      }
      sourceOrganization.setContact(contact);
      Organization org = profile.getOrganization();
      if (org == null){
        org = new Organization();
      }
      sourceOrganization.setOrganizationName(org.getName().getFieldValue());
      sourceOrganization.setOrganizationAddress(org.getAddress().getFieldValue());

      this.toContact = person;
    }

    applyProfileToFieldMap(profile);
  }

  private void applyProfileToFieldMap(Profile profile) {
    if (profile != null) {
      if (profile.isNoProfile()) {
        if (fieldMap.containsKey(DefaultBagInfo.FIELD_LC_PROJECT)) {
          fieldMap.remove(DefaultBagInfo.FIELD_LC_PROJECT);
        }
      }else{
        BagInfoField field = new BagInfoField();
        field.setLabel(DefaultBagInfo.FIELD_LC_PROJECT);
        field.setName(DefaultBagInfo.FIELD_LC_PROJECT);
        field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
        field.isEnabled(false);
        field.isEditable(false);
        field.isRequiredvalue(true);
        field.isRequired(true);
        field.setValue(profile.getName());
        field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
        fieldMap.put(field.getLabel(), field);
      }

      List<ProfileField> list = BaggerProfileStore.getInstance().getProfileFields(profile.getName());
      LinkedHashMap<String, ProfileField> profileFields = convertToMap(list);

      if (fieldMap.size() > 0) {
        for (BagInfoField field : fieldMap.values()) {
          ProfileField projectProfile = profileFields.get(field.getLabel());
          if (projectProfile == null){ continue; }

          field.isEnabled(!projectProfile.isReadOnly());
          field.isEditable(!projectProfile.isReadOnly());
          field.isRequiredvalue(projectProfile.getIsValueRequired());
          field.isRequired(projectProfile.getIsRequired());
          // field.setValue(projectProfile.getFieldValue());
          field.buildElements(projectProfile.getElements());
          if (projectProfile.getFieldType().equalsIgnoreCase(BagInfoField.TEXTFIELD_CODE)) {
            field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
          }
          else if (projectProfile.getFieldType().equalsIgnoreCase(BagInfoField.TEXTAREA_CODE)) {
            field.setComponentType(BagInfoField.TEXTAREA_COMPONENT);
          }
          else if (!(projectProfile.getElements().isEmpty())) {
            field.setComponentType(BagInfoField.LIST_COMPONENT);
          }
        }
      }

      HashMap<String, ProfileField> exclusiveProfileFields = new HashMap<String, ProfileField>();
      exclusiveProfileFields.putAll(profileFields);
      exclusiveProfileFields.keySet().removeAll(fieldMap.keySet());

      if (exclusiveProfileFields.size() > 0) {
        for (ProfileField profileField : exclusiveProfileFields.values()) {
          ProfileField projectProfile = profileField;
          if (projectProfile != null) {
            BagInfoField field = new BagInfoField();
            field.setLabel(projectProfile.getFieldName());
            field.setName(field.getLabel());
            field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
            field.isEnabled(!projectProfile.isReadOnly());
            field.isEditable(!projectProfile.isReadOnly());
            field.isRequiredvalue(projectProfile.getIsValueRequired());
            field.isRequired(projectProfile.getIsRequired());
            field.setValue(projectProfile.getFieldValue());
            // field.setValue("");
            if (projectProfile.isReadOnly()){ field.isEnabled(false); }
            field.buildElements(projectProfile.getElements());
            if (projectProfile.getFieldType().equalsIgnoreCase(BagInfoField.TEXTFIELD_CODE)) {
              field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
            }
            else if (projectProfile.getFieldType().equalsIgnoreCase(BagInfoField.TEXTAREA_CODE)) {
              field.setComponentType(BagInfoField.TEXTAREA_COMPONENT);
            }
            else if (!(projectProfile.getElements().isEmpty())) {
              field.setComponentType(BagInfoField.LIST_COMPONENT);
            }
            fieldMap.put(field.getLabel(), field);
          }
        }
      }
    }
  }

  public LinkedHashMap<String, ProfileField> convertToMap(List<ProfileField> profileFields) {
    LinkedHashMap<String, ProfileField> filedsToReturn = new LinkedHashMap<String, ProfileField>();
    if (profileFields == null){
      return filedsToReturn;
    }
    for (ProfileField profileFiled : profileFields) {
      filedsToReturn.put(profileFiled.getFieldName(), profileFiled);
    }
    return filedsToReturn;
  }

  public void clearFields() {
    fieldMap = new LinkedHashMap<String, BagInfoField>();
  }

  public void removeField(String key) {
    fieldMap.remove(key);
  }

  public static boolean isOrganizationContactField(String fieldName) {
    return ORGANIZATION_CONTACT_FIELD_SET.contains(fieldName);
  }

  public void prepareBilBagInfo(BagInfoTxt bagInfoTxt) {
    bagInfoTxt.clear();

    for (Map.Entry<String, BagInfoField> entry : fieldMap.entrySet()) {
      bagInfoTxt.put(entry.getKey(), entry.getValue().getValue());
    }

    updateBagInfoTxtWithOrganizationInformation(bagInfoTxt);
  }

  private void updateBagInfoTxtWithOrganizationInformation(BagInfoTxt bagInfoTxt) {
    if (!sourceOrganization.getOrganizationName().trim().isEmpty()) {
      bagInfoTxt.setSourceOrganization(sourceOrganization.getOrganizationName().trim());
    }
    if (!sourceOrganization.getOrganizationAddress().trim().isEmpty()) {
      bagInfoTxt.setOrganizationAddress(sourceOrganization.getOrganizationAddress().trim());
    }
    Contact contact = sourceOrganization.getContact();
    if (!contact.getContactName().getFieldValue().trim().isEmpty()) {
      bagInfoTxt.setContactName(contact.getContactName().getFieldValue().trim());
    }
    if (!contact.getTelephone().getFieldValue().trim().isEmpty()) {
      bagInfoTxt.setContactPhone(contact.getTelephone().getFieldValue().trim());
    }
    if (!contact.getEmail().getFieldValue().trim().isEmpty()) {
      bagInfoTxt.setContactEmail(contact.getEmail().getFieldValue().trim());
    }
    if (!toContact.getContactName().getFieldValue().trim().isEmpty()) {
      bagInfoTxt.put(Contact.FIELD_TO_CONTACT_NAME, toContact.getContactName().getFieldValue());
    }
    if (!toContact.getTelephone().getFieldValue().trim().isEmpty()) {
      bagInfoTxt.put(Contact.FIELD_TO_CONTACT_PHONE, toContact.getTelephone().getFieldValue().trim());
    }
    if (!toContact.getEmail().getFieldValue().trim().isEmpty()) {
      bagInfoTxt.put(Contact.FIELD_TO_CONTACT_EMAIL, toContact.getEmail().getFieldValue().trim());
    }

  }

  public Contact getToContact() {
    return toContact;
  }

  public void setToContact(Contact toContact) {
    this.toContact = toContact;
  }

  public void update(Map<String, String> map) {
    for(Entry<String, String> entry : map.entrySet()){
      if(fieldMap.get(entry.getKey()) != null){
        fieldMap.get(entry.getKey()).setValue(entry.getValue());
      }
    }
  }

}
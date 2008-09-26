package gov.loc.repository.bagger.bag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.core.style.ToStringCreator;

/**
 *
 * @author Jon Steinbach
 */
public class BagInfo {
	private static final Log log = LogFactory.getLog(BagInfo.class);

	private String externalDescription = "The collection 'Local Davis Flood Control Collection' includes captured " +
			"California State and local websites containing information on flood control resources for " +
			"the Davis and Sacramento area.  Sites were captured by UC Davis curator Wrigley Spyder using" +
			" the Web Archiving Service in February 2007 and October 2007.";

	private String packingDate = "2008.04.15";

	private String externalIdentifier = "spengler_yoshimuri_001";

	private String bagSize = "22Gb";

	private String bagGroupIdentifier = "spengler_yoshimuri";

	private String bagCount = "1 of 15";

	private String internalSenderIdentifier = "UCDL";

	private String internalSenderDescription = "University of California Davis Libraries";

	public void setExternalDescription(String description) {
		this.externalDescription = description;
	}

	public String getExternalDescription() {
		return this.externalDescription;
	}

	public void setPackingDate(String deliveryDate) {
		this.packingDate = deliveryDate;
	}

	public String getPackingDate() {
		return this.packingDate;
	}

	public void setExternalIdentifier(String externalIdentifier) {
		this.externalIdentifier = externalIdentifier;
	}

	public String getExternalIdentifier() {
		return this.externalIdentifier;
	}

	public void setBagSize(String bagSize) {
		this.bagSize = bagSize;
	}

	public String getBagSize() {
		return this.bagSize;
	}

	public void setBagGroupIdentifier(String bagGroupIdentifier) {
		this.bagGroupIdentifier = bagGroupIdentifier;
	}

	public String getBagGroupIdentifier() {
		return this.bagGroupIdentifier;
	}

	public void setBagCount(String bagCount) {
		this.bagCount = bagCount;
	}

	public String getBagCount() {
		return this.bagCount;
	}

	public void setInternalSenderIdentifier(String internalSenderIdentifier) {
		this.internalSenderIdentifier = internalSenderIdentifier;
	}

	public String getInternalSenderIdentifier() {
		return this.internalSenderIdentifier;
	}

	public void setInternalSenderDescription(String internalSenderDescription) {
		this.internalSenderDescription = internalSenderDescription;
	}

	public String getInternalSenderDescription() {
		return this.internalSenderDescription;
	}
}

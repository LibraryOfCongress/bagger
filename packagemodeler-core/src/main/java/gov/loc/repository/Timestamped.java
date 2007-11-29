package gov.loc.repository;

import java.util.Date;

public interface Timestamped {
	public Date getCreateTimestamp();
	
	public Date getUpdateTimestamp();
}

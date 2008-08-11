package gov.loc.repository.exceptions;

public class ConfigurationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ConfigurationException(String msg) {
		super(msg);
	}
	
	public ConfigurationException(Throwable ex)
	{
		super(ex);
	}
	
}

package gov.loc.repository.results;

public class SimpleResult {
	private boolean isSuccess;
	private String message = null;
	
	public SimpleResult(boolean isSuccess) {
		this.isSuccess = isSuccess;			
	}

	public SimpleResult(boolean isSuccess, String message) {
		this.isSuccess = isSuccess;
		this.message = message;
	}
			
	public boolean isSuccess()
	{
		return this.isSuccess;
	}
	
	public String getMessage()
	{
		return this.message;
	}

}

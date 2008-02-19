package gov.loc.repository.transfer.ui.springframework;

public class ModelAndView extends org.springframework.web.servlet.ModelAndView {
	private String msg = null;
	private Integer sc = null;
	
	public void setError(int sc)
	{
		this.sc = sc;
	}
	
	public void setError(int sc, String msg)
	{
		this.sc = sc;
		this.msg = msg;
	}
	
	public Integer getStatusCode()
	{
		return this.sc;
	}
	
	public String getMessage()
	{
		return this.msg;
	}
}

package gov.loc.repository.console.taskInstance;

import org.springframework.web.servlet.ModelAndView;

public class ConfigurableTaskInstanceUpdateCommand extends
		DefaultTaskInstanceUpdateCommand {
	
	private String formViewName = null;
	private String instructionViewName = null;
	
	public void setFormViewName(String formViewName)
	{
		this.formViewName = formViewName;
	}
	
	public void setInstructionViewName(String instructionViewName)
	{
		this.instructionViewName = instructionViewName;
	}
	
	@Override
	public ModelAndView prepareForm() throws Exception {
		if (formViewName != null)
		{
			return new ModelAndView(formViewName);
		}
		return super.prepareForm();
	}
	
	@Override
	public ModelAndView prepareInstruction() throws Exception {
		if (instructionViewName != null)
		{
			return new ModelAndView(instructionViewName);
		}
		return super.prepareInstruction();
	}
}

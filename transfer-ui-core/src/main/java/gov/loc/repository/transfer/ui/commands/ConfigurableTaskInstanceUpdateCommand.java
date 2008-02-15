package gov.loc.repository.transfer.ui.commands;

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
	public void prepareForm() throws Exception {
		if (formViewName != null)
		{
			this.mav.addObject("formViewName", formViewName);
		}
		else
		{
			super.prepareForm();
		}
	}
	
	@Override
	public void prepareInstruction() throws Exception {
		if (instructionViewName != null)
		{
			this.mav.addObject("instructionViewName", instructionViewName);
		}
		else
		{
			super.prepareInstruction();
		}
	}
}

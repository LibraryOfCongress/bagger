package gov.loc.repository.console.taskInstance.ndnp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import gov.loc.repository.console.taskInstance.DefaultTaskInstanceUpdateCommand;
import gov.loc.repository.packagemodeler.batch.dao.BatchModelDAO;
import gov.loc.repository.utilities.ndnp.BatchCharacterization;
import gov.loc.repository.utilities.ndnp.BatchHelper;

public class RegisterBatchTaskInstanceUpdateCommand extends
		DefaultTaskInstanceUpdateCommand {

	private BatchModelDAO dao;
	
	@SuppressWarnings("unchecked")
	public ModelAndView prepareForm() throws Exception {
		Map model = new HashMap();

		model.put("awardeeList", dao.findRequiredRole("ndnp_awardee").getAgentSet());
		
		return new ModelAndView(".taskinstance.form.ndnp1.registerbatch", model);
	}

	@Override
	public ModelAndView prepareInstruction() throws Exception {		
		return new ModelAndView(".taskinstance.instruction.ndnp1.registerbatch");
	}
	
	public void setDao(BatchModelDAO dao)
	{
		this.dao = dao;
	}
	
	@Override
	public void preprocessPut() throws Exception {
		if (! (request instanceof MultipartHttpServletRequest))
		{
			return;
		}

		MultipartFile file = ((MultipartHttpServletRequest)request).getFile("file");
		if (file != null && ! file.isEmpty())
		{
			BatchCharacterization characterization = BatchHelper.characterize(file.getInputStream());
			if (characterization.getPackageId() != null)
			{
				additionalParameterMap.put("packageId", characterization.getPackageId());
			}
			if (characterization.getLccnList() != null && ! characterization.getLccnList().isEmpty())
			{
				additionalParameterMap.put("lccnList", characterization.getLccnList());
			}
			if (characterization.getReelNumberList() != null && ! characterization.getReelNumberList().isEmpty())
			{
				additionalParameterMap.put("reelNumberList", characterization.getReelNumberList());
			}
			log.debug(additionalParameterMap.size());
		}
	}
		
}

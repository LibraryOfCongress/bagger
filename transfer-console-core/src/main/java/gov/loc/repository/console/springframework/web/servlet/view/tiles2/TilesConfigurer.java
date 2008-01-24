package gov.loc.repository.console.springframework.web.servlet.view.tiles2;

import java.io.File;
import java.util.ArrayList;

import javax.servlet.ServletContext;

import org.apache.tiles.TilesException;
import org.springframework.util.PatternMatchUtils;

public class TilesConfigurer extends
		org.springframework.web.servlet.view.tiles2.TilesConfigurer {
	private String pattern = "*.*";
	private File contextDir;
	private String definitionPath = "/WEB-INF";
	
	public void setDefinitionPattern(String pattern)
	{
		this.pattern = pattern;
	}
	
	public void setDefinitionPath(String definitionPath)
	{
		this.definitionPath = definitionPath;
	}
	
	@Override
	public void setServletContext(ServletContext context) {
		this.contextDir = new File(context.getRealPath("."));
		super.setServletContext(context);
	}
	
	@Override
	public void afterPropertiesSet() throws TilesException {
		
		ArrayList<String> definitionList = new ArrayList<String>();
		File definitionDir = new File(this.contextDir, this.definitionPath);
		for(File file : definitionDir.listFiles())
		{
			if (PatternMatchUtils.simpleMatch(this.pattern, file.getName()))
			{
				String definition = this.definitionPath;
				if (! this.definitionPath.endsWith("/"))
				{
					definition += "/";
				}
				definition += file.getName();
				this.logger.debug("Discovered tiles definition: " + definition);
				definitionList.add(definition);
			}
			
		}
		this.setDefinitions(definitionList.toArray(new String[0]));
		
		super.afterPropertiesSet();
	}
}

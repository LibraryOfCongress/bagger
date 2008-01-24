package gov.loc.repository.console.taskInstance;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("hiding")
public class TaskInstanceUpdateCommandMap<String,TaskInstanceUpdateCommand> extends HashMap<String,TaskInstanceUpdateCommand> {

	private static final long serialVersionUID = 1L;

	public void setMap(Map<String,TaskInstanceUpdateCommand> map)
	{
		this.putAll(map);
	}
}

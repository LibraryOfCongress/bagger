package gov.loc.repository.transfer.ui.model;

import java.util.Date;

import org.jbpm.graph.exe.Comment;

public class CommentBean extends AbstractWorkflowBean {
	private Comment comment;

	public void setComment(Comment comment)
	{
		this.comment = comment;
	}
	
	public String getMessage()
	{
		return comment.getMessage();
	}

	public Date getDate()
	{
		return comment.getTime();
	}
	
	public String getActorId()
	{
		return comment.getActorId();
	}
	
}

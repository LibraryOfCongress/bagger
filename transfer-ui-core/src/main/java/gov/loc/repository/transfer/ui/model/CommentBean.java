package gov.loc.repository.transfer.ui.model;

import java.util.Date;

import org.jbpm.graph.exe.Comment;

public class CommentBean extends AbstractWorkflowBean {
	private Comment comment;

	void setComment(Comment comment)
	{
		this.comment = comment;
	}
		
	public String getMessage()
	{
		return comment.getMessage();
	}

	public Date getCreateDate()
	{
		return comment.getTime();
	}
	
	public UserBean getUserBean()
	{
		return this.factory.createUserBean(comment.getActorId());
	}
	
	public String getId() {
		return Long.toString(comment.getId());
	}
	
	public String getName() {
		return "Comment " + getId();
	}
}

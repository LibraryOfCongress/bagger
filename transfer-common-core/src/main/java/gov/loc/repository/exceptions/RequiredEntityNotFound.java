package gov.loc.repository.exceptions;

public class RequiredEntityNotFound extends Exception {

	private static final long serialVersionUID = 1L;

	public RequiredEntityNotFound(String entity) {
		super("Required entity not found:" + entity);
	}
	
}

package cz.anophel.resharer.utils;

/**
 * General exception for ReSharer.
 * 
 * @author Patrik Vesely
 *
 */
public class ResharerException extends Exception {

	private static final long serialVersionUID = -3238876931534004414L;

	public ResharerException() {}
	
	public ResharerException(String message) {
		super(message);
	}
	
	public ResharerException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ResharerException(Throwable cause) {
		super(cause);
	}
	
}

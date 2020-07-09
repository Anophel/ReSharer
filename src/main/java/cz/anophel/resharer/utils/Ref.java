package cz.anophel.resharer.utils;

/**
 * Utility class for reference holding.
 * 
 * @author Patrik Vesely
 *
 * @param <T>
 */
public class Ref<T> {

	private T t;

	public Ref() {
		t = null;
	}

	public Ref(T t) {
		this.t = t;
	}
	
	/**
	 * Gets the object the reference pointing at.
	 * 
	 * @return
	 */
	public T get() {
		return t;
	}
	
	/**
	 * Sets the object the reference pointing at.
	 * 
	 * @return
	 */
	public void set(T t) {
		this.t = t;
	}
	
}

package cz.anophel.resharer.utils;

public class Ref<T> {

	private T t;

	public Ref() {
		t = null;
	}

	public Ref(T t) {
		this.t = t;
	}
	
	public T get() {
		return t;
	}

	public void set(T t) {
		this.t = t;
	}
	
}

package net.afterlifelochie.sandbox;

/**
 * Observable is a container for objects which have values for which we care
 * about the contents of, but do not want to cache the old/new values in a
 * non-persistent or non-volatile way.
 * 
 * @author AfterLifeLochie
 * 
 */
public class Observable {

	/**
	 * The modified state.
	 */
	private volatile boolean modified;
	/**
	 * The parent Observable, in the event we are in a List or want to upwardly
	 * notify Observable parent objects.
	 */
	private volatile Observable parent;

	public Observable(Observable parent) {
		this.parent = parent;
	}

	/**
	 * Marks this object as modified. Only accessible to the extending object.
	 */
	protected void modify() {
		modified = true;
		if (parent != null)
			parent.modify();
	}

	/**
	 * Returns the state of this Observable.
	 */
	public boolean modified() {
		return modified;
	}

	/**
	 * Resets the state of this Observable. If the Observable has any meta-data
	 * about the change in the Observable state, it should also be reset.
	 */
	public void clearModified() {
		modified = false;
	}

}

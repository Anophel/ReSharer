package cz.anophel.resharer.fs;

import java.time.LocalDateTime;

/**
 * Interface for resource (e.g. file, directory) descriptor.
 * 
 * @author Patrik Vesely
 *
 */
public interface IDescriptor {
	
	/**
	 * Returns unique id of descriptor in filesystem.
	 * 
	 * @return
	 */
	public long getUid();
	
	/**
	 * Returns the name of resource descriptor.
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Returns enum type of descriptor.
	 * 
	 * @return
	 */
	public DescriptorTypes getType();
	
	/**
	 * Returns LocalDateTime of time, which this object was modified.
	 * 
	 * @return
	 */
	public LocalDateTime getLastModif();
	
	/**
	 * Indicator if this descriptor is a view.
	 * 
	 * @return
	 */
	public boolean isView();
	
}

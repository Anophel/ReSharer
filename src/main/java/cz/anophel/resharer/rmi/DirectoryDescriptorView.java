package cz.anophel.resharer.rmi;

import java.io.Serializable;
import java.time.LocalDateTime;

import cz.anophel.resharer.fs.DescriptorTypes;
import cz.anophel.resharer.fs.DirectoryDescriptor;
import cz.anophel.resharer.fs.IDescriptor;

/**
 * View object for DirectoryDescriptor.
 * 
 * @author Patrik Vesely
 *
 */
public class DirectoryDescriptorView implements IDescriptor, Serializable {

	private static final long serialVersionUID = 1L;

	private long uid;

	private String name;

	private LocalDateTime lastModif;

	public DirectoryDescriptorView(DirectoryDescriptor desc) {
		this.uid = desc.getUid();
		this.name = desc.getName();
		this.lastModif = desc.getLastModif();
	}

	public DirectoryDescriptorView(long uid, String name) {
		this.uid = uid;
		this.name = name;
	}

	@Override
	public long getUid() {
		return uid;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public DescriptorTypes getType() {
		return DescriptorTypes.DIRECTORY;
	}

	@Override
	public LocalDateTime getLastModif() {
		return lastModif;
	}

	@Override
	public boolean isView() {
		return true;
	}

}

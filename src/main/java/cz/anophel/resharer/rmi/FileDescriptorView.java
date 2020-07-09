package cz.anophel.resharer.rmi;

import java.io.Serializable;
import java.time.LocalDateTime;

import cz.anophel.resharer.fs.DescriptorTypes;
import cz.anophel.resharer.fs.FileDescriptor;
import cz.anophel.resharer.fs.IDescriptor;

/**
 * View object for FileDescriptor.
 * 
 * @author Patrik Vesely
 *
 */
public class FileDescriptorView implements IDescriptor, Serializable {

	private static final long serialVersionUID = 1L;

	private long uid;
	
	private String name;
	
	private boolean exists;
	
	private LocalDateTime lastModif;
	
	public FileDescriptorView(FileDescriptor desc) {
		this.uid = desc.getUid();
		this.name = desc.getName();
		this.exists = desc.exists();
		this.lastModif = desc.getLastModif();
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
		return DescriptorTypes.FILE;
	}

	@Override
	public LocalDateTime getLastModif() {
		return lastModif;
	}

	public boolean exists() {
		return exists;
	}

	@Override
	public boolean isView() {
		return true;
	}
	
}

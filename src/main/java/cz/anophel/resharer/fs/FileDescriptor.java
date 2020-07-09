package cz.anophel.resharer.fs;

import java.io.File;
import java.io.Serializable;

/**
 * Implementation of descriptor for files.
 * 
 * @author Patrik Vesely
 *
 */
public class FileDescriptor extends AbstractDescriptor implements Serializable {

	private static final long serialVersionUID = 8216395642710361013L;
	
	private String path;

	public FileDescriptor(String path, long uid, String name) {
		super(uid, name);
		this.path = path;
	}

	public String getPath() {
		return path;
	}
	
	protected void setPath(String path) {
		this.path = path;
		touch();
	}

	public File getFile() {
		touch();
		return new File(path);
	}
	
	public boolean exists() {
		return new File(path).exists();
	}
	
	@Override
	public DescriptorTypes getType() {
		return DescriptorTypes.FILE;
	}

	@Override
	public boolean isView() {
		return false;
	}
	
}

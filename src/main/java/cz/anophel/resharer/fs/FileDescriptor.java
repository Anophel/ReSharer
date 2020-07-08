package cz.anophel.resharer.fs;

import java.io.File;

public class FileDescriptor extends AbstractDescriptor {

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
	
}

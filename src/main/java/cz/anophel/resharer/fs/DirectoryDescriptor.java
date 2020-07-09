package cz.anophel.resharer.fs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.anophel.resharer.utils.MultiListView;

/**
 * Implementation of descriptor for directories.
 * 
 * @author Patrik Vesely
 *
 */
public class DirectoryDescriptor extends AbstractDescriptor implements Serializable {

	private static final long serialVersionUID = -7156621207795143187L;
	
	private List<FileDescriptor> files;
	private List<DirectoryDescriptor> dirs;

	private List<IDescriptor> allDescs;

	public DirectoryDescriptor(long uid, String name) {
		super(uid, name);

		files = new ArrayList<>();
		dirs = new ArrayList<>();

		allDescs = new MultiListView<IDescriptor>(files, dirs);
	}

	@Override
	public DescriptorTypes getType() {
		return DescriptorTypes.DIRECTORY;
	}

	/**
	 * Returns read only list of descriptors in this folder.
	 * 
	 * @return
	 */
	public List<IDescriptor> ls() {
		return allDescs;
	}

	/**
	 * Returns read only list of files in this folder.
	 * 
	 * @return
	 */
	public List<FileDescriptor> lsFiles() {
		return Collections.unmodifiableList(files);
	}

	/**
	 * Returns read only list of directories in this folder.
	 * 
	 * @return
	 */
	public List<DirectoryDescriptor> lsDirs() {
		return Collections.unmodifiableList(dirs);
	}

	/**
	 * Returns a directory descriptor based on its name.
	 * 
	 * @param name
	 * @return
	 */
	public DirectoryDescriptor getDirByName(String name) {
		for (DirectoryDescriptor d : dirs) {
			if (d.getName().equals(name))
				return d;
		}
		return null;
	}

	/**
	 * Return a file descriptor based on its name.
	 * 
	 * @param name
	 * @return
	 */
	public FileDescriptor getFileByName(String name) {
		for (FileDescriptor d : files) {
			if (d.getName().equals(name))
				return d;
		}
		return null;
	}
	
	/**
	 * Adds a descriptor to this directory.
	 * 
	 * @param desc
	 * @return
	 */
	protected boolean addDesc(IDescriptor desc) {
		if (desc == null)
			return false;

		switch (desc.getType()) {
		case FILE:
			return touch(files.add((FileDescriptor) desc));
		case DIRECTORY:
			return touch(dirs.add((DirectoryDescriptor) desc));
		}

		return false;
	}

	/**
	 * Adds a file to this directory.
	 * 
	 * @param desc
	 * @return
	 */
	protected boolean addDesc(FileDescriptor desc) {
		if (desc == null)
			return false;

		return touch(files.add(desc));
	}

	/**
	 * Adds a directory to this directory.
	 * 
	 * @param desc
	 * @return
	 */
	protected boolean addDesc(DirectoryDescriptor desc) {
		if (desc == null)
			return false;

		return touch(dirs.add(desc));
	}

	/**
	 * Removes a descriptor from this directory.
	 * 
	 * @param desc
	 * @return
	 */
	protected boolean removeDesc(IDescriptor desc) {
		if (desc == null)
			return false;

		switch (desc.getType()) {
		case FILE:
			return touch(files.remove((FileDescriptor) desc));
		case DIRECTORY:
			return touch(dirs.remove((DirectoryDescriptor) desc));
		}

		return false;
	}

	/**
	 * Removes a file from this directory.
	 * 
	 * @param desc
	 * @return
	 */
	protected boolean removeDesc(FileDescriptor desc) {
		if (desc == null)
			return false;

		return touch(files.remove(desc));
	}

	/**
	 * Removes a directory from this directory.
	 * 
	 * @param desc
	 * @return
	 */
	protected boolean removeDesc(DirectoryDescriptor desc) {
		if (desc == null)
			return false;

		return touch(dirs.remove(desc));
	}

	@Override
	public boolean isView() {
		return false;
	}
	
}

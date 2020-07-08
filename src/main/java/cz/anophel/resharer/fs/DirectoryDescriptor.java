package cz.anophel.resharer.fs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.anophel.resharer.utils.MultiListView;

public class DirectoryDescriptor extends AbstractDescriptor {

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

	public List<IDescriptor> ls() {
		return allDescs;
	}

	public List<FileDescriptor> lsFiles() {
		return Collections.unmodifiableList(files);
	}

	public List<DirectoryDescriptor> lsDirs() {
		return Collections.unmodifiableList(dirs);
	}

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

	protected boolean addDesc(FileDescriptor desc) {
		if (desc == null)
			return false;

		return touch(files.add(desc));
	}

	protected boolean addDesc(DirectoryDescriptor desc) {
		if (desc == null)
			return false;

		return touch(dirs.add(desc));
	}

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
	
	protected boolean removeDesc(FileDescriptor desc) {
		if (desc == null)
			return false;

		return touch(files.remove(desc));
	}
	
	protected boolean removeDesc(DirectoryDescriptor desc) {
		if (desc == null)
			return false;

		return touch(dirs.remove(desc));
	}
	
}

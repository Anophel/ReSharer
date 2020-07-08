package cz.anophel.resharer.fs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystem {

	private DirectoryDescriptor root;

	private long uidQueue;

	public FileSystem() {
		uidQueue = 0;
		root = new DirectoryDescriptor(uidQueue, "root");
	}

	public DirectoryDescriptor getRoot() {
		return root;
	}

	public boolean addVirtualDir(DirectoryDescriptor dir, String name) {
		DirectoryDescriptor fd = new DirectoryDescriptor(getNextId(), name);
		return dir.addDesc(fd);
	}
	
	public boolean addDescriptor(DirectoryDescriptor dir, Path path, int maxDepth) throws IOException {
		return addDescriptor(dir, path.toFile(), maxDepth);
	}

	public boolean addDescriptor(DirectoryDescriptor dir, File file, int maxDepth) throws IOException {
		// Do not follow symlinks
		if (Files.isSymbolicLink(file.toPath()))
			return false;

		if (file.isFile()) { // Add file
			FileDescriptor fd = new FileDescriptor(file.getAbsolutePath(), getNextId(), file.getName());
			return dir.addDesc(fd);
		} else if (file.isDirectory()) { // Add directory
			DirectoryDescriptor fd = new DirectoryDescriptor(getNextId(), file.getName());
			if (dir.addDesc(fd)) {
				boolean result = true;
				if (maxDepth > 0) {
					// Recursively add other files from directory
					for (var path : file.list()) {
						result &= addDescriptor(fd, new File(path), maxDepth - 1);
					}
				}
				return result;
			} else {
				return false;
			}
		}

		return false;
	}

	public boolean removeDescriptor(DirectoryDescriptor dir, IDescriptor desc) {
		return dir.removeDesc(desc);
	}
	
	private long getNextId() {
		return uidQueue++;
	}
}

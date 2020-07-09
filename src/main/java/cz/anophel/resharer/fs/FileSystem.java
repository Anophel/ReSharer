package cz.anophel.resharer.fs;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cz.anophel.resharer.rmi.DirectoryDescriptorView;
import cz.anophel.resharer.rmi.FileDescriptorView;

/**
 * Main class of virtual filesystem.
 * 
 * @author Patrik Vesely
 *
 */
public class FileSystem implements Serializable {

	private static final long serialVersionUID = -3399714840539314314L;

	/**
	 * DirectoryDescriptor for virtual root directory
	 */
	private DirectoryDescriptor root;

	/**
	 * UID of virtual root directory
	 */
	private Long rootUID = 0L;
	
	/**
	 * DirectoryDescriptor for virtual root directory
	 */
	private DirectoryDescriptor jobResults;

	/**
	 * UID of virtual jobResults directory
	 */
	private Long jobResultsUID = rootUID + 1;

	/**
	 * Table of all descriptors by their uid.
	 */
	private Map<Long, IDescriptor> descriptorTable;

	/**
	 * Next unique id for a descriptor.
	 */
	private long uidQueue;

	public FileSystem() {
		uidQueue = rootUID;
		root = new DirectoryDescriptor(getNextId(), "root");
		descriptorTable = new HashMap<>();
		addToTable(root);
		
		// Virtual directory for job results
		try {
			File jobRes = new File(getJobResultsPath());
			jobRes.mkdirs();
			addDescriptor(root, jobRes, 10);
			jobResults = (DirectoryDescriptor) descriptorTable.get(jobResultsUID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a path to directory, where output files from jobs
	 * are saved.
	 * 
	 * @return
	 */
	public String getJobResultsPath() {
		return System.getProperty("user.home") + File.separator + "ReshererJobResults";
	}
	
	/**
	 * Adds descriptor to the descriptor table.
	 * 
	 * @param desc
	 */
	private void addToTable(IDescriptor desc) {
		descriptorTable.put(desc.getUid(), desc);
	}

	/**
	 * Adds descriptor to the descriptor table iff val is true and returns val.
	 * 
	 * @param val
	 * @param desc
	 */
	private boolean addToTable(boolean val, IDescriptor desc) {
		if (val)
			descriptorTable.put(desc.getUid(), desc);
		return val;
	}

	/**
	 * Removes descriptor from the descriptor table iff val is true and returns val.
	 * 
	 * @param val
	 * @param desc
	 */
	private boolean removeFromTable(boolean val, IDescriptor desc) {
		if (val)
			descriptorTable.put(desc.getUid(), desc);
		return val;
	}

	/**
	 * Finds descriptor for descriptor view.
	 * 
	 * @param desc
	 * @return
	 */
	private DirectoryDescriptor lookUp(DirectoryDescriptorView desc) {
		return (DirectoryDescriptor) descriptorTable.get(desc.getUid());
	}

	/**
	 * Finds descriptor for descriptor view.
	 * 
	 * @param desc
	 * @return
	 */
	private FileDescriptor lookUp(FileDescriptorView desc) {
		return (FileDescriptor) descriptorTable.get(desc.getUid());
	}

	/**
	 * Finds descriptor for descriptor view.
	 * 
	 * @param desc
	 * @return
	 */
	private IDescriptor lookUp(IDescriptor desc) {
		return descriptorTable.get(desc.getUid());
	}

	public DirectoryDescriptorView getJobResultsDir() {
		return toView(jobResults);
	}
	
	/**
	 * Returns DirectoryDescriptorView of root directory.
	 * 
	 * @return
	 */
	public DirectoryDescriptorView getRootView() {
		return new DirectoryDescriptorView(getRoot());
	}

	/**
	 * Returns DirectoryDescriptor of root directory.
	 * 
	 * @return
	 */
	private DirectoryDescriptor getRoot() {
		return root;
	}

	/**
	 * Creates a view for a descriptor.
	 * 
	 * @param desc
	 * @return
	 */
	private IDescriptor toView(IDescriptor desc) {
		if (desc.isView())
			return desc;

		if (desc.getType().equals(DescriptorTypes.FILE)) {
			return toView((FileDescriptor) desc);
		} else {
			return toView((DirectoryDescriptor) desc);
		}
	}

	/**
	 * Creates a view for a file descriptor.
	 * 
	 * @param desc
	 * @return
	 */
	private FileDescriptorView toView(FileDescriptor desc) {
		return new FileDescriptorView(desc);
	}

	/**
	 * Creates a view for a file descriptor.
	 * 
	 * @param desc
	 * @return
	 */
	private DirectoryDescriptorView toView(DirectoryDescriptor desc) {
		return new DirectoryDescriptorView(desc);
	}

	/**
	 * Translates views, creates and adds virtual directory to given directory.
	 * 
	 * @param dir
	 * @param name
	 * @return
	 */
	public boolean addVirtualDir(DirectoryDescriptorView dir, String name) {
		return addVirtualDir(lookUp(dir), name);
	}

	/**
	 * Creates and adds virtual directory to given directory.
	 * 
	 * @param dir
	 * @param name
	 * @return
	 */
	private boolean addVirtualDir(DirectoryDescriptor dir, String name) {
		DirectoryDescriptor fd = new DirectoryDescriptor(getNextId(), name);
		return addToTable(dir.addDesc(fd), fd);
	}

	/**
	 * Creates new descriptor and adds it to given directory.
	 * 
	 * @param dir      - directory view where new file will be added
	 * @param file     - file or directory to be added
	 * @param maxDepth - maximal depth for directory recursive adding
	 * @return
	 * @throws IOException
	 */
	public boolean addDescriptor(DirectoryDescriptorView dir, File file, int maxDepth) throws IOException {
		return addDescriptor(lookUp(dir), file, maxDepth);
	}

	/**
	 * Creates new descriptor and adds it to given directory.
	 * 
	 * @param dir      - directory where new file will be added
	 * @param file     - file or directory to be added
	 * @param maxDepth - maximal depth for directory recursive adding
	 * @return
	 * @throws IOException
	 */
	private boolean addDescriptor(DirectoryDescriptor dir, File file, int maxDepth) throws IOException {
		// Do not follow symlinks
		if (Files.isSymbolicLink(file.toPath()))
			return false;

		if (file.isFile()) { // Add file
			FileDescriptor fd = new FileDescriptor(file.getAbsolutePath(), getNextId(), file.getName());
			return addToTable(dir.addDesc(fd), fd);
		} else if (file.isDirectory()) { // Add directory
			DirectoryDescriptor fd = new DirectoryDescriptor(getNextId(), file.getName());
			if (addToTable(dir.addDesc(fd), fd)) {
				boolean result = true;
				if (maxDepth > 0) {
					// Recursively add other files from directory
					for (var name : file.list()) {
						result &= addDescriptor(fd, Paths.get(file.getAbsolutePath(), name).toFile(), maxDepth - 1);
					}
				}
				return result;
			} else {
				return false;
			}
		}

		return false;
	}

	/**
	 * Translates views and remove descriptor from given directory.
	 * 
	 * @param dir
	 * @param desc
	 * @return
	 */
	public boolean removeDescriptor(DirectoryDescriptorView dir, IDescriptor desc) {
		return removeDescriptor(lookUp(dir), lookUp(desc));
	}

	/**
	 * Remove descriptor from given directory.
	 * 
	 * @param dir
	 * @param desc
	 * @return
	 */
	private boolean removeDescriptor(DirectoryDescriptor dir, IDescriptor desc) {
		return removeFromTable(dir.removeDesc(desc), desc);
	}

	/**
	 * Returns File object for given view.
	 * 
	 * @param desc
	 * @return
	 */
	public File getFile(FileDescriptorView desc) {
		return lookUp(desc).getFile();
	}

	/**
	 * Returns list of view of content of given directory descriptor.
	 * 
	 * @param desc
	 * @return
	 */
	public List<IDescriptor> ls(DirectoryDescriptorView desc) {
		return lookUp(desc).ls().stream().map(this::toView).collect(Collectors.toList());
	}

	/**
	 * Returns FileDescriptorView based on where is starting directory
	 * and the path.
	 * 
	 * @param desc
	 * @param path
	 * @return
	 */
	public FileDescriptorView getFileByPath(DirectoryDescriptorView desc, String path) {
		DirectoryDescriptor dir = lookUp(desc);
		if (dir == null || path == null)
			return null;

		var paths = path.replaceFirst("^[\\/]?(root[\\/]?)?", "").split("[\\/]");
		for (int i = 0; i < paths.length; i++) { // walking through the path
			if (paths[i].isEmpty())
				continue;
			
			if (i < (paths.length - 1)) {
				dir = dir.getDirByName(paths[i]);
				if (dir == null)
					return null;
			} else { // finding correct path
				FileDescriptor fd = dir.getFileByName(paths[i]);
				if (fd != null)
					return toView(fd);
			}
		}
		return null;
	}

	/**
	 * Returns DirectoryDescriptorView based on the path.
	 * 
	 * @param path
	 * @return
	 */
	public DirectoryDescriptorView getDirectoryByPath(String path) {
		if (path == null)
			return null; 
		
		DirectoryDescriptor dir = root;
		var paths = path.replaceFirst("^[\\/]?(root[\\/]?)?", "").split("[\\/]");
		
		for (int i = 0; i < paths.length; i++) { // walking through the path
			if (paths[i].isEmpty())
				continue;
			
			dir = dir.getDirByName(paths[i]);
			if (dir == null)
				return null;
		}
		
		return toView(dir);
	}

	/**
	 * Generates new unique id.
	 * 
	 * @return
	 */
	private long getNextId() {
		return uidQueue++;
	}
}

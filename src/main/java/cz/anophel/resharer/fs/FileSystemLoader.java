package cz.anophel.resharer.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cz.anophel.resharer.utils.ResharerException;

/**
 * This class stores/loads a virtual file system.
 * 
 * @author Patrik Vesely
 *
 */
public final class FileSystemLoader {

	/**
	 * Loads a virtual file system from a file on the disk.
	 * 
	 * @param f
	 * @return
	 * @throws ResharerException
	 */
	public static FileSystem load(File f) throws ResharerException {
		try (var in = new ObjectInputStream(new FileInputStream(f))) {
			return (FileSystem) in.readObject();
		} catch (Exception e) {
			throw new ResharerException("Could not load virtual file system. MSG: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Saves the virtual file system to a file on the disk.
	 * 
	 * @param fs
	 * @param f
	 * @throws ResharerException
	 */
	public static void save(FileSystem fs, File f) throws ResharerException {
		try (var out = new ObjectOutputStream(new FileOutputStream(f))) {
			out.writeObject(fs);
		} catch (Exception e) {
			throw new ResharerException("Could not load virtual file system. MSG: " + e.getMessage(), e);
		}
	}
	
}

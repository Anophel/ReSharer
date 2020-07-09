package cz.anophel.resharer.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cz.anophel.resharer.utils.ResharerException;

public final class FileSystemLoader {

	
	public static FileSystem load(File f) throws ResharerException {
		try (var in = new ObjectInputStream(new FileInputStream(f))) {
			return (FileSystem) in.readObject();
		} catch (Exception e) {
			throw new ResharerException("Could not load virtual file system. MSG: " + e.getMessage(), e);
		}
	}
	
	public static void save(FileSystem fs, File f) throws ResharerException {
		try (var out = new ObjectOutputStream(new FileOutputStream(f))) {
			out.writeObject(fs);
		} catch (Exception e) {
			throw new ResharerException("Could not load virtual file system. MSG: " + e.getMessage(), e);
		}
	}
	
}

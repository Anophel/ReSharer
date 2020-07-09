package cz.anophel.resharer.rmi.runner;

import java.io.File;
import java.rmi.RemoteException;

import cz.anophel.resharer.rmi.DirectoryDescriptorView;
import cz.anophel.resharer.rmi.IResourceProvider;

public class ResharerClassLoader extends ClassLoader {

	private IResourceProvider provider;

	private DirectoryDescriptorView remoteClassPath;

	public ResharerClassLoader(IResourceProvider provider, DirectoryDescriptorView remoteClassPath) {
		super();
		this.provider = provider;
		this.remoteClassPath = remoteClassPath;
	}

	public ResharerClassLoader(ClassLoader parent, IResourceProvider provider,
			DirectoryDescriptorView remoteClassPath) {
		super(parent);
		this.provider = provider;
		this.remoteClassPath = remoteClassPath;
	}

	public ResharerClassLoader(String name, ClassLoader parent, IResourceProvider provider,
			DirectoryDescriptorView remoteClassPath) {
		super(name, parent);
		this.provider = provider;
		this.remoteClassPath = remoteClassPath;
	}

	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] b = loadClassFromFile(name);
		return defineClass(name, b, 0, b.length);
	}

	private byte[] loadClassFromFile(String fileName) throws ClassNotFoundException {
		try {
			return provider.getFile(remoteClassPath, fileName.replace('.', File.separatorChar) + ".class");
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new ClassNotFoundException();
		}
	}

}

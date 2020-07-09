package cz.anophel.resharer.rmi.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.util.List;

import cz.anophel.resharer.fs.FileSystem;
import cz.anophel.resharer.fs.IDescriptor;
import cz.anophel.resharer.rmi.DirectoryDescriptorView;
import cz.anophel.resharer.rmi.FileDescriptorView;
import cz.anophel.resharer.rmi.IResourceProvider;
import cz.anophel.resharer.rmi.ResourceProviderFactory;
import cz.anophel.resharer.rmi.runner.IResharerRunnable;
import cz.anophel.resharer.rmi.runner.ResharerClassLoader;
import cz.anophel.resharer.utils.Ref;

public class ResourceProviderImpl implements IResourceProvider {

	private Ref<FileSystem> fs;

	public ResourceProviderImpl(Ref<FileSystem> fs) {
		this.fs = fs;
	}

	@Override
	public DirectoryDescriptorView getRoot() {
		return fs.get().getRootView();
	}

	@Override
	public byte[] getFile(FileDescriptorView desc) {
		if (desc.exists()) {
			try {
				return Files.readAllBytes(fs.get().getFile(desc).toPath());
			} catch (IOException e) {
				return null;
			}
		}
		return null;
	}

	@Override
	public List<IDescriptor> ls(DirectoryDescriptorView desc) throws RemoteException {
		return fs.get().ls(desc);
	}

	@Override
	public byte[] getFile(DirectoryDescriptorView base, String path) throws RemoteException {
		if (base != null && path != null) {
			try {
				return Files.readAllBytes(fs.get().getFile(fs.get().getFileByPath(base, path)).toPath());
			} catch (IOException e) {
				return null;
			}
		}
		return null;
	}

	@Override
	public DirectoryDescriptorView getDirectory(String path) throws RemoteException {
		return fs.get().getDirectoryByPath(path);
	}

	@Override
	public void startRemoteJob(String remoteClassPath, String url, String mainClass, File output)
			throws RemoteException {
		try {
			// Connect to code base
			IResourceProvider provider = ResourceProviderFactory.instance().getProviderFor(url);
			DirectoryDescriptorView desc = provider.getDirectory(remoteClassPath);

			// Prepare classloader
			ResharerClassLoader classLoader = new ResharerClassLoader(ResourceProviderImpl.class.getClassLoader(),
					provider, desc);

			// Prepare main class
			@SuppressWarnings("unchecked")
			Class<? extends IResharerRunnable> job = (Class<? extends IResharerRunnable>) classLoader
					.loadClass(mainClass);

			// Create and add output file
			if (!fs.get().addDescriptor(fs.get().getJobResultsDir(), output, 0))
				throw new Exception("Could not add output file!");

			// Run job thread
			Thread jobThread = new Thread(() -> {
				// Prepare output stream
				try (PrintStream ps = new PrintStream(new FileOutputStream(output))) {
					try {
						var constructor = job.getConstructor();
						IResharerRunnable jobObj = constructor.newInstance();
						jobObj.run(ps);
					} catch (Exception e) {
						e.printStackTrace(ps);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			});
			jobThread.setDaemon(true);

			jobThread.start();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException("Failed to start remote job", e);
		}
	}

}

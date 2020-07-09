package cz.anophel.resharer.rmi;

import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import cz.anophel.resharer.fs.FileSystem;
import cz.anophel.resharer.rmi.server.ResourceProviderImpl;
import cz.anophel.resharer.utils.Ref;
import cz.anophel.resharer.utils.ResharerException;

/**
 * Factory for handling communication between client
 * and server.
 * 
 * @author Patrik Vesely
 *
 */
public final class ResourceProviderFactory {

	public static String REGISTRY_KEY = "ResourceProvider";

	public static int PORT = 1099;

	/**
	 * Main object for handling and storing RMI objects.
	 */
	private static ResourceProviderFactory singleton;
	
	/**
	 * Returns instance of ResourceProviderFactory
	 * 
	 * @return
	 */
	public static  ResourceProviderFactory instance() {
		synchronized (ResourceProviderFactory.class) {
			if (singleton == null)
				singleton = new ResourceProviderFactory();
			return singleton;
		}
	}
	
	/**
	 * Closes all communication for server side application.
	 */
	public static void mischiefManaged() {
		synchronized (ResourceProviderFactory.class) {
			try {
				singleton.unbindStub();
				singleton.destroyStub();
				singleton = null;
			} catch (Exception e) {
				// Just hide these things before Snape finds out.
			}
		}
	}
	
	/**
	 * Resource provider for file sharing.
	 */
	private ResourceProviderImpl resProvider;

	/**
	 * Stub for resource provider.
	 */
	private IResourceProvider stub;

	/**
	 * Registry for RMI.
	 */
	private Registry localRegistry;
	
	private ResourceProviderFactory() { }
	
	/**
	 * Returns a resource provider running on url.
	 * 
	 * @param url - address of a resharer server
	 * @return
	 * @throws ResharerException
	 */
	public IResourceProvider getProviderFor(String url) throws ResharerException {
		try {
			Registry remoteRegistry = LocateRegistry.getRegistry(url, PORT);
			return (IResourceProvider) remoteRegistry.lookup(REGISTRY_KEY);
		} catch (RemoteException e) {
			throw new ResharerException("Could not connect to given address.", e);
		} catch(NotBoundException e) {
			throw new ResharerException("Service not found on given address.", e);
		}
	}
	
	/**
	 * Prepares a stub that uses reference to given filesystem.
	 * 
	 * @param fs
	 * @throws ResharerException
	 */
	public void prepareStub(Ref<FileSystem> fs) throws ResharerException {
		try {
			resProvider = new ResourceProviderImpl(fs);
			stub = (IResourceProvider) UnicastRemoteObject.exportObject(resProvider, 0);
			localRegistry = LocateRegistry.createRegistry(PORT);
		} catch (RemoteException e) {
			resProvider = null;
			stub = null;
			localRegistry = null;
			throw new ResharerException("Could not create stub.", e);
		}
	}
	
	/**
	 * Destroys stub and registry to free resources.
	 */
	public void destroyStub() {
		try {
			if (stub != null)
				UnicastRemoteObject.unexportObject(stub, true);
			if (localRegistry != null)
				UnicastRemoteObject.unexportObject(localRegistry, true);
		} catch (NoSuchObjectException e) {
		}
	}
	
	/**
	 * Binds stub to RMI registry and starts listening
	 * 
	 * @throws ResharerException
	 */
	public void bindStub() throws ResharerException {
		if (stub == null)
			return;
		
		try {
			localRegistry.rebind(REGISTRY_KEY, stub);
		} catch (RemoteException e) {
			throw new ResharerException("Could not bind stub to RMI registry.", e);
		}
	}
	
	/**
	 * Unbinds stub and stops listening
	 * 
	 * @throws ResharerException
	 */
	public void unbindStub() throws ResharerException {
		if (stub == null)
			return;
		
		try {
			localRegistry.unbind(REGISTRY_KEY);
		} catch (RemoteException | NotBoundException e) {
			throw new ResharerException("Could not unbind stub from RMI registry.", e);
		}
	}
}

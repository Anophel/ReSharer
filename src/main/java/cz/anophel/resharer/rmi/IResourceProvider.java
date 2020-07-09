package cz.anophel.resharer.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import cz.anophel.resharer.fs.IDescriptor;

/**
 * Interface for RMI between client and server
 * for sharing resources.
 * 
 * @author Patrik Vesely
 *
 */
public interface IResourceProvider extends Remote {

	/**
	 * Returns a view object for root directory
	 * on the server.
	 * 
	 * @return
	 * @throws RemoteException
	 */
	DirectoryDescriptorView getRoot() throws RemoteException;
	
	/**
	 * Downloads a file from the server.
	 * 
	 * @param desc
	 * @return
	 * @throws RemoteException
	 */
	byte[] getFile(FileDescriptorView desc) throws RemoteException;
	
	/**
	 * Downloads a file from the server based on path and base directory.
	 * 
	 * @param desc
	 * @param path
	 * @return
	 * @throws RemoteException
	 */
	byte[] getFile(DirectoryDescriptorView base, String path) throws RemoteException;
	
	/**
	 * Get DirectoryDescriptorView from server identified by path.
	 * 
	 * @param path
	 * @return
	 * @throws RemoteException
	 */
	DirectoryDescriptorView getDirectory(String path) throws RemoteException;
	
	/**
	 * Lists files in directory from server.
	 * 
	 * @param desc
	 * @return
	 * @throws RemoteException
	 */
	List<IDescriptor> ls(DirectoryDescriptorView desc) throws RemoteException;
	
	/**
	 * Starts new jobs on the server with class files on 
	 * code base server.
	 * 
	 * @param remoteClassPath - class path in code base server
	 * @param url - address of code base server
	 * @param mainClass
	 * @param output - name of output file on worker server
	 * @throws RemoteException
	 */
	public void startRemoteJob(String remoteClassPath, String url, String mainClass, String output)
			throws RemoteException;
}

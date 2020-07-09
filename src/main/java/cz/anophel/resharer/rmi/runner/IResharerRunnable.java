package cz.anophel.resharer.rmi.runner;

/**
 * Interface of a custom remote job.
 * 
 * Every job, that is going to be executed on a ReSharer
 * server, needs to have a main class, which implements
 * this interface.
 * 
 * @author Patrik Vesely
 *
 */
public interface IResharerRunnable {

	/**
	 * Start point of the job with PrintStream opened
	 * to the result file.
	 * 
	 * @param out - PrintStream opened to the result file
	 */
	public void run(java.io.PrintStream out);
	
}

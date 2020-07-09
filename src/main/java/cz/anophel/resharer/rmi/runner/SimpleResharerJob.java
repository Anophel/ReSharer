package cz.anophel.resharer.rmi.runner;

import java.io.PrintStream;

/**
 * Example of a job for resharer remote computing.
 * 
 * @author Patrik Vesely
 *
 */
public class SimpleResharerJob implements IResharerRunnable {

	@Override
	public void run(PrintStream out) {
		// Sum of first 300 prime numbers
		int sum = 0;
		for (int i = 0, num = 2; i < 300; num++) {
			if (isPrime(num)) {
				sum += num;
				i++;
			}
		}
		try {
			out.append("Result: " + sum + "\n");
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean isPrime(int num) {
		int maxDiv = (int) Math.ceil(Math.sqrt(num));
		for (int i = 2; i < maxDiv; i++)
			if ((num % i) == 0)
				return false;
		return true;
	}

}

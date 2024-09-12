package channels;

import ichannels.IBroker;

public abstract class Task {
	/**
	 * Create a task with the given broker
	 * @param b Broker
	 * @param r Runnable
	 */
	Task(IBroker b, Runnable r) {
		// TODO
	}
	
	abstract static IBroker getBroker();
	
}

package task1;

public abstract class Task {
	/**
	 * Create a task with the given broker
	 * @param b Broker
	 * @param r Runnable
	 */
	Task(Broker b, Runnable r);
	
	/**
	 * return the broker
	 */
    static Broker getBroker();
}

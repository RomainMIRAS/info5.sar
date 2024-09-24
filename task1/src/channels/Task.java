package channels;

import ichannels.IBroker;

public class Task extends Thread {
	private IBroker broker;
	private Runnable runnable;

	/**
	 * Create a task with the given broker
	 * @param b Broker
	 * @param r Runnable
	 */
	public Task(IBroker b, Runnable r) {
		this.broker = b;
		this.runnable = r;
		this.start(); // Changed from this.run() to this.start()
	}

	@Override
	public void run() {
		this.runnable.run();
	}

	/**
	 * Get the broker associated with this task
	 * @return IBroker
	 */
	private IBroker getMyBroker() {
		return this.broker;
	}

	/**
	 * Static method to get the broker of the current thread
	 * @return IBroker
	 */
	public static IBroker getBroker() {
		Thread currentThread = Thread.currentThread();
		if (currentThread instanceof Task) {
			return ((Task) currentThread).getMyBroker();
		}
		throw new RuntimeException("Current thread is not a Task instance");
	}
}

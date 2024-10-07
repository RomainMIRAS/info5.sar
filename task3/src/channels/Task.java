package channels;

import imessages.IQueueBroker;

public class Task extends Thread {
	private IQueueBroker queueBroker;
	private Runnable runnable;

	/**
	 * Create a task with the given broker
	 * @param b Broker
	 * @param r Runnable
	 */
	public Task(Runnable r) {
		this.runnable = r;
		this.start(); // Changed from this.run() to this.start()
	}
	
	/**
	 * Create a task with the given queue broker
	 * 
	 * @param b QueueBroker
	 * @param r Runnable
	 */
	public Task(IQueueBroker b, Runnable r) {
		this.queueBroker = b;
		this.runnable = r;
		this.start();
	}

	@Override
	public void run() {
		this.runnable.run();
	}
	
	/**
	 * Get the queue broker associated with this task
	 * 
	 * @return IQueueBroker
	 */
	public IQueueBroker getQueueBroker() {
		return this.queueBroker;
	}

	/**
	 * Static method to get the broker of the current thread
	 * @return IBroker
	 */
	public static Task getTask() {
		Thread currentThread = Thread.currentThread();
		if (currentThread instanceof Task) {
			return ((Task) currentThread);
		}
		throw new RuntimeException("Current thread is not a Task instance");
	}
}

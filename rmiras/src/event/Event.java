package event;

public class Event implements Runnable {
	Task myTask;
	Runnable runnable;

	public Event(Task myTask, Task upperTask, Runnable runnable) {
		this.myTask = myTask;
		this.runnable = runnable;
	}

	@Override
	public void run() {
		if (myTask != null && !myTask.killed()) {
			runnable.run();
		}
	}
}

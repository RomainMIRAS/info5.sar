package event;

import java.util.LinkedList;
import java.util.List;

public class EventPump extends Thread{
	
	private Runnable currentRunnable;
	private EventTask task;
	private boolean running;
	private List<EventTask> tasks;
    private static EventPump instance;

	private EventPump() {
		super();
		this.currentRunnable = null;
		this.running = false;
		this.tasks = new LinkedList<>();
	}
	
	public static EventPump getEventPump() {
	    Thread t = Thread.currentThread();
		if (t instanceof EventPump) {
			return (EventPump) t;
		}
		return null;
	}
	

	static {
		instance = new EventPump();
	}
	
	public static synchronized EventPump getInstance() {
        if (instance == null) {
            instance = new EventPump();
        }
        return instance;
    }

	public synchronized void post(EventTask event) {
		this.tasks.add(event);
		notify();
	}

	public void kill() {
        this.running = false;		
	}
	
	public boolean killed() {
		return !this.running;
	}
	
	public EventTask getTask() {
		return task;
	}
	
	@Override
	public void run() {
		this.running = true;
		while (this.running) {
			if (this.tasks.isEmpty()) {
				sleep();
			} else {
				this.task = this.tasks.remove(0);
				this.currentRunnable = this.task.getMyRunnable();
				this.currentRunnable.run();
				this.currentRunnable = null;
				this.task = null;
			}
		}
	}
	
	public synchronized boolean remove(EventTask event) {
        return this.tasks.remove(event);
    }
	
	private synchronized void sleep() {
		try {
		wait();
		} catch (InterruptedException ex){
		// nothing to do here.
		}
	}
}

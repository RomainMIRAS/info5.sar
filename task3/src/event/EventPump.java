package event;

import java.util.LinkedList;
import java.util.List;

public class EventPump extends Thread{
	
	private Event currentRunnable;
	private boolean running;
	private List<Event> runnables;
    private static EventPump instance;

	private EventPump() {
		super();
		this.currentRunnable = null;
		this.running = false;
		this.runnables = new LinkedList<>();
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
	
	public static EventPump getInstance() {
        if (instance == null) {
            instance = new EventPump();
        }
        return instance;
    }

	public synchronized void post(Event event) {
		this.runnables.add(event);
		notify();
	}

	public void kill() {
        this.running = false;		
	}
	
	public boolean killed() {
		return !this.running;
	}
	
	@Override
	public void run() {
		this.running = true;
		while (this.running) {
			if (this.runnables.isEmpty()) {
				sleep();
			} else {
				this.currentRunnable = this.runnables.remove(0);
				Task.current = currentRunnable.myTask;
				this.currentRunnable.run();
				this.currentRunnable = null;
			}
		}
	}
	
	public synchronized boolean remove(Event event) {
        return this.runnables.remove(event);
    }
	
	private synchronized void sleep() {
		try {
		wait();
		} catch (InterruptedException ex){
		// nothing to do here.
		}
	}
}

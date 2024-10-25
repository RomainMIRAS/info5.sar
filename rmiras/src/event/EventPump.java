package event;

import java.util.LinkedList;
import java.util.Queue;

public class EventPump extends Thread{
	Event currentRunnable;
	boolean running;
	Queue<Event> runnables;
    static EventPump instance;

	private EventPump() {
		super("EventPump");
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
	public synchronized void run() {
		this.running = true;
		while (this.running) {
			if (this.runnables.isEmpty()) {
				sleep();
			} else {
				this.currentRunnable = this.runnables.poll();
				Task.current = currentRunnable.myTask;
				this.currentRunnable.run();
				this.currentRunnable = null;
			}
		}
	}
	
	public boolean remove(Event event) {
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

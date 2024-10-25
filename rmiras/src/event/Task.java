package event;

import java.util.HashSet;
import java.util.Set;

public class Task {
	
	boolean alive;
	static Task current = null;
	Set<Event> events;
	EventPump eventPump;
	String name;
	
	public Task(String name) {
		this.name = name;
		this.events = new HashSet<>();
		alive = true;
		eventPump = EventPump.getInstance();
	}
	
	/**
	 * 
	 * @param r
	 */
	public void post(Runnable r) {
		if (!alive) {
			return;
		}
		Event e = new Event(this, current, r);
		events.add(e);
		eventPump.post(e);
	}
	
	public static Task task() {
		return current;
	}
	
	public void kill() {
		this.alive = false;
		for (Event e : events) {
			eventPump.remove(e);
		}
	}
	
	public boolean killed() {
		return !alive;
	}
	
	public String name() {
		return name;
	}
}

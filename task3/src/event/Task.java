package event;

public class Task {
	
	boolean alive;
	static Task current = null;
	
	public Task() {
		alive = true;
	}
	
	public void post(Runnable r) {
		Task t = task();
		Event e = new Event(this, t, r);
		EventPump.getInstance().post(e);
	}
	
	public static Task task() {
		return current;
	}
	
	public void kill() {
		alive = false;
	}
	
	public boolean killed() {
		return !alive;
	}
}

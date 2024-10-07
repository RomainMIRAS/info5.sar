package event;

public class EventTask {
	
	private boolean alive;
		
	private Runnable myRunnable;
	private EventPump pump;

	public EventTask() {
		myRunnable = null;
		alive = true;
		pump = EventPump.getInstance();
	}
	
	public void post(Runnable r) {
		this.myRunnable = r;
		pump.post(this);
	}
	
	public static EventTask task() {
		EventPump pump = EventPump.getEventPump();
		if (pump != null) {
			return pump.getTask();
		}
		return null;
	}
	
	public void kill() {
		this.alive = false;
		pump.remove(this);
	}
	
	public boolean killed() {
		return !this.alive;
	}
		
	public Runnable getMyRunnable() {
		return myRunnable;
	}
}

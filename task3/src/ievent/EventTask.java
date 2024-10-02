package ievent;

public abstract class EventTask {
	public abstract void post(Runnable r);
	public static EventTask task() {
		return null;
	}
	public abstract void kill();
	public abstract boolean killed();
}

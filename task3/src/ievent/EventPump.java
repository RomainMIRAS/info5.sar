package ievent;

public abstract class EventPump extends Thread{
	
	Runnable currentRunnable;
	
	public abstract void post(Runnable r, long delay);

	public abstract void post(Runnable r);

	public abstract void kill();
	
	public abstract void start();

	public static EventPump pump() {
		return null;
	}
}

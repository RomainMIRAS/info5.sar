package channels;

import event.Task;

public class AcceptRunnable implements Runnable {
	Binder binder;

	public AcceptRunnable(Binder binder) {
		this.binder = binder;
	}

	@Override
	public void run() {
		if (!binder.alreadyAccepted) {
			Task.task().post(this);
			return;
		}
		binder.acceptConnection();
	}

}

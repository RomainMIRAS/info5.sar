package event;

import ichannels.IChannel;

public class CloseEventTask extends EventTask {

	public CloseEventTask(IChannel channel) {
        super();
        this.myRunnable = new Runnable() {
            @Override
            public void run() {
                channel.disconnect();
            }
        };
    }

	@Override
	public void post(Runnable r) {
		throw new RuntimeException("Should not be call");
	}

	public void postTask() {
		super.post(myRunnable);
	}
}

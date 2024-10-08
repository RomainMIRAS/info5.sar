package event;

import ichannels.DisconnectedException;
import messages.MessageQueue;

public class ReceiveEventTask extends EventTask {
	
	byte[] bytes;
	int offset;
	int length;
	
	public ReceiveEventTask(MessageQueue queue , int length) {
		super();
		this.bytes = new byte[length];
		this.offset = 0;
		this.length = length;
		this.myRunnable = new Runnable() {
			@Override
			public void run() {
				
				if (queue.getListener() == null) {
					EventPump.getInstance().post(EventTask.task());
					return;
				}
				
				try {
					int byteRead = queue.getChannel().read(bytes, offset, length - offset);
					offset += byteRead;
				} catch (DisconnectedException e) {
					// Do nothing
				}
				
				if (offset == length) {
					queue.getListener().received(bytes);
				}
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

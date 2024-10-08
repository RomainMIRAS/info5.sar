package event;

import ichannels.DisconnectedException;
import ichannels.IChannel;
import imessages.IMessageQueue.Listener;
import imessages.Message;
import messages.MessageQueue;

public class SendEventTask extends EventTask {

	private Runnable myRunnable;
	
	ReceiveEventTask receiveEvent;
	
	private byte[] bytes;
	private int offset;
	private int length;

	

	public SendEventTask(Listener listener, IChannel channel, Message message, MessageQueue remoteQueue) {
		super();
		
		receiveEvent = new ReceiveEventTask(remoteQueue , message.getLength());
		length = message.getLength();
		bytes = message.getBytes();
		offset = 0;
		
		this.myRunnable = new Runnable() {
			@Override
			public void run() {
				if (remoteQueue.getListener() == null) {
                    EventPump.getInstance().post(EventTask.task());
                    return;
                }
				
				try {
					int byteWrite = channel.write(bytes, offset, length - offset);
					offset += byteWrite;
					receiveEvent.postTask();
				} catch (DisconnectedException e) {
					// Do nothing
				}
				
				if (offset == length) {
                    listener.sent(message);
				} else {
					EventPump.getInstance().post(EventTask.task());
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

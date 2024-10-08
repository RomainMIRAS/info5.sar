package messages;

import event.CloseEventTask;
import event.SendEventTask;
import ichannels.IChannel;
import imessages.IMessageQueue;
import imessages.Message;

public class MessageQueue implements IMessageQueue {
	
	private IChannel channel;
	private Listener listener;
	private MessageQueue remoteQueue;
	
	public MessageQueue(IChannel channel) {
		this.channel = channel;
		this.listener = null;
	}

	@Override
	public void setListener(Listener listener) {
		this.listener = listener;
	}

	@Override
	public synchronized boolean send(Message message) {
		if (this.listener == null) {
			return false;
		} else if (channel.disconnected()) {
			return false;
		}
				
		SendEventTask sendEvent = new SendEventTask(listener, channel, message, remoteQueue);
		sendEvent.postTask();
			
		return true;
	}
	
	public Listener getListener() {
		return listener;
	}
	
	public IChannel getChannel() {
		return channel;
	}

	@Override
	public void close() {
		CloseEventTask closeEvent = new CloseEventTask(channel);
		closeEvent.postTask();
	}

	@Override
	public boolean closed() {
		return channel.disconnected();
	}
	
	public void setRemoteQueue(MessageQueue remoteQueue) {
	    this.remoteQueue = remoteQueue;
	}

}
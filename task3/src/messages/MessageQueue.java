package messages;

import ichannels.IChannel;
import imessages.IMessageQueue;
import imessages.Message;

public class MessageQueue implements IMessageQueue {
	
	IChannel channel;
	
	Listener listener;
	
	public MessageQueue(IChannel channel) {
		this.channel = channel;
		this.listener = null;
	}

	@Override
	public void setListener(Listener listener) {
		this.listener = listener;
	}

	@Override
	public boolean send(Message message) {
		if (this.listener == null) {
			return false;
		}
	
		return true;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean closed() {
		// TODO Auto-generated method stub
		return false;
	}

}

package messages;

import ichannels.IChannel;
import imessages.IMessageQueue;
import imessages.Message;

public class MessageQueue implements IMessageQueue {
	
	IChannel channel;
	
	Listener listener;
	
	public MessageQueue(IChannel channel) {
		this.channel = channel;
	}

	@Override
	public void setListener(Listener listener) {
		this.listener = listener;
	}

	@Override
	public void send(Message message) {
	    // TODO Auto-generated method stub
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

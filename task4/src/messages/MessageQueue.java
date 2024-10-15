package messages;

import java.util.LinkedList;
import java.util.Queue;

import event.Task;
import ichannels.IChannel;
import imessages.IMessageQueue;
import imessages.Message;

public class MessageQueue implements IMessageQueue {
	
	private IChannel channel;
	private Queue<Message> messages;
	private Listener listener;
	private Task sendTask;
	private MyListener myListener;

	public MessageQueue(IChannel channel) {
		this.channel = channel;
		messages = new LinkedList();
		sendTask = new Task("SendTask");
		myListener = new MyListener();
	}

	@Override
	public void setListener(Listener listener) {
		this.listener = listener;
		myListener.setListener(listener);
	}

	@Override
	public boolean send(Message message) {
		if (channel.disconnected()) {
			return false;
		}

	}

	@Override
	public void close() {
		channel.disconnect();
	}

	@Override
	public boolean closed() {
		return channel.disconnected();
	}
	
	private class MyListener implements IChannel.Listener {
		
		private Listener listener;
		
		public MyListener() {
			this.listener = null;
		}
		
		void setListener(Listener listener) {
            this.listener = listener;
        }

		@Override
		public void readed(byte[] bytes) {
			
		}

		@Override
		public void disconnected() {
			
		}

		@Override
		public void wrote(int bytesWrote) {
			
		}
	}

}
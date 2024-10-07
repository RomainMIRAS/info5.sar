package event;

import channels.Channel;
import imessages.IMessageQueue;
import imessages.IQueueBroker;
import messages.MessageQueue;

public class AcceptTaskEvent extends EventTask {
    
	private MessageQueue acceptQueue;
	private MessageQueue connectQueue;
	
	private boolean connectionAlreadyAccepted;
	
	
	public AcceptTaskEvent(int port, IQueueBroker.AcceptListener listener) {
		super();
		
		connectionAlreadyAccepted = false;
		Channel connectChannel = new Channel();
		connectChannel.connect(connectChannel);
		Channel acceptChannel = new Channel();
		acceptChannel.connect(acceptChannel);
		
		acceptQueue = new MessageQueue(acceptChannel);
		connectQueue = new MessageQueue(connectChannel);
		
		this.myRunnable = new Runnable() {
			@Override
			public void run() {
				// if connection is already accepted, repost the task
				if (connectionAlreadyAccepted) {
					EventPump.getInstance().post(EventTask.task());
				} else {
					listener.accepted(acceptQueue);
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
	
	public boolean isConnectionAlreadyAccepted() {
		return connectionAlreadyAccepted;
	}
	
	public IMessageQueue getConnectQueue() {
		return connectQueue;
	}
}

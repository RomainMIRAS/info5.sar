package tests;

import event.EventPump;
import event.Task;
import imessages.IMessageQueue;
import imessages.IQueueBroker;
import imessages.Message;
import messages.QueueBroker;

public class EchoServer {
	
	public final static int messageSize = 255;
	
    public static void main(String[] args) {
    	
    	Runnable serverRunnable = new Runnable() {
 		   public void run() {
 			   IQueueBroker queueBroker = new QueueBroker("server");
 			   MyQueueAcceptListener listener = new MyQueueAcceptListener();
 				boolean bound = queueBroker.bind(8080, listener);
 				
 				if (!bound) {
 					System.out.println("Server failed to bind");
 					return;
 				}
            }
        };
        
 		Runnable clientRunnable = new Runnable() {
 			public void run() {
 				IQueueBroker queueBroker = new QueueBroker("client");
 				MyQueueConnectListener listener = new MyQueueConnectListener();
 				boolean connected = queueBroker.connect("server", 8080, listener);
 				if (!connected) {
 					System.out.println("Client failed to connect");
 					return;
 				}
 			}
 		};
 		
 		new Task().post(serverRunnable);
 		new Task().post(clientRunnable);
 		EventPump.getInstance().start();
    }
}



class MyEchoServerQueueListener implements IMessageQueue.Listener {
	
	private IMessageQueue queue;
	
	public MyEchoServerQueueListener(IMessageQueue queue) {
		this.queue = queue;
	}

	@Override
	public void received(byte[] bytes) {
		System.out.println("Server received message");
        try {
            boolean sent = queue.send(new Message(bytes));
			if (!sent) {
				System.out.println("Server failed to send response");
			}
        } catch (Exception e) {
        	System.out.println("Server failed to send message: " + e.getMessage());
        }
	}

	@Override
	public void closed() {
		System.out.println("Server finished");
	}

	@Override
	public void sent(Message message) {
		System.out.println("Server sent response");
	}
}

class MyEchoClientQueueListener implements IMessageQueue.Listener {
	
	private IMessageQueue queue;
	
	public MyEchoClientQueueListener(IMessageQueue queue) {
		this.queue = queue;
	}

	@Override
	public void received(byte[] bytes) {
		System.out.println("Client received response");
		
		// Check if the response is correct
		int messageSize = EchoServer.messageSize;
		byte[] messageContent = new byte[messageSize];
		for (int i = 0; i < messageSize; i++) {
			messageContent[i] = (byte) (i + 1);
		}
		
		for (int i = 0; i < messageSize; i++) {
			if (bytes[i] != messageContent[i]) {
				System.out.println("Client received incorrect response");
				return;
			}
		}
		
		queue.close();
		
		System.out.println("Test passed");
	}

	@Override
	public void closed() {
		System.out.println("Client finished");
	}

	@Override
	public void sent(Message message) {
		System.out.println("Client sent message");
	}
	
}	

class MyQueueAcceptListener implements IQueueBroker.AcceptListener {
	
	@Override
	public void accepted(IMessageQueue queue) {
		System.out.println("Server accepted connection");
		MyEchoServerQueueListener listener = new MyEchoServerQueueListener(queue);
		queue.setListener(listener);
	}
	
}

class MyQueueConnectListener implements IQueueBroker.ConnectListener {

	@Override
	public void connected(IMessageQueue queue) {
		System.out.println("Connection established for client");
		int messageSize = EchoServer.messageSize;
		byte[] messageContent = new byte[messageSize];
		for (int i = 0; i < messageSize; i++) {
			messageContent[i] = (byte) (i + 1);
		}
		
		MyEchoClientQueueListener listener = new MyEchoClientQueueListener(queue);
		queue.setListener(listener);

		Message message = new Message(messageContent, 0, messageSize);
		boolean sent = queue.send(message);
		if (!sent) {
			System.out.println("Client failed to send message");
		}		
	}

	@Override
	public void refused() {
		System.out.println("Connection refused");
	}
	
}
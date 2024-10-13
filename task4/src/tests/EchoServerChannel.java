package tests;

import channels.Broker;
import event.EventPump;
import event.Task;
import ichannels.IBroker;
import ichannels.IChannel;

public class EchoServerChannel {
	
	public final static int messageSize = 255;
	
   public static void main(String[] args) {
	   
	   Runnable serverRunnable = new Runnable() {
		   public void run() {
			   	Broker broker = new Broker("server");
			   	MyAcceptListener listener = new MyAcceptListener();
				boolean bound = broker.bind(8080, listener);
				
				if (!bound) {
					System.out.println("Server failed to bind");
					return;
				}
           }
       };
       
		Runnable clientRunnable = new Runnable() {
			public void run() {
				Broker broker = new Broker("client");
				MyConnectListener listener = new MyConnectListener();
				boolean connected = broker.connect("server", 8080, listener);
				if (!connected) {
					System.out.println("Client failed to connect");
					return;
				}
			}
		};
		Task mainTask = new Task("Main Test Channel");
		mainTask.post(serverRunnable);
		mainTask.post(clientRunnable);
		EventPump.getInstance().start();
	}
   
}




class MyEchoServerListener implements IChannel.Listener {
	
	private IChannel channel;
	
	public MyEchoServerListener(IChannel channel) {
		this.channel = channel;
	}

	@Override
	public void readed(byte[] bytes) {
		System.out.println("Server received bytes");
        try {
            boolean sent = channel.write(bytes,0,bytes.length);
			if (!sent) {
				System.out.println("Server failed to send response");
			}
        } catch (Exception e) {
        	System.out.println("Server failed to send message: " + e.getMessage());
        }
	}

	@Override
	public void disconnected() {
		System.out.println("Server finished");
	}

	@Override
	public void wrote(int bytesWrote) {
		System.out.println("Server wrote " + bytesWrote + " bytes");		
	}
}

class MyEchoClientListener implements IChannel.Listener {
	
	private IChannel channel;
	int bytesSent = 0;
	byte[] messageContent;
	
	public MyEchoClientListener(IChannel channel) {
		this.channel = channel;
	}

	@Override
	public void readed(byte[] bytes) {
		System.out.println("Client received response");
		
		// Check if the response is correct
		int messageSize = EchoServerChannel.messageSize;
		messageContent = new byte[messageSize];
		for (int i = 0; i < messageSize; i++) {
			messageContent[i] = (byte) (i + 1);
		}
		
		for (int i = 0; i < messageSize; i++) {
			if (bytes[i] != messageContent[i]) {
				System.out.println("Client received incorrect response");
				return;
			}
		}
		
		channel.disconnect();
		
		System.out.println("Test passed");
	}

	@Override
	public void disconnected() {
		System.out.println("Client finished");
		
	}

	@Override
	public void wrote(int bytesSent) {
		this.bytesSent += bytesSent;
        System.out.println("Client wrote " + this.bytesSent + " bytes");
        // Keep sending until the message is fully sent
        
		if (this.bytesSent < messageContent.length) {
			boolean sent = channel.write(messageContent, this.bytesSent, messageContent.length - this.bytesSent);
			if (!sent) {
				System.out.println("Client failed to send message");
			}
		} else {
			System.out.println("Client finished sending message");
		}
	}
	
}	

class MyAcceptListener implements IBroker.AcceptListener {
	
	@Override
	public void accepted(IChannel channel) {
		System.out.println("Server accepted connection");
		MyEchoServerListener listener = new MyEchoServerListener(channel);
		channel.setListener(listener);
	}
	
}

class MyConnectListener implements IBroker.ConnectListener {

	@Override
	public void connected(IChannel channel) {
		System.out.println("Connection established for client");
		int messageSize = EchoServerChannel.messageSize;
		byte[] messageContent = new byte[messageSize];
		for (int i = 0; i < messageSize; i++) {
			messageContent[i] = (byte) (i + 1);
		}
		
		MyEchoClientListener listener = new MyEchoClientListener(channel);
		channel.setListener(listener);

		boolean sent = channel.write(messageContent, 0, messageSize);
		if (!sent) {
			System.out.println("Client failed to send message");
		}		
	}

	@Override
	public void refused() {
		System.out.println("Connection refused");
	}
	
}
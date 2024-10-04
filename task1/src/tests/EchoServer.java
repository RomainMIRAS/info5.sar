package tests;

import channels.Broker;
import channels.Task;
import ichannels.DisconnectedException;
import ichannels.IBroker;
import ichannels.IChannel;
import imessages.IMessageQueue;
import imessages.IQueueBroker;
import messages.QueueBroker;

public class EchoServer {
    public static void main(String[] args) {
		//testChannel();
		testMessage(5000);
    }
    
	private static void testChannel() {
		Broker serverBroker = new Broker("server");
        new Task(serverBroker, () -> runServer());
        
        try {
            Thread.sleep(1000); // Wait for the server to starts
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Broker clientBroker = new Broker("client");
        new Task(clientBroker, () -> runClient());
	}
	
	private static void testMessage(int messageSize) {
		Broker serverBroker = new Broker("server");
		QueueBroker serverQueueBroker = new QueueBroker(serverBroker);
        new Task(serverQueueBroker, () -> runServerMessage(messageSize));
        
        try {
            Thread.sleep(1000); // Wait for the server to starts
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Broker clientBroker = new Broker("client");
        QueueBroker clientQueueBroker = new QueueBroker(clientBroker);
        new Task(clientQueueBroker, () -> runClientMessage(messageSize));
	}
	
	private static void runServerMessage(int messageSize) {
		IQueueBroker queueBroker = Task.getTask().getQueueBroker();
		IMessageQueue messageQueue = queueBroker.accept(8080);
		
		byte[] message = messageQueue.receive();
		if (message == null) {
			System.out.println("Server failed to receive message");
			return;
		}
		else if (message.length != messageSize) {
			System.out.println("Server received message of incorrect size");
			return;
		}
		
		try {
			messageQueue.send(message, 0, message.length);
		} catch (DisconnectedException e) {
			System.out.println("Server failed to send message: " + e.getMessage());
		}
		
		messageQueue.closed();
		
		System.out.println("Server finished");
	}
	
	private static void runClientMessage(int messageSize) {
		IQueueBroker queueBroker = Task.getTask().getQueueBroker();
		IMessageQueue messageQueue = queueBroker.connect("server", 8080);

		byte[] message = new byte[messageSize];
		for (int i = 0; i < messageSize; i++) {
			message[i] = (byte) (i + 1);
		}

		try {
			messageQueue.send(message, 0, message.length);
		} catch (DisconnectedException e) {
			System.out.println("Client failed to send message: " + e.getMessage());
		}

		byte[] response = messageQueue.receive();
		if (response == null) {
			System.out.println("Client failed to receive response");
			return;
		} else if (response.length != message.length) {
			System.out.println("Client received response of incorrect size");
			return;
		}

		for (int i = 0; i < response.length; i++) {
			if (message[i] != response[i]) {
				System.out.println("Client received incorrect response");
				return;
			}
		}

		messageQueue.closed();

		System.out.println("Client finished");
	}

    private static void runServer() {
    	IBroker broker = Task.getTask().getBroker();
        IChannel channel = broker.accept(8080);
        
        byte[] buffer = new byte[256];
        int bytesRead = 0;
        int byteWrite = 0;
        try {	
            while (true) {
            	bytesRead = channel.read(buffer, 0, buffer.length);
				while (byteWrite < bytesRead) {
					byteWrite += channel.write(buffer, byteWrite, bytesRead - byteWrite);
				}
                byteWrite = 0;            
             }
        } catch (DisconnectedException e) {
            System.out.println("Server disconnected: " + e.getMessage());
        } finally {
            channel.disconnect();
        }
    }
    
    private static void runClient() {
    	IBroker broker = Task.getTask().getBroker();
        IChannel channel = broker.connect("server", 8080);
        if (channel == null) {
            System.out.println("Failed to connect to server");
            return;
        }
        
        byte[] sendData = new byte[255];
        for (int i = 0; i < 255; i++) {
            sendData[i] = (byte) (i + 1);
        }
        
        try {
        	int byteWrite = 0;
        	
        	byte[] receiveData = new byte[255];
            int bytesRead = 0;
            
        	while (byteWrite < sendData.length) {
         		byteWrite += channel.write(sendData, byteWrite, sendData.length - byteWrite);
         		
         		// clean the receive buffer
				bytesRead += channel.read(receiveData, bytesRead, receiveData.length - bytesRead);
        	}
        	
            // Check if the data is received correctly
			while ((bytesRead < receiveData.length)) {
				bytesRead += channel.read(receiveData, bytesRead, receiveData.length - bytesRead);
			}
            
            for (int i = 0; i < bytesRead; i++) {
                if (sendData[i] != receiveData[i]) {
                    System.out.println("Test failed at byte " + i);
                    return;
                }
            }
            
            System.out.println("Test passed");
        } catch (DisconnectedException e) {
            System.out.println("Test failed due to disconnection: " + e.getMessage());
        } finally {
            channel.disconnect();
        }
    }
    
}
package tests;

import channels.Broker;
import channels.Task;
import ichannels.DisconnectedException;
import ichannels.IBroker;
import ichannels.IChannel;

public class EchoServer {
    public static void main(String[] args) {
        Broker serverBroker = new Broker("server");
        new Task(serverBroker, () -> runServer());
        
        try {
            Thread.sleep(1000); // Wait for the server to start
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Broker clientBroker = new Broker("client");
        new Task(clientBroker, () -> runClient());
    }

    private static void runServer() {
    	IBroker broker = Task.getBroker();
        IChannel channel = broker.accept(8080);
        
        byte[] buffer = new byte[256];
        int bytesRead;
        
        try {
            while ((bytesRead = channel.read(buffer, 0, buffer.length)) != -1) {
                channel.write(buffer, 0, bytesRead);
            }
        } catch (DisconnectedException e) {
            System.out.println("Server disconnected: " + e.getMessage());
        } finally {
            channel.disconnect();
        }
    }
    
    private static void runClient() {
    	IBroker broker = Task.getBroker();
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
            channel.write(sendData, 0, sendData.length);
            
            byte[] receiveData = new byte[255];
            int bytesRead = channel.read(receiveData, 0, receiveData.length);
            
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
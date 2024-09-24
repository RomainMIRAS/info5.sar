package tests;

import channels.Broker;
import channels.Task;
import ichannels.IBroker;
import ichannels.IChannel;
import ichannels.DisconnectedException;

public class EchoClient {
    public static void main(String[] args) {
        Broker clientBroker = new Broker("client");
        new Task(clientBroker, () -> runClient());
    }

    private static void runClient() {
    	IBroker broker = Task.getBroker();
        IChannel channel = broker.connect("127.0.0.1", 8080);
        
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
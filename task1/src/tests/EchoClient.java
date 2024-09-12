package tests;

import channels.Broker;
import channels.Channel;
import ichannels.IChannel;

public class EchoClient {
    public static void main(String[] args) {
        Broker clientBroker = new Broker("127.0.0.1");
        IChannel channel = clientBroker.connect("127.0.0.1", 8080);
        
        byte[] sendData = new byte[255];
        for (int i = 0; i < 255; i++) {
            sendData[i] = (byte) (i + 1);
        }
        
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
        channel.disconnect();
    }
}
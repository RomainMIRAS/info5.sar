package tests;

import channels.Broker;
import channels.Channel;

public class EchoServer {
    public static void main(String[] args) {
        Broker serverBroker = new Broker("127.0.0.1");
        Channel channel = (Channel) serverBroker.accept(8080);
        
        byte[] buffer = new byte[256];
        int bytesRead;
        
        while ((bytesRead = channel.read(buffer, 0, buffer.length)) != -1) {
            channel.write(buffer, 0, bytesRead);
        }
        
        channel.disconnect();
    }
}

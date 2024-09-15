package tests;

import channels.Broker;
import channels.Task;
import ichannels.IBroker;
import ichannels.IChannel;

public class EchoServer {
    public static void main(String[] args) {
        Broker serverBroker = new Broker("127.0.0.1");
        new Task(serverBroker, () -> runServer());
    }

    private static void runServer() {
    	IBroker broker = Task.getBroker();
        IChannel channel = broker.accept(8080);
        
        byte[] buffer = new byte[256];
        int bytesRead;
        
        while ((bytesRead = channel.read(buffer, 0, buffer.length)) != -1) {
            channel.write(buffer, 0, bytesRead);
        }
        
        channel.disconnect();
    }
}
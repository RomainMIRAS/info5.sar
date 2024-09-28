package imessages;

public interface IQueueBroker {
	String name();
	IMessageQueue accept(int port);
	IMessageQueue connect(String name, int port);
}

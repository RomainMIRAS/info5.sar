package task1;

public abstract class Broker {
	Broker(String name);
	void connect(String name, int port);
	void accept(int port);
}

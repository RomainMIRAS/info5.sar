package tests;

import java.util.ArrayList;
import java.util.List;

import channels.Broker;
import event.EventPump;
import event.Task;
import ichannels.IBroker;
import ichannels.IChannel;

public class EchoServerChannel {

	public final static int MESSAGE_SIZE = 120;

	public final static byte[] messageContent = new byte[MESSAGE_SIZE];

	static {
		for (int i = 0; i < MESSAGE_SIZE; i++) {
			messageContent[i] = (byte) i;
		}
	}

	public static void main(String[] args) {
		
		WorkingMultipleClientSending();
		
		//ServerStopBinding();
		
		// Démarrage du système d'événements
		EventPump.getInstance().start();
	}
	
	private static void ServerStopBinding() {
		// Initialisation de la tâche principale
		Task mainTask = new Task("Test Channel - Server Stop Binding");

		// Lancement du serveur
		Runnable serverRunnable = new Runnable() {
			@Override
			public void run() {
				Broker broker = new Broker("serverStopBinding");
				MyAcceptListener listener = new MyAcceptListener();
				boolean bound = broker.bind(8080, listener);
				
				if (!bound) {
					throw new RuntimeException("Server failed to bind");
				}
				
				broker.unbind(8080);

			}
		};
		mainTask.post(serverRunnable);
		
		// Création d'un client
		Runnable clientRunnable = new Runnable() {
			@Override
			public void run() {
				Broker broker = new Broker("clientStopBinding");
				MyConnectListener listener = new MyConnectListener();
				boolean connected = broker.connect("serverStopBinding", 8080, listener);

				if (!connected) {
					throw new RuntimeException("Client failed to connect");
				}
			}
		};
		mainTask.post(clientRunnable);
	}

	private static void WorkingMultipleClientSending() {
		// Initialisation de la tâche principale
		Task mainTask = new Task("Test Channel - Multiple Client Sending");

		// Lancement du serveur
		Runnable serverRunnable = new Runnable() {
			@Override
			public void run() {
				Broker broker = new Broker("server");
				MyAcceptListener listener = new MyAcceptListener();
				boolean bound = broker.bind(8080, listener);

				if (!bound) {
					throw new RuntimeException("Server failed to bind");
				}
			}
		};
		mainTask.post(serverRunnable);

		// Création de plusieurs clients
		List<Runnable> clientRunnables = new ArrayList<>();
		int clientCount = 5;

		for (int i = 0; i < clientCount; i++) {
			final int clientId = i;
			clientRunnables.add(new Runnable() {
				@Override
				public void run() {
					Broker broker = new Broker("client" + clientId);
					MyConnectListener listener = new MyConnectListener();
					boolean connected = broker.connect("server", 8080, listener);

					if (!connected) {
						System.out.println("Client " + clientId + " failed to connect");
					}
				}
			});
		}

		// Lancement de chaque client
		for (Runnable clientRunnable : clientRunnables) {
			mainTask.post(clientRunnable);
		}
	}
}

class MyEchoServerListener implements IChannel.ReadListener {

	private final IChannel channel;

	public MyEchoServerListener(IChannel channel) {
		this.channel = channel;
	}

	@Override
	public void available() {
		System.out.println("Server has data available to read");

		byte[] bytes = new byte[EchoServerChannel.MESSAGE_SIZE];
		int bytesRead = channel.read(bytes, 0, EchoServerChannel.MESSAGE_SIZE);

		System.out.println("Server read " + bytesRead + " bytes");

		if (bytesRead == 0)
			return;

		IChannel.WriteListener listenerWrite = new IChannel.WriteListener() {
			@Override
			public void written(int bytesWritten) {
				System.out.println("Server wrote " + bytesWritten + " bytes");
			}
		};

		try {
			boolean sent = channel.write(bytes, 0, bytes.length, listenerWrite);
			if (!sent) {
				throw new RuntimeException("Server failed to send response");
			}
		} catch (Exception e) {
			throw new RuntimeException("Server failed to send message: " + e.getMessage());
		}
	}
}

class MyEchoClientListener implements IChannel.ReadListener {

	private final IChannel channel;
	int bytesReceived;
	byte[] messageReceived = new byte[EchoServerChannel.MESSAGE_SIZE];

	public MyEchoClientListener(IChannel channel) {
		this.channel = channel;
		this.bytesReceived = 0;
	}

	@Override
	public void available() {
		System.out.println("Client has data available to read");

		int bytedRead = channel.read(messageReceived, bytesReceived, EchoServerChannel.MESSAGE_SIZE - bytesReceived);

		System.out.println("Client read " + bytedRead + " bytes");

		if (bytedRead + bytesReceived > EchoServerChannel.MESSAGE_SIZE) {
			throw new RuntimeException("Client received too many bytes");
		} else if (bytedRead == 0) {
			throw new RuntimeException("Client received empty response");
		}

		bytesReceived += bytedRead;

		if (bytesReceived < EchoServerChannel.MESSAGE_SIZE) {
			return;
		}

		for (int i = 0; i < EchoServerChannel.MESSAGE_SIZE; i++) {
			if (messageReceived[i] != EchoServerChannel.messageContent[i]) {
				throw new RuntimeException("Client received incorrect response");
			}
		}

		channel.disconnect(new IChannel.DisconnectListener() {
			@Override
			public void disconnected() {
				System.out.println("Client disconnected");
			}
		});

		System.out.println("Test passed");
	}
}

class MyAcceptListener implements IBroker.AcceptListener {

	@Override
	public void accepted(IChannel channel) {
		System.out.println("Server accepted connection");

		MyEchoServerListener listener = new MyEchoServerListener(channel);
		channel.setReadListener(listener);
	}

}

class MyConnectListener implements IBroker.ConnectListener {
	@Override
	public void connected(IChannel channel) {
		System.out.println("Connection established for client");

		MyEchoClientListener listener = new MyEchoClientListener(channel);
		channel.setReadListener(listener);

		IChannel.WriteListener listenerWrite = new IChannel.WriteListener() {

			private int bytesSent = 0;

			@Override
			public void written(int bytesSent) {
				this.bytesSent += bytesSent;
				System.out.println("Client wrote " + bytesSent + " bytes");

				if (this.bytesSent < EchoServerChannel.MESSAGE_SIZE) {
					boolean sent = channel.write(EchoServerChannel.messageContent, this.bytesSent,
							EchoServerChannel.MESSAGE_SIZE - this.bytesSent, this);
					if (!sent) {
						System.out.println("Client failed to send message");
					}
				} else {
					System.out.println("Client finished sending message");
				}
			}
		};

		boolean sent = channel.write(EchoServerChannel.messageContent, 0, EchoServerChannel.MESSAGE_SIZE,
				listenerWrite);
		if (!sent) {
			System.out.println("Client failed to send message");
		}
	}

	@Override
	public void refused() {
		System.out.println("Connection refused");
	}
}
package tests;

import java.util.ArrayList;
import java.util.List;

import event.EventPump;
import event.Task;
import imessages.IMessageQueue;
import imessages.IQueueBroker;
import imessages.Message;
import messages.QueueBroker;

public class EchoServerQueue {

	public final static int MESSAGE_SIZE = 255;

	public static void main(String[] args) {

		WorkingMultipleClientSending();
		
		ServerStopBinding();

		// Démarrage du système d'événements
		EventPump.getInstance().start();
	}
	
	private static void ServerStopBinding() {
		// Initialisation de la tâche principale
		Task mainTask = new Task("Test Queue - Server Stop Binding");

		// Lancement du serveur
		Runnable serverRunnable = new Runnable() {
			@Override
			public void run() {
				IQueueBroker broker = new QueueBroker("serverStopBinding");
				MyQueueAcceptListener listener = new MyQueueAcceptListener();
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
				IQueueBroker broker = new QueueBroker("clientStopBinding");
				MyQueueConnectListener listener = new MyQueueConnectListener();
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
		Task mainTask = new Task("Test Queue - Working Multiple Client Sending");

		// Lancement du serveur
		Runnable serverRunnable = new Runnable() {
			@Override
			public void run() {
				IQueueBroker broker = new QueueBroker("serverWorkingMultipleClientSending");
				MyQueueAcceptListener listener = new MyQueueAcceptListener();
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

			Runnable clientRunnable = new Runnable() {
				@Override
				public void run() {
					IQueueBroker broker = new QueueBroker("client" + clientId);
					MyQueueConnectListener listener = new MyQueueConnectListener();
					boolean connected = broker.connect("serverWorkingMultipleClientSending", 8080, listener);

					if (!connected) {
						throw new RuntimeException("Client failed to connect");
					}
				}
			};

			clientRunnables.add(clientRunnable);
		}
		
		for (Runnable clientRunnable : clientRunnables) {
			mainTask.post(clientRunnable);
		}
		
	}
}

class MyEchoServerQueueListener implements IMessageQueue.Listener {

	private IMessageQueue queue;

	public MyEchoServerQueueListener(IMessageQueue queue) {
		this.queue = queue;
	}

	@Override
	public void received(byte[] bytes) {
		System.out.println("Server received message");

		try {
			boolean sent = queue.send(new Message(bytes));

			if (!sent) {
				throw new RuntimeException("Server failed to send response");
			}
		} catch (Exception e) {
			throw new RuntimeException("Server failed to send message: " + e.getMessage());
		}
	}

	@Override
	public void closed() {
		System.out.println("Server finished");
	}

	@Override
	public void sent(Message message) {
		System.out.println("Server sent response");
	}
}

class MyEchoClientQueueListener implements IMessageQueue.Listener {

	private final IMessageQueue queue;

	public MyEchoClientQueueListener(IMessageQueue queue) {
		this.queue = queue;
	}

	@Override
	public void received(byte[] bytes) {
		System.out.println("Client received response");

		int messageSize = EchoServerQueue.MESSAGE_SIZE;
		byte[] messageContent = new byte[messageSize];
		for (int i = 0; i < messageSize; i++) {
			messageContent[i] = (byte) (i + 1);
		}

		for (int i = 0; i < messageSize; i++) {
			if (bytes[i] != messageContent[i]) {
				throw new RuntimeException("Client received incorrect response");
			}
		}

		queue.close();

		System.out.println("Test passed");
	}

	@Override
	public void closed() {
		System.out.println("Client finished");
	}

	@Override
	public void sent(Message message) {
		System.out.println("Client sent message");
	}

}

class MyQueueAcceptListener implements IQueueBroker.AcceptListener {
	@Override
	public void accepted(IMessageQueue queue) {
		System.out.println("Server accepted connection");

		MyEchoServerQueueListener listener = new MyEchoServerQueueListener(queue);
		queue.setListener(listener);
	}

}

class MyQueueConnectListener implements IQueueBroker.ConnectListener {
	@Override
	public void connected(IMessageQueue queue) {
		System.out.println("Connection established for client");

		int messageSize = EchoServerQueue.MESSAGE_SIZE;
		byte[] messageContent = new byte[messageSize];
		for (int i = 0; i < messageSize; i++) {
			messageContent[i] = (byte) (i + 1);
		}

		MyEchoClientQueueListener listener = new MyEchoClientQueueListener(queue);
		queue.setListener(listener);

		Message message = new Message(messageContent, 0, messageSize);
		boolean sent = queue.send(message);
		if (!sent) {
			throw new RuntimeException("Client failed to send message");
		}
	}

	@Override
	public void refused() {
		System.out.println("Connection refused");
	}

}
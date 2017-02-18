package Triqui;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Observable;

public class Comunicacion extends Observable implements Runnable{
	
	private MulticastSocket socket;
	private final int PORT = 5000;
	private final String GROUP_ADDRESS = "224.2.2.2";
	private InetAddress ia;
	private int identifier;
	private boolean identified;

	public Comunicacion() {
		try {
			socket = new MulticastSocket(PORT);
			ia = InetAddress.getByName(GROUP_ADDRESS);
			socket.joinGroup(ia);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		grupo();


		try {
			socket.setSoTimeout(2000); 
			while (!identified) {
				respuestas();
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void grupo(){
		Serializador mensaje = new Serializador("Hi i'm a new member");
		byte[] bytes = serialize(mensaje);
		try {
			sendMessage(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void respuestas() throws IOException {
		try {
			DatagramPacket receivedPacket = receiveMessage();
			Object receivedObject = deserialize(receivedPacket.getData());

			if (receivedObject instanceof Serializador) {
				Serializador message = (Serializador) receivedObject;
				String messageContent = message.getMensaje();

				if (messageContent.contains("I am:")) { 
					String[] partes = messageContent.split(":");
					int externalID = Integer.parseInt(partes[1]);
					if (externalID >= identifier) {
						identifier = externalID + 1;
					}
				}
			}

		} catch (SocketTimeoutException e) {
			identified = true;
			socket.setSoTimeout(0);
			System.out.println("My AutoID is: " + identifier);
		}

	}

	private void gracias() {
		Serializador message = new Serializador("Hi, I am:" + identifier);
		byte[] bytes = serialize(message);
		try {
			sendMessage(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private byte[] serialize(Object data) {
		byte[] bytes = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(data);
			bytes = baos.toByteArray();
			oos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	private Object deserialize(byte[] bytes) {
		Object data = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			data = ois.readObject();
			ois.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return data;
	}

	public void sendMessage(byte[] data) throws IOException {
		DatagramPacket packet = new DatagramPacket(data, data.length, ia, PORT);
		socket.send(packet);
	}

	public DatagramPacket receiveMessage() throws IOException {
		byte[] buffer = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		socket.receive(packet);
		System.out.println("Se recibe");
		return packet;
	}

	@Override
	public void run() {
		while (true) {
			if (socket != null) {
				try {
					DatagramPacket receivedPacked = receiveMessage();
					Object receivedObject = deserialize(receivedPacked.getData());
					if (receivedObject != null) {
						if (receivedObject instanceof Serializador) {
							Serializador message = (Serializador) receivedObject;
							String messageContent = message.getMensaje();
							if (messageContent.contains("new member")) {
								gracias();
							}
						}
						System.out.println("NO LLEGO");
						setChanged();
						notifyObservers(receivedObject);
						clearChanged();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public int getIdentifier() {
		// TODO Auto-generated method stub
		return this.identifier;
	}

}
package threadchatserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientThread extends Thread
{
	private Socket socket = null;
	private Client client = null;
	private DataInputStream input = null;
	
	public ClientThread(Client client, Socket socket)
	{
		System.out.println("adad");
		this.client = client;
		this.socket = socket;
		open();
		start();
	}

	public void open()
	{
		try
		{
			input = new DataInputStream(socket.getInputStream());
		}
		catch (IOException e)
		{
			System.out.println("Error getting InputStream: " + e.getMessage());
			client.stop();
		}
		
	}
	public void close()
	{
		try
		{
			if (input != null)
			{
				input.close();
			}
		}
		catch (IOException e)
		{
			System.out.println("Error closing input stream: " + e.getMessage());
		}
	}
	
	public void run()
	{
		while (true)
		{
			try
			{
				client.handle(input.readUTF());
			}
			catch (IOException e)
			{
				System.out.println("Listening error: " + e.getMessage());
				client.stop();
			}
			
		}
	}
	
}

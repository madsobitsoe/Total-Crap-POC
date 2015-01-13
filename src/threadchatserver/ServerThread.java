package threadchatserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import observerpattern.Observer;

public class ServerThread extends Thread implements Observer 
{	
	private Server server = null;
	private Socket socket = null;
	private int ID = -1;
	private DataInputStream in = null;
	private DataOutputStream out = null;
	
	
	public ServerThread (Server server, Socket socket) 
	{
		super();
		this.server = server;
		this.socket = socket;
		this.ID = socket.getPort();
	}
	
	public int getID()
	{
		return this.ID;
	}
	public void run()
	{
		System.out.println("Server Thread " + ID + "running...");
		while (true)
		{
			try 
			{
				server.handle(ID, in.readUTF());
			}
			catch (IOException e)
			{
				System.out.println(ID + " ERROR reading: " + e.getMessage());
				server.remove(ID);
				stop();
			}
		}
	}
	
	
	public void open() throws IOException
	{
		in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	}
	
	public void close() throws IOException
	{
		if (socket != null) socket.close();
		if (in != null) in.close();
		if (out != null) out.close();
	}
	
	@Override
	public void update(String message)
	{
		try 
		{
			out.writeUTF(message);
			out.flush();
		}
		catch (IOException e)
		{
			System.out.println(this.ID + " ERROR sending " + e.getMessage());
			server.remove(ID);
			stop();
		}
	}
	public void send(String msg)
	{
		update(msg);
	}
}
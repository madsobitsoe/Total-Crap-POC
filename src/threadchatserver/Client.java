package threadchatserver;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import observerpattern.Observer;

public class Client  implements Runnable  
{
	private Socket socket = null;
	private DataInputStream input = null;
	private DataOutputStream dos = null;
	private ClientThread client = null;
	
	private JFrame frame;
	final JTextArea messages, newField;
	final JButton btn;
	
	public Client()
	{
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Simple Java Chat");
		messages = new JTextArea(10, 50);
		newField = new JTextArea(3,50);
		try 
		{
			socket = new Socket("localhost", 12398);
			start();
		}
		catch(Exception e)
		{ 
			System.out.println(e.getMessage());
		}
		
		btn = new JButton("Send");
		btn.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				try 
				{
					sendMessage();
				} 
				catch (IOException e) 
				{
					System.out.println("Error sending message: " + e.getMessage());
				}
			}
		});
		
		frame.setLayout(new BorderLayout(10, 10));
		frame.add(messages, BorderLayout.NORTH);
		frame.add(newField, BorderLayout.CENTER);
		frame.add(btn, BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
		
	}

	public void start() throws IOException
	{
		dos = new DataOutputStream(socket.getOutputStream());
		if (client == null)
      {  
			client = new ClientThread(this, socket);
      }
			
	}
	
	private void sendMessage() throws IOException
	{
		if (newField.getText() != null)
		{
		dos.writeUTF(newField.getText());
		dos.flush();
		newField.setText("");
		}
	}

	
	public static void main(String[] args) throws IOException 
	{
		new Client();
	}
	@Override
	public void run()
	{
		// Nothing to do here
		
	}
	
	public void stop()
	{
		
		if (client != null)
		{
			client.stop();
			client = null;
		}
		try {
			if (dos != null) dos.close();
			if (socket != null) socket.close();
		}
		catch (IOException e)
		{
			client.close();
		}
	}
	
	public void handle(String msg)
	{
		if (msg != null)
		{
			messages.append("\n" + msg);
		}
	}	
}
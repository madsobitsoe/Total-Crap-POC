package threadchatserver;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import observerpattern.Observer;
import observerpattern.Subject;

public class Server implements Runnable, Subject
{
	private ServerThread observers[] = new ServerThread[10];
	//private ArrayList<ServerThread> observers = new ArrayList<ServerThread>();
	private ServerSocket listener = null;
	private Thread thread = null;
	private int clientCount = 0;

	int LISTENING_PORT = 12398;
	
	// Swing stuff
	JFrame frame;
	JTextArea messageArea;
	JButton startButton, stopButton;

	public Server()
	{
		try 
		{
			listener = new ServerSocket(LISTENING_PORT);
			setupGui();
			
			start();
		}
		catch (IOException e)
		{
			System.out.println("Can not bind to port " + LISTENING_PORT + ": " + e.getMessage());
		}
	}

	public void setupGui()
	{
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout(10, 10));
		messageArea = new JTextArea(10, 50);
		startButton = new JButton("Start Server");
		stopButton = new JButton("Stop Server");
		messageArea.setText("Server v. 0.1");
		/*startButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if (!running)
				{
					try 
					{
						System.out.println("Yeah " + running);
						start();
						System.out.println("Started");
					}
					catch (IOException e)
					{
						System.out.println(e.getMessage());
					}
				}
			}
		});

		stopButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if (running)
				{
					stop();
				}
			}


		});*/

		frame.add(messageArea, BorderLayout.NORTH);
		frame.add(startButton, BorderLayout.WEST);
		frame.add(stopButton, BorderLayout.EAST);
		frame.pack();
		frame.setVisible(true);


	}

	private void start()
	{
		if (thread == null)
		{
			thread = new Thread(this);
			thread.start();
		}
	}

	public void stop()
	{
		if (thread != null)
		{
			thread.stop();
			thread = null;
		}

	}

	private int findClient(int ID)
	{
		for (int i = 0; i < clientCount; i++)
		{
			if (observers[i].getID() == ID)
			{
				return i;
			}

		}
		return -1;
	}



	public void run()
	{
		messageArea.setText(messageArea.getText() + "Starting Server...\nBinding to port: " + LISTENING_PORT + "\n");


		while (thread != null)
		{
			try 
			{
				messageArea.setText(messageArea.getText() + "Waiting for connection...\n");
				addThread(listener.accept());
			}	
			catch (IOException e)
			{
				e.printStackTrace();
			}
			

		}

	}




	public synchronized void remove(int ID)
	{
		int pos = findClient(ID);
		if (pos >= 0)
		{
			ServerThread threadToTerminate = observers[pos];
			messageArea.append("\nRemoving client thread " + ID + " at index " + pos + ".");
			if (pos < clientCount-1)
			{
				for (int i = pos+1; i < clientCount; i++)
				{
					observers[i-1] = observers[i];
				}

			}
			clientCount--;
			try
			{ threadToTerminate.close(); }
			catch(IOException e)
			{
				System.out.println("Error closing thread " + e.getMessage());
				threadToTerminate.stop();
			}
		}

	}


	private void addThread(Socket socket)
	{
		if (clientCount < observers.length)
		{
			messageArea.append("\nClient accepted: " + socket.getInetAddress());
			//observers.add(new ServerThread(this, socket));
			
			observers[clientCount] = new ServerThread(this, socket);
			//registerObserver(new ServerThread(this, socket));
			try 
			{
				observers[clientCount].open();
				observers[clientCount].start();
				clientCount++; 
			}
			catch(IOException e)
			{  
				System.out.println("Error opening thread: " + e); 
			} 

		}
	}

	public synchronized void handle(int ID, String input)
	{
		for (int i = 0; i < clientCount; i++)
		{
			observers[i].send(ID + ": " + input);
		}

	}
	/**
	 * Runs the server.
	 */
	public static void main(String[] args) throws IOException {

		new Server();

	}

	@Override
	public void registerObserver(Observer o)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeObserver(Observer o)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyAllObservers(String message)
	{
		// TODO Auto-generated method stub
		
	}

	
}
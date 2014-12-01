import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import javax.swing.Timer;
import java.util.concurrent.locks.*;
import java.awt.*;
import java.awt.event.*;

public class GameServer 
{
	private static ReentrantLock queryLock = null;
	private static ServerSocket server = null;
	private static int numClients = 0;
	private static ArrayList<ServerHandler> twoQueue = new ArrayList<ServerHandler>();
	private static ArrayList<ServerHandler> threeQueue = new ArrayList<ServerHandler>();
	private static ArrayList<RunningGame> runningGames = new ArrayList<RunningGame>();
	
	public static void main(String[] args) throws IOException
	{
	    try 
	    {
	    	if(queryLock == null)
	    		queryLock = new ReentrantLock();
			Class.forName(ServerInfo.DRIVER);
	    	server = new ServerSocket(ServerInfo.portNumber);
			ServerInfo.conn = DriverManager.getConnection(ServerInfo.DB_ADDRESS + ServerInfo.DB_NAME, ServerInfo.USER, ServerInfo.PASSWORD);
	    } 
	    catch (IOException e) 
	    {
	    	e.printStackTrace();
	    	System.exit(0);
	    }
		catch (ClassNotFoundException c)
		{
			System.out.println("ERROR: Driver not found.");
		}
		catch (SQLException s)
		{
			System.out.println("ERROR: SQL error 2.");
		}
	    
	    try
	    {
			PreparedStatement createUserTable = ServerInfo.conn.prepareStatement("CREATE TABLE USER(username VARCHAR(50) NOT NULL, password VARCHAR(50) NOT NULL, PRIMARY KEY(username));");
			createUserTable.executeUpdate();
	    }
	    catch (SQLException e) { } // Already Created!
	    
	    try
	    {
	    	while(true)
	    	{
	    		new ServerHandler(server.accept()).start();
	    	}
	    }
	    finally
	    {
	    	server.close();
	    }
	    
	}
	
	public static class ServerHandler extends Thread
	{
		public Socket client;
		public BufferedReader inputLine;
		public PrintStream output;
		public String userName;
		
		public ServerHandler(Socket s)
		{
			client = s;
			numClients++;
		}
		
		public void run()
		{
	    	try 
	    	{
    			inputLine = new BufferedReader(new InputStreamReader(client.getInputStream()));
	    		output = new PrintStream(client.getOutputStream());
	    		userName = "Anonymous-" + numClients;
	    		
	    		while (true) 
	    		{
	    			
	    			String line = inputLine.readLine();
	    			System.out.println("Line: " + line);
		    		Scanner inputStream = new Scanner(line);
		    		String command = inputStream.next();
		    		
		    		if(command.equals("LOGIN"))
		    		{
		    			command = inputStream.next();
	    				String username = inputStream.next();
	    				String password = inputStream.next();
	    				
		    			if(command.equals("NEW"))
		    			{
		    				PreparedStatement hasUser = ServerInfo.conn.prepareStatement("SELECT * FROM USER WHERE username = '" + username + "';");
		    				ResultSet results = hasUser.executeQuery();
		    				
		    				if(results.next())
		    				{
		        				output.println("RED");
		        				output.println("FAILURE: Username Already Exists!");
		    				}
		    				else
		    				{
			    				PreparedStatement insertState = ServerInfo.conn.prepareStatement("INSERT INTO USER (username, password) VALUES (?, ?);");
			    				insertState.setString(1, username);
			    				insertState.setString(2, password);
			    				insertState.execute();
		        				output.println("GREEN");
			    				output.println("SUCCESS: Created A New User!");
		    				}
		    				results.close();
		    			}
		    			else if(command.equals("CURRENT"))
		    			{
		    				System.out.println("SELECT * FROM USER WHERE username = '" + username + "' AND password = '" + password + "';");
		    				PreparedStatement hasUser = ServerInfo.conn.prepareStatement("SELECT * FROM USER WHERE username = '" + username + "' AND password = '" + password + "';");
		    				ResultSet results = hasUser.executeQuery();
	
		    				if(!results.next())
		    				{
		        				output.println("RED");
		        				output.println("FAILURE: Invalid Username OR Password!");
		    				}
		    				else
		    				{
		        				output.println("GREEN");
			    				output.println("SUCCESS: Welcome " + username + "!");
			    				userName = username;
		    				}
		    				results.close();
		    			}
		    		}
		    		else if(command.equals("MESSAGE"))
		    		{
	    				String username = inputStream.next();
	    				System.out.println(username);
	    				String message = inputStream.nextLine();
		    			output.println(username + " RED " + message);
		    		}
		    		else if(command.equals("QUEUE"))
		    		{
		    			int queueNum = Integer.parseInt(inputStream.next());
		    			if(queueNum == 1)
		    			{
		    				runningGames.add(new RunningGame(this, null, null));
			    			output.println("START");
		    			}
		    			else if(queueNum == 2)
		    				twoQueue.add(this);
		    			else if(queueNum == 3)
		    				threeQueue.add(this);
		    			createTwoOrThreeGames();
		    		}
		    		inputStream.close();
		    	} 
		    }
	    	catch (IOException e) 
	    	{
	    		
	    	}
			catch(SQLException s)
			{
				
			}
	    }
		
		public void createTwoOrThreeGames()
		{
			if(twoQueue.size() >= 2)
			{
				runningGames.add(new RunningGame(twoQueue.get(0), twoQueue.get(1), null));
				twoQueue.remove(0);
				twoQueue.remove(1);
				output.println("START");
			}
			if(threeQueue.size() >= 3)
			{
				runningGames.add(new RunningGame(threeQueue.get(0), threeQueue.get(1), threeQueue.get(2)));
				threeQueue.remove(0);
				threeQueue.remove(1);
				threeQueue.remove(2);
				output.println("START");
			}
		}
	}

	public static class RunningGame extends Thread implements ActionListener
	{
		private ArrayList<ServerHandler> players = new ArrayList<ServerHandler>();
		private Timer timer;
		private int time, roundNum;
		private String givenWord, bonusWord;
		
		public RunningGame(ServerHandler player1, ServerHandler player2, ServerHandler player3)
		{
			players.add(player1);
			if(player2 != null)
				players.add(player2);
			if(player3 != null)
				players.add(player3);
			timer = new Timer(1000, this);
			
			time = 90;
			roundNum = 1;

			try
			{
				PreparedStatement hasUser = ServerInfo.conn.prepareStatement("SELECT * FROM DICTIONARY ORDER BY RAND() LIMIT 1;");
				ResultSet results = hasUser.executeQuery();
				results.next();
				givenWord = results.getString(0);
			}
			catch (SQLException e)
			{
				
			}

			for(int i = 0; i < players.size(); i++)
				players.get(i).output.println("START " + roundNum + " ");
		}
		
		public void run()
		{
			for(int i = 0; i < players.size(); i++)
			{
				if(time != 0)
    				players.get(i).output.println("DONE " + givenWord);
				else
					players.get(i).output.println("UPDATE " + time);
			}
		}
		
		public void actionPerformed(ActionEvent e)
		{
			if(e.getSource() == timer)
				time--;
		}
	}
}

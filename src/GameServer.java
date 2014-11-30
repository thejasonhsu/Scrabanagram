import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class GameServer 
{
	private static ReentrantLock queryLock = null;
	private static ServerSocket server = null;
	private static int numClients = 0;
	
	private static ArrayList<Socket> clients = new ArrayList<Socket>();
	private static ArrayList<BufferedReader> inputLine = new ArrayList<BufferedReader>();
	private static ArrayList<PrintStream> output = new ArrayList<PrintStream>();
	private static ArrayList<String> userNames = new ArrayList<String>();

	public static void main(String[] args) 
	{
	    try 
	    {
	    	if(queryLock == null)
	    		queryLock = new ReentrantLock();
			Class.forName(ServerInfo.DRIVER);
	    	server = new ServerSocket(ServerInfo.portNumber);
			ServerInfo.conn = DriverManager.getConnection(ServerInfo.DATABASE_ADDRESS + ServerInfo.DATABASE_NAME, ServerInfo.USER, ServerInfo.PASSWORD);
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
	    
	    while (true) 
	    {
	    	try 
	    	{
	    		Socket newClient = server.accept();
	    		if(!isConnected(newClient))
	    		{
    				numClients++;
	    			clients.add(newClient);
	    			inputLine.add(new BufferedReader(new InputStreamReader(newClient.getInputStream())));
		    		output.add(new PrintStream(newClient.getOutputStream()));
		    		userNames.add("Anonymous-" + numClients);
	    		}
	    		
	    		int index = getStreamNumber(newClient); 
	    		Scanner inputStream = new Scanner(inputLine.get(index).readLine());
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
	        				output.get(index).println("RED");
	        				output.get(index).println("FAILURE: Username Already Exists!");
	    				}
	    				else
	    				{
		    				PreparedStatement insertState = ServerInfo.conn.prepareStatement("INSERT INTO USER (username, password) VALUES (?, ?);");
		    				insertState.setString(1, username);
		    				insertState.setString(2, password);
		    				insertState.execute();
	        				output.get(index).println("GREEN");
		    				output.get(index).println("SUCCESS: Created A New User!");
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
	        				output.get(index).println("RED");
	        				output.get(index).println("FAILURE: Invalid Username OR Password!");
	    				}
	    				else
	    				{
	        				output.get(index).println("GREEN");
		    				output.get(index).println("SUCCESS: Welcome " + username + "!");
		    				userNames.set(index, username);
	    				}
	    				results.close();
	    			}
	    		}
	    		inputStream.close();
	    		newClient.close();
	    	} 
	    	catch (IOException e) 
	    	{
	    		
	    	}
			catch(SQLException s)
			{
				
			}
	    }
	}
	
	public static boolean isConnected(Socket s)
	{
		for(int i = 0; i < clients.size(); i++)
			if(clients.get(i).equals(s))
				return true;
		return false;
	}

	public static int getStreamNumber(Socket s)
	{
		for(int i = 0; i < clients.size(); i++)
			if(clients.get(i).equals(s))
				return i;
		return -1;
	}
}

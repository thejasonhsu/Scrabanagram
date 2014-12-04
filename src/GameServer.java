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
			s.printStackTrace();
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
		public int score[] = new int[3];
		
		private final int LETTER_VALUES[] = {1, 3, 3, 2, 1,
											 4, 2, 4, 1, 8,
											 5, 1, 3, 1, 1,
											 3, 10, 1, 1, 1,
											 1, 4, 4, 8, 4, 10};
		private TreeSet<String> goodWords;
		private String goodBank;
		private int gameNumber = -1;
		
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
	    				
	    				System.out.println("GAME NUMBER: " + gameNumber);
	    				for(int i = 0; i < runningGames.get(gameNumber).players.size(); i++)
	    					runningGames.get(gameNumber).players.get(i).output.println("MESSAGE " + username + " RED " + message);
		    		}
		    		else if(command.equals("QUEUE"))
		    		{
		    			int queueNum = Integer.parseInt(inputStream.next());
		    			if(queueNum == 1)
		    			{
			    			output.println("START");
							gameNumber = runningGames.size();
		    				runningGames.add(new RunningGame(this, null, null));
		    				runningGames.get(runningGames.size() - 1).start();
		    			}
		    			else if(queueNum == 2)
		    				twoQueue.add(this);
		    			else if(queueNum == 3)
		    				threeQueue.add(this);
		    			createTwoOrThreeGames();
		    		}
		    		else if(command.equals("CONTINUE"))
		    		{
	    				output.println("START");
	    				if(runningGames.get(gameNumber).time <= 0)
	    					runningGames.get(gameNumber).startGame();
	    				else
	    					this.output.println("WORD " + runningGames.get(gameNumber).givenWord + " " + runningGames.get(gameNumber).roundNum);
		    		}
		    		else if(command.equals("SCORE"))
		    		{
		    			int round = 0;
	    				String bank = inputStream.nextLine(), given = "";
	    				
	    				given = runningGames.get(gameNumber).givenWord;
	    				round = runningGames.get(gameNumber).roundNum;
	    				
	    				goodBank = "";
	    				goodWords = new TreeSet<String>();
	    				score[round - 1] = getScore(bank, given);
	    				if(round < 3)
	    					output.println("SCORE " + round + " " + score[round - 1] + " " + goodBank);
	    				else
	    				{
	    					output.println("FINAL " + round + " " + score[0] + " " + score[1] + " " + score[2] + " " + goodBank);
	    					runningGames.get(gameNumber).doneCount++;
	    					while(runningGames.get(gameNumber).doneCount < runningGames.get(gameNumber).players.size()) ;
	    					runningGames.get(gameNumber).end();
	    					runningGames.set(gameNumber, null);
	    				}
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
		
		public void createTwoOrThreeGames() throws IOException
		{
			if(twoQueue.size() >= 2)
			{
				for(int i = 0; i < 2; i++)
				{
					twoQueue.get(i).output.println("START");
					twoQueue.get(i).gameNumber = runningGames.size();
				}
				runningGames.add(new RunningGame(twoQueue.get(0), twoQueue.get(1), null));
				runningGames.get(runningGames.size() - 1).start();
				twoQueue.remove(0);
				twoQueue.remove(0);
			}
			if(threeQueue.size() >= 3)
			{
				for(int i = 0; i < 3; i++)
				{
					threeQueue.get(i).output.println("START");
					threeQueue.get(i).gameNumber = runningGames.size();
				}
				runningGames.add(new RunningGame(threeQueue.get(0), threeQueue.get(1), threeQueue.get(2)));
				runningGames.get(runningGames.size() - 1).start();
				threeQueue.remove(0);
				threeQueue.remove(0);
				threeQueue.remove(0);
			}
		}

		public int getScore(String bank, String given)
		{
			Scanner input = new Scanner(bank);
			input.useDelimiter(" ");
			
			int total = 0;
			while(input.hasNext())
				goodWords.add(input.next().toLowerCase());
			
			Iterator<String> iter = goodWords.iterator();
			while(iter.hasNext())
			{
				String word = iter.next();
				System.out.println("WORD: " + word);
				if(isAnagram(word, given) && isValidWord(word) && word.length() >= 3)
				{
					total += scoreWord(word);
					goodBank += (word + " " + scoreWord(word) + " ");
					System.out.println("WORD: " + word + ", SCORE: " + scoreWord(word));
				}
			}
			if(total > 0)
				goodBank = goodBank.substring(0, goodBank.length() - 1);
			input.close();
			return total;
		}
		
		private int scoreWord(String word)
		{
			int total = 0;
			for(int i = 0; i < word.length(); i++)
				total += LETTER_VALUES[word.charAt(i) - 'a'];
			return total;
		}
		
		private boolean isAnagram(String word, String givenWord)
		{
			String tempWord = givenWord;
			for(int i = 0; i < word.length(); i++)
			{
				int loc = tempWord.indexOf(word.charAt(i));
				if(loc == -1)
					return false;
				tempWord = tempWord.substring(0, loc) + tempWord.substring(loc + 1);
			}
			return true;
		}

		private boolean isValidWord(String word)
		{
			try
			{
				PreparedStatement hasUser = ServerInfo.conn.prepareStatement("SELECT * FROM DICTIONARY WHERE word = '" + word + "';");
				ResultSet results = hasUser.executeQuery();
				boolean hasResult = results.next();
				
				results.close();
				return hasResult;
			}
			catch(SQLException e)
			{
				return false;
			}
		}
	}
	
	public static class RunningGame extends Thread implements ActionListener
	{
		private ArrayList<ServerHandler> players = new ArrayList<ServerHandler>();
		private Timer timer;
		private int time, roundNum;
		private String givenWord, bonusWord;
		public boolean isReady[];
		private int doneCount = 0;
		
		public RunningGame(ServerHandler player1, ServerHandler player2, ServerHandler player3)
		{
			System.out.println("CREATE");
			players.add(player1);
			if(player2 != null)
				players.add(player2);
			if(player3 != null)
				players.add(player3);
			timer = new Timer(1000, this);
			roundNum = 0;
			startGame();
		}
		
		public void end()
		{
			if(this.isAlive())
			{
				this.interrupt();
			}
		}
		
		public void startGame()
		{
			time = 30;
			roundNum++;

			try
			{
				PreparedStatement hasUser = ServerInfo.conn.prepareStatement("SELECT * FROM DICTIONARY ORDER BY RAND() LIMIT 1;");
				ResultSet results = hasUser.executeQuery();
				results.next();
				givenWord = results.getString("word");
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}

			for(int i = 0; i < players.size(); i++)
				players.get(i).output.println("WORD " + givenWord + " " + roundNum);
			timer.start();
		}
		
		public void actionPerformed(ActionEvent e)
		{
			if(e.getSource() == timer)
			{
				time--;
				System.out.println("SOURCE: ");
				if(time == 0)
					timer.stop();
				System.out.println("TIMER: " + time);
				for(int i = 0; i < players.size(); i++)
				{
					if(time <= 0)
					{
	    				players.get(i).output.println("DONE " + givenWord);
					}
					else
						players.get(i).output.println("UPDATE " + time);
				}
			}
		}
	}
}

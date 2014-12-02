import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;

public class GameClient extends JFrame implements ActionListener
{
	private enum STATE {INITIAL, MAIN, GAME, STATUS, FINAL};

	private JPanel mainPanel = new JPanel();
	private Timer timer;
	private STATE currState = STATE.INITIAL;
	private JProgressBar progressBar = new JProgressBar();
	private Animation motion;
	private JButton gameStartButton, instructionsButton, userLoginButton, backButton;
	private Login login = null; 
	private String username = "";
	private Label userLabel; 
	private ChatBox chat = null;
	private GameBox game = null;
	private String round, score, bank, scores[] = new String[3];
	private static int timeLeft = 60;
	private JButton backToMainButton = new JButton("Bank To Main");

	private Socket clientSocket = null;
	private ClientHandler client;

	public GameClient()
	{
		super("Scrabanagram!");
		add(setupTitleScreen());

	    try 
	    {
	        clientSocket = new Socket(ServerInfo.host, ServerInfo.portNumber);
	    } 
	    catch (UnknownHostException u) 
	    {
	        System.out.println("ERROR: Unknown Host!");
	    } 
	    catch (IOException u) 
	    {
	        System.out.println("ERROR: Problem Reading Host!");
	    }
	    
		setSize(800, 600);
		setVisible(true);
		setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public JPanel setupTitleScreen()
	{
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBackground(Color.WHITE);
		mainPanel.add(new JLabel(new ImageIcon(".\\images\\main.jpg")), BorderLayout.NORTH);

	    progressBar.setStringPainted(true);
	    mainPanel.add(progressBar, BorderLayout.SOUTH);
	    
	    motion = new Animation();
	    mainPanel.add(motion, BorderLayout.CENTER);
	    
	    timer = new Timer(100, this);
	    timer.start();
	    
		return mainPanel;
	}
	
	public void setupMainScreen()
	{
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(new JLabel(new ImageIcon(".\\images\\main.jpg")), BorderLayout.NORTH);

		JPanel centerPanel = new JPanel();
		centerPanel.setPreferredSize(new Dimension(448, 390));
		centerPanel.setLayout(new GridLayout(3, 1, 20, 20));
		gameStartButton = new JButton(new ImageIcon(".\\images\\gamestart.png"));
		gameStartButton.addActionListener(this);
		centerPanel.add(gameStartButton);
		instructionsButton = new JButton(new ImageIcon(".\\images\\instructions.png"));
		instructionsButton.addActionListener(this);
		centerPanel.add(instructionsButton);
		userLoginButton = new JButton(new ImageIcon(".\\images\\userlogin.png"));
		userLoginButton.addActionListener(this);
		centerPanel.add(userLoginButton);
		centerPanel.setBackground(Color.WHITE);
		
		centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 173, 170, 173));
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		
		userLabel = new Label(" User: " + username);
		userLabel.setFont(new Font("Arial", Font.BOLD, 20));
		mainPanel.add(userLabel, BorderLayout.SOUTH);
	}
	
	public void setupInstructionsPanel()
	{
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBackground(Color.WHITE);
		
		JLabel label = new JLabel(" Instructions:");
		label.setFont(new Font("Courier", Font.BOLD, 24));
		mainPanel.add(label, BorderLayout.NORTH);
		
		JPanel textPanel = new JPanel(), buttonPanel = new JPanel();
		JTextArea area = new JTextArea(" Objective of this game is to have the highest score by the end\n"
									 + " of 3 rounds.  At the start of each round, a given word will be\n"
									 + " presented to each player (max of 4 players per game). The goal\n"
									 + " is to find as many anagrams as possible within the given word\n"
									 + " within the time constraint of one minute and half. The words\n"
									 + " found must be at least 3 letters or more.\n\n"
									 + " Normal Words Are Scored: C3 R1 A1 B3 = 8 points.\n"
									 + " Special Word Found:      Word Score + 15 points.\n\n"
									 + " Along the way, there are also other \"surprises\" in this game\n"
									 + " as well, be prepared to change things up if needed.\n");
		area.setFont(new Font("Courier", Font.PLAIN, 20));
		area.setEditable(false);
		textPanel.setLayout(new GridLayout(1, 1));
		textPanel.add(area);
		textPanel.setBackground(Color.WHITE);
		mainPanel.add(textPanel, BorderLayout.CENTER);
		
		buttonPanel.setLayout(new GridLayout(1, 1, 15, 200));
		backButton = new JButton("Back");
		backButton.addActionListener(this);
		buttonPanel.add(backButton);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
	}

	public void setupGamePanel()
	{
		chat.setPreferredSize(new Dimension(200, 600));
		chat.setBackground(Color.LIGHT_GRAY);
		add(chat, BorderLayout.EAST);
		
		game.setPreferredSize(new Dimension(600, 600));
		add(game, BorderLayout.CENTER);
	}
	
	private JTable setDataSummary()
	{
		ArrayList<String> bankWords = new ArrayList<String>();
		ArrayList<String> wordValue = new ArrayList<String>();
		
		Scanner input = new Scanner(bank);
		while(input.hasNext())
		{
			bankWords.add(input.next());
			wordValue.add(input.next());
		}
		input.close();
		
		String summary[][] = new String[bankWords.size() + 1][3];
		summary[0][0] = "Word Used";
		summary[0][1] = "Word Value";
		summary[0][2] = "Special Notes";
		String[] inputCol = {"Word Used", "Word Value", "Special Notes"};
		
		for(int i = 0; i < bankWords.size(); i++)
		{
			summary[i + 1][0] = bankWords.get(i);
			summary[i + 1][1] = wordValue.get(i);
		}
		
		JTable summaryTable = new JTable(summary, inputCol);

		// Align the information so it is centralized.
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		summaryTable.getColumn("Word Used").setCellRenderer(centerRenderer);
		summaryTable.getColumn("Word Value").setCellRenderer(centerRenderer);
		summaryTable.getColumn("Special Notes").setCellRenderer(centerRenderer);
		summaryTable.setBorder(BorderFactory.createEmptyBorder(5, 5, 24, 5));
		
		return summaryTable;
	}	
	
	public void setupStatusPanel()
	{
		setLayout(new BorderLayout());

		JLabel label = new JLabel("Round " + round + " Complete!", SwingConstants.CENTER);
		label.setFont(new Font("Arial", Font.PLAIN, 30));
		mainPanel.add(label, BorderLayout.NORTH);
		
		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new GridLayout(1, 2));
		JTable table = setDataSummary();
		table.setBackground(Color.LIGHT_GRAY);
		table.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		statusPanel.add(table);
		statusPanel.setBackground(Color.WHITE);

		JLabel scoreLabel = new JLabel("<html><center>Round Score <br>" + score + "</center></html>", SwingConstants.CENTER);
		scoreLabel.setFont(new Font("Arial", Font.PLAIN, 30));
		statusPanel.add(scoreLabel);
		
		mainPanel.add(statusPanel, BorderLayout.CENTER);
	}
	
	public void setupFinalPanel()
	{
		setLayout(new BorderLayout());

		JLabel label = new JLabel("Round 3 Complete!", SwingConstants.CENTER);
		label.setFont(new Font("Arial", Font.PLAIN, 30));
		mainPanel.add(label, BorderLayout.NORTH);
		
		JPanel statusPanel = new JPanel();
		statusPanel.setLayout(new GridLayout(1, 2));
		JTable table = setDataSummary();
		table.setBackground(Color.LIGHT_GRAY);
		table.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		statusPanel.add(table);
		statusPanel.setBackground(Color.WHITE);

		JPanel finalScore = new JPanel();
		finalScore.setLayout(new GridLayout(4, 1));
		JLabel scoreLabel1 = new JLabel("Round Score 1: " + scores[0], SwingConstants.CENTER);
		scoreLabel1.setFont(new Font("Arial", Font.PLAIN, 30));
		JLabel scoreLabel2 = new JLabel("Round Score 2: " + scores[1], SwingConstants.CENTER);
		scoreLabel2.setFont(new Font("Arial", Font.PLAIN, 30));
		JLabel scoreLabel3 = new JLabel("Round Score 3: " + scores[2], SwingConstants.CENTER);
		scoreLabel3.setFont(new Font("Arial", Font.PLAIN, 30));
		finalScore.add(scoreLabel1);
		finalScore.add(scoreLabel2);
		finalScore.add(scoreLabel3);
		backToMainButton.addActionListener(this);
		finalScore.add(backToMainButton);
		statusPanel.add(finalScore);
		
		mainPanel.add(statusPanel, BorderLayout.CENTER);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == timer && currState == STATE.INITIAL)
		{
			progressBar.setValue(progressBar.getValue() + 1);
			progressBar.repaint();
			if(progressBar.getValue() == 100)
			{
				currState = STATE.MAIN;
				motion.dispose();
			}
		}
		else if((e.getSource() == timer && currState == STATE.MAIN) || e.getSource() == backButton)
		{
			mainPanel.removeAll();
			if(timer.isRunning())
				timer.stop();
			setupMainScreen();
			mainPanel.repaint();
			mainPanel.revalidate();
		}
		else if(e.getSource() == timer && currState == STATE.GAME)
		{
			this.remove(chat);
			this.remove(game);
			mainPanel.removeAll();
			timer.stop();
			if(!round.equals("3"))
				setupStatusPanel();
			else
				setupFinalPanel();
			mainPanel.repaint();
			mainPanel.revalidate();
			if(round.equals("3"))
				currState = STATE.FINAL;
			else
			{
				timeLeft = 120;
				timer.start();
				currState = STATE.STATUS;
			}
		}
		else if(e.getSource() == timer && currState == STATE.STATUS)
		{
			timeLeft--;
			if(timeLeft == 0)
			{
				timer.stop();
				currState = STATE.GAME;
				client.output.println("CONTINUE");
			}
		}
		else if(e.getSource() == instructionsButton)
		{
			mainPanel.removeAll();
			setupInstructionsPanel();
			mainPanel.repaint();
			mainPanel.revalidate();
		}
		else if(e.getSource() == userLoginButton)
		{
			if(login == null)
			{
				login = new Login();
				login.closeButton.addActionListener(this);
				gameStartButton.setEnabled(false);
				instructionsButton.setEnabled(false);
				userLoginButton.setEnabled(false);
			}
		}
		else if((login != null && e.getSource() == login.closeButton)||(login != null && !login.isVisible()))
		{
			if(!login.getUserName().equals(""))
			{
				username = login.getUserName();
				userLabel.setText(" User: " + username);
			}
			login.dispose();
			login = null;
			gameStartButton.setEnabled(true);
			instructionsButton.setEnabled(true);
			userLoginButton.setEnabled(true);
		}
		else if(e.getSource() == gameStartButton)
		{
			Object options[] = {"1 Player", "2 Players", "3 Players"};
			int choice = JOptionPane.showOptionDialog(null, "How many players do you want to play with?", "Question", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
			
			if(client == null)
			{
				client = new ClientHandler(clientSocket, username);
				client.start();
			}
		
			System.out.println("CHOICE: " + choice);
			client.output.println("QUEUE " + (choice + 1));
		}
		else if(e.getSource() == backToMainButton)
		{
			mainPanel.removeAll();
			setupMainScreen();
			mainPanel.repaint();
			mainPanel.revalidate();
			currState = STATE.MAIN;
		}
	}

	public class ClientHandler extends Thread
	{
		private Socket client = null;
		public PrintStream output = null;
		public BufferedReader inputLine = null;
		public String userName;
		
		public ClientHandler(Socket s, String name)
		{
			client = s;
			userName = name;
			
			try
			{
    			inputLine = new BufferedReader(new InputStreamReader(client.getInputStream()));
	    		output = new PrintStream(client.getOutputStream());
			}
			catch (IOException e)
			{
				
			}
		}
		
		public void run()
		{
	    	try 
	    	{
	    		
	    		while (true) 
	    		{
	    			System.out.println("WAITING.");
	    			String line = inputLine.readLine();
	    			System.out.println("Line: " + line);
		    		Scanner inputStream = new Scanner(line);
		    		String command = inputStream.next();
		    		
		    		if(command.equals("MESSAGE"))
		    		{
	    				String username = inputStream.next();
	    				Color color = chat.convertColorString(inputStream.next());
	    				String message = inputStream.nextLine() + "\n";
	    				chat.setText(username, color, message);
		    		}
		    		else if(command.equals("START"))
		    		{
		    			chat = new ChatBox(username, output);
		    			game = new GameBox(username, output);
						currState = STATE.GAME;

		    			mainPanel.removeAll();
		    			setupGamePanel();
		    			mainPanel.repaint();
		    			mainPanel.revalidate();
		    		}
		    		else if(command.equals("WORD"))
		    		{
		    			game.startGame(inputStream.nextLine());
		    		}
		    		else if(command.equals("UPDATE"))
		    		{
		    			game.update(inputStream.next());
		    		}
		    		else if(command.equals("DONE"))
		    		{
		    			game.complete();
		    		}
		    		else if(command.equals("SCORE"))
		    		{
		    			timer.start();
		    			round = inputStream.next();
		    			score = inputStream.next();
		    			bank = inputStream.nextLine();
		    		}
		    		else if(command.equals("FINAL"))
		    		{
		    			timer.start();
		    			round = inputStream.next();
		    			scores[0] = inputStream.next();
		    			scores[1] = inputStream.next();
		    			scores[2] = inputStream.next();
		    			bank = inputStream.nextLine();
		    		}
		    		inputStream.close();
	    		}
	    	}
	    	catch (IOException e)
	    	{
	    		
	    	}
		}
	}
	
	public static void main(String args[])
	{
		new GameClient();
	}
}

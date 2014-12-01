import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import javax.swing.*;

public class GameClient extends JFrame implements ActionListener
{
	private enum STATE {INITIAL, MAIN, GAME, STATUS, FINAL};

	private JPanel mainPanel = new JPanel();
	private Timer timer;
	private STATE currState = STATE.MAIN;
	private JProgressBar progressBar = new JProgressBar();
	private Animation motion;
	private JButton gameStartButton, instructionsButton, userLoginButton, backButton;
	private Login login = null; 
	private String username = "";
	private Label userLabel; 
	private ChatBox chat = null;
	private GameBox game = null;
	private boolean hasStarted = false;

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
		chat = new ChatBox(username, null);
		chat.setPreferredSize(new Dimension(200, 600));
		chat.setBackground(Color.LIGHT_GRAY);
		add(chat, BorderLayout.EAST);

		game = new GameBox(username, null);
		game.setPreferredSize(new Dimension(600, 600));
		add(chat, BorderLayout.CENTER);
	}
	
	public JPanel setupStatusPanel()
	{
		return null;
	}
	
	public JPanel setupFinalPanel()
	{
		return null;
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
			
			mainPanel.removeAll();
			setupGamePanel();
			mainPanel.repaint();
			mainPanel.revalidate();

			System.out.println("CHOICE: " + choice);
			//output.println("QUEUE " + (choice + 1));

			while(!hasStarted);
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
		}
		
		public void run()
		{
	    	try 
	    	{
    			inputLine = new BufferedReader(new InputStreamReader(client.getInputStream()));
	    		output = new PrintStream(client.getOutputStream());
	    		
	    		while (true) 
	    		{
	    			String line = inputLine.readLine();
	    			System.out.println("Line: " + line);
		    		Scanner inputStream = new Scanner(line);
		    		String command = inputStream.next();
		    		
		    		if(command.equals("MESSAGE"))
		    		{
	    				String username = inputStream.next();
	    				Color color = chat.convertColorString(inputStream.next());
	    				String message = inputStream.next() + "\n";
	    				chat.setText(username, color, message);
		    		}
		    		else if(command.equals("START"))
		    		{
		    			hasStarted = true;
		    		}
		    		else if(command.equals("UPDATE"))
		    		{
		    			game.update(inputStream.next());
		    		}
		    		else if(command.equals("DONE"))
		    		{
		    			game.complete();
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

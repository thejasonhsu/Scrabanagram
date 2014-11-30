import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.Timer;

public class GameClient extends JFrame implements ActionListener
{
	private enum STATE {INITIAL, MAIN, GAME, INSTRUCTIONS, STATUS, FINAL};

	private JPanel mainPanel = new JPanel();
	private Timer timer;
	private STATE currState = STATE.INITIAL;
	private JProgressBar progressBar = new JProgressBar();
	private Animation motion;
	private JButton gameStartButton, instructionsButton, userLoginButton, backButton;
	private Login login = null; 
	private String username = "";
	private Label userLabel; 
	
	public GameClient()
	{
		super("Scrabanagram!");
		add(setupTitleScreen());
		
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

	public JPanel setupGamePanel()
	{
		return null;
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
	}
	
	public static void main(String args[])
	{
		new GameClient();
	}
}

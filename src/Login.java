import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class Login extends JFrame implements ActionListener
{
	private JPanel mainPanel, userPanel, passPanel, buttonPanel;
	private JTextField usernameBox, passwordBox;
	private JLabel loginLabel, usernameLabel, passwordLabel, outputLabel;
	private JButton loginButton, newUserButton;
	public JButton closeButton;
	
	private Socket clientSocket = null;
	private PrintStream output = null;
	private BufferedReader inputLine = null;
	private String user = ""; 
	
	public Login()
	{
		super("Login Menu");
		setLayout(new BorderLayout());
		
		loginLabel = new JLabel("Login To Account", SwingConstants.CENTER);
		loginLabel.setFont(new Font("Arial", Font.BOLD, 14));
		add(loginLabel, BorderLayout.NORTH);
		
		userPanel = new JPanel();
		usernameLabel = new JLabel("Username: ", SwingConstants.RIGHT);
		userPanel.add(usernameLabel);
		usernameBox = new JTextField("", 15);
		userPanel.add(usernameBox);
		
		passPanel = new JPanel();
		passwordLabel = new JLabel("Password: ", SwingConstants.RIGHT);
		passPanel.add(passwordLabel);
		passwordBox = new JTextField("", 15);
		passPanel.add(passwordBox);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 3));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,  5,  5,  5));
		loginButton = new JButton("Login");
		loginButton.addActionListener(this);
		buttonPanel.add(loginButton);
		newUserButton = new JButton("Create User");
		newUserButton.addActionListener(this);
		buttonPanel.add(newUserButton);
		closeButton = new JButton("Close");
		closeButton.addActionListener(this);
		buttonPanel.add(closeButton);

		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(3, 1));
		mainPanel.add(userPanel);
		mainPanel.add(passPanel);
		mainPanel.add(buttonPanel);
		add(mainPanel, BorderLayout.CENTER);
		
		outputLabel = new JLabel(" ", SwingConstants.CENTER);
		outputLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		outputLabel.setFont(new Font("Arial", Font.BOLD, 14));
		add(outputLabel, BorderLayout.SOUTH);

		setSize(340, 180);
		setVisible(true);
		setResizable(false);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == loginButton)
		{
			if(usernameBox.getText().equals("") || passwordBox.getText().equals(""))
			{
	        	outputLabel.setForeground(Color.RED);
				outputLabel.setText("Error: Missing Username AND Password.");
			}
			else
			{
			    try 
			    {
			        clientSocket = new Socket(ServerInfo.host, ServerInfo.portNumber);
			        inputLine = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			        output = new PrintStream(clientSocket.getOutputStream());
			        
			        output.println("LOGIN CURRENT " + usernameBox.getText() + " " + passwordBox.getText());
			        outputLabel.setForeground((inputLine.readLine().equals("GREEN")) ? new Color(3, 94, 0) : Color.RED);
			        outputLabel.setText(inputLine.readLine());
			        if(outputLabel.getForeground() != Color.RED)
			        	user = usernameBox.getText();
			    } 
			    catch (UnknownHostException u) 
			    {
			        System.out.println("ERROR: Unknown Host!");
			    } 
			    catch (IOException u) 
			    {
			        System.out.println("ERROR: Problem Reading Host!");
			    }
			}
		}
		else if(e.getSource() == newUserButton)
		{
			if(usernameBox.getText().equals("") || passwordBox.getText().equals(""))
			{
	        	outputLabel.setForeground(Color.RED);
				outputLabel.setText("Error: Missing Username AND Password.");
			}
			else
			{
			    try 
			    {
			        clientSocket = new Socket(ServerInfo.host, ServerInfo.portNumber);
			        inputLine = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			        output = new PrintStream(clientSocket.getOutputStream());
			        
			        output.println("LOGIN NEW " + usernameBox.getText() + " " + passwordBox.getText());
			        outputLabel.setForeground((inputLine.readLine().equals("GREEN")) ? new Color(3, 94, 0) : Color.RED);
			        outputLabel.setText(inputLine.readLine());
			        if(outputLabel.getForeground() != Color.RED)
			        	user = usernameBox.getText();
			    } 
			    catch (UnknownHostException u) 
			    {
			        System.out.println("ERROR: Unknown Host!");
			    } 
			    catch (IOException u) 
			    {
			        System.out.println("ERROR: Problem Reading Host!");
			    }
			}	
		}
	}
	
	public String getUserName()
	{
		return user;
	}
}

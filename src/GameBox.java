import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.imageio.*;
import javax.swing.*;

public class GameBox extends JPanel implements KeyListener
{
	private JPanel middlePanel = new JPanel();
	private JLabel yourLabel, bankLabel, yourTime, roundLabel;
	private JTextField typeWords = new JTextField();
	private JTextArea bankWords = new JTextArea();
	private WordGraph graph;
	
	private PrintStream output = null;
	private String username;
	private String givenWord = "";
	private int round = 0;
	
	public GameBox(String username, PrintStream p)
	{
	    output = p;
	    this.username = username;
		
		setLayout(new BorderLayout());
		
		JPanel topPanel = new JPanel();
		topPanel.add(new JLabel(new ImageIcon(".\\images\\main.jpg")));
		topPanel.setBackground(Color.WHITE);
		
		middlePanel.setLayout(new GridLayout(3, 1, 5, 5));
		middlePanel.setBackground(Color.WHITE);
		
		roundLabel = new JLabel("Round", SwingConstants.CENTER);
		roundLabel.setFont(new Font("Arial", Font.PLAIN, 30));
		middlePanel.add(roundLabel, BorderLayout.NORTH);
		yourTime = new JLabel("Timer: 30", SwingConstants.CENTER);
		middlePanel.add(yourTime, BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(2, 1, 5, 5));
		
		JPanel actionPanel = new JPanel();
		actionPanel.setLayout(new GridLayout(3, 1, 5, 5));
		yourLabel = new JLabel("Your Word", SwingConstants.CENTER);
		actionPanel.add(yourLabel);
		typeWords = new JTextField();
		typeWords.addKeyListener(this);
		actionPanel.add(typeWords);
		bankLabel = new JLabel("Your Bank", SwingConstants.CENTER);
		actionPanel.add(bankLabel);
		
		bottomPanel.add(actionPanel);
		bottomPanel.setBackground(Color.WHITE);
		bottomPanel.setPreferredSize(new Dimension(600, 200));
		bankWords.setEditable(false);
		JScrollPane scroll = new JScrollPane(bankWords);
		bottomPanel.add(scroll);

		add(topPanel, BorderLayout.NORTH);
		add(middlePanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}
	
	public void startGame(String message)
	{
		Scanner input = new Scanner(message);
		
		givenWord = input.next();
		graph = new WordGraph(givenWord);
		graph.setPreferredSize(new Dimension(600, 100));

		round = Integer.parseInt(input.next());
		roundLabel.setText("Round " + round);
		
		middlePanel.add(graph, BorderLayout.SOUTH);
		
		input.close();
	}
	
	public void update(String time)
	{
		yourTime.setText("Timer: " + time);
	}
	
	public void complete()
	{
		yourTime.setText("Timer: 0");
		JOptionPane.showMessageDialog(null, "Time's Up!");
		output.println("SCORE " + convertBank());
		middlePanel.removeAll();
	}
	
	private String convertBank()
	{
		String bank = "";
		Scanner input = new Scanner(bankWords.getText());
		while(input.hasNext())
			bank += (input.next() + " ");
		input.close();
		
		if(bank.equals(""))
			return "";
		else
			return bank.substring(0, bank.length() - 1);
	}
	
	@SuppressWarnings("serial")
	public class WordGraph extends JPanel
	{
		private String word;
		
		public WordGraph(String word)
		{
			this.word = word.toLowerCase();
		}
		
		public void paintComponent(Graphics g)
		{
			try
			{
				super.repaint();
				for(int i = 0; i < word.length(); i++)	
					g.drawImage(ImageIO.read(new File(".\\images\\letters\\" + word.charAt(i) + "_b.png")), (280 - 20 * word.length()) + (41 * i), 0, null);
			}
			catch (IOException e)
			{
				
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) 
	{
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) 
	{
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER && !typeWords.getText().equals(""))
		{
			bankWords.setText(bankWords.getText() + typeWords.getText() + "\n");
			typeWords.setText("");
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) 
	{
		
	}
}
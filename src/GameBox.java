import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.imageio.*;
import javax.swing.*;

public class GameBox extends JPanel implements KeyListener
{
	private final int LETTER_VALUES[] = {1, 3, 3, 2, 1,
										 4, 2, 4, 1, 8,
										 5, 1, 3, 1, 1,
										 3, 10, 1, 1, 1,
										 1, 4, 4, 8, 4, 10};
	
	private JLabel yourLabel, bankLabel, yourTime;
	private JTextField typeWords = new JTextField();
	private JTextArea bankWords = new JTextArea();
	
	private PrintStream output = null;
	private String givenWord = "";
	
	public GameBox(String username, PrintStream p)
	{
		String message = "";
	    output = p;
		
		Scanner input = new Scanner(message);
		int round = Integer.parseInt(input.next());
		givenWord = input.next();
		
		setLayout(new BorderLayout());
		
		JPanel topPanel = new JPanel();
		topPanel.add(new JLabel(new ImageIcon(".\\images\\main.jpg")));
		
		JPanel middlePanel = new JPanel();
		middlePanel.setLayout(new BorderLayout());
		
		JLabel roundLabel = new JLabel("Round " + round, SwingConstants.CENTER);
		roundLabel.setFont(new Font("Arial", Font.PLAIN, 30));
		middlePanel.add(roundLabel, BorderLayout.NORTH);
		yourTime = new JLabel("Timer: 90");
		middlePanel.add(yourTime, BorderLayout.CENTER);
		middlePanel.add(new WordGraph(givenWord), BorderLayout.SOUTH);
		add(middlePanel, BorderLayout.CENTER);
		
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
		JScrollPane scroll = new JScrollPane(bankWords);
		bottomPanel.add(scroll);
		
		add(bottomPanel, BorderLayout.SOUTH);
		input.close();
	}
	
	private int getScore()
	{
		Scanner input = new Scanner(bankWords.getText());
		input.useDelimiter("\n");
		
		int total = 0;
		while(input.hasNext())
		{
			String word = input.next();
			if(isAnagram(word))
				total += scoreWord(word);
		}
		input.close();
		return total;
	}
	
	private int scoreWord(String word)
	{
		int total = 0;
		for(int i = 0; i < word.length(); i++)
			total += LETTER_VALUES[word.charAt(i)];
		return total;
	}
	
	private boolean isAnagram(String word)
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
	
	public void update(String time)
	{
		yourTime.setText("Timer: " + time);
	}
	
	public void complete()
	{
		yourTime.setText("Timer: 0");
		JOptionPane.showMessageDialog(null, "Time's Up!");
		output.println("SCORE " + getScore());
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
					g.drawImage(ImageIO.read(new File(".\\images\\letters\\" + word.charAt(i) + "_b.png")), 41 * i, 0, null);
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
		if(!typeWords.getText().equals(""))
			bankWords.setText(bankWords.getText() + typeWords.getText() + "\n");
	}

	@Override
	public void keyTyped(KeyEvent arg0) 
	{
		
	}
}
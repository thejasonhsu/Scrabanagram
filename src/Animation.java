import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;

public class Animation extends JPanel implements ActionListener
{
	public class AnimateLetter
	{
		public char letter, size;
		public int x, y;
		
		public AnimateLetter(char let, char s, int x, int y)
		{
			letter = let;
			size = s;
			this.x = x;
			this.y = y;
		}
	}
	
	private ArrayList<AnimateLetter> tiles = new ArrayList<AnimateLetter>();
	private Timer timer;
	private Random rand = new Random();
	
	public Animation()
	{
		timer = new Timer(20, this);
		timer.start();
	}
	
	public void paintComponent(Graphics g)
	{
		super.repaint();
		try
		{
			for(int i = 0; i < tiles.size(); i++)
			{
				AnimateLetter aLetter = tiles.get(i);
				g.drawImage(ImageIO.read(new File(".\\images\\letters\\" + aLetter.letter + "_" + aLetter.size + ".png")), aLetter.x, aLetter.y, null);
			}
		}
		catch (IOException e)
		{
			
		}
	}
	
	public void removeOutOfBounds()
	{
		for(int i = 0; i < tiles.size(); i++)
			if(tiles.get(i).x < -50 || tiles.get(i).x > 730)
				tiles.remove(i);
	}
	
	public void dispose()
	{
		timer.stop();
		tiles.clear();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == timer)
		{
			char letter = (char)((Math.abs(rand.nextInt()) % 26) + 'a');
			char size = (Math.abs(rand.nextInt()) % 2 == 0 ? 's' : 'b');
			int x = Math.abs(rand.nextInt()) % 750;
			int y = Math.abs(rand.nextInt()) % 300;

			tiles.add(new AnimateLetter(letter, size, x, y));
			repaint();
		}
	}
}

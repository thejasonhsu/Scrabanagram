import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Game extends JFrame{
	
	String username = "elif", level = "diamond";
	JPanel main, play, setting, startPanel, window, gamePanel;
	CardLayout cl;
	int count = 30, points = 0, numPlayers;
	Timer t, tim2, time3;
	int roundCount = 1;
	
	public Game(){
		
		super("Scrabanagram");
		setSize(600, 500);

		window = new JPanel();
		main = new JPanel();		
		JLabel logo = new JLabel(new ImageIcon("./main.jpg")); 
		window.setLayout(new CardLayout());
		cl = (CardLayout)(window.getLayout());

		JButton start = new JButton(new ImageIcon("./start.jpg"));
		JButton settings = new JButton(new ImageIcon("./settings.jpg"));
		start.setBorder(BorderFactory.createEmptyBorder());
		start.setContentAreaFilled(false);
		settings.setBorder(BorderFactory.createEmptyBorder());
		settings.setContentAreaFilled(false);
		
		/*
		start.setRolloverEnabled(true);
		start.setRolloverIcon(new ImageIcon("./start.jpg"));
		settings.setRolloverEnabled(true);
		settings.setRolloverIcon(new ImageIcon("./start.jpg"));
		*/
		
		settings.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				
				  cl.show(window, "Settings");
					
				
			}
					
		});
		
		start.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				  cl.show(window, "Start");
				
			}
			
			
			
			
		});
		
		JLabel nameLabel = new JLabel(username);
		JLabel levelLabel = new JLabel(level);
		
		Font font = new Font(nameLabel.getFont().getName(), Font.PLAIN, 24);
		nameLabel.setFont(font);
		levelLabel.setFont(font);

		
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		
		main.add(Box.createGlue());
		main.add(logo);
		main.add(Box.createGlue());
		main.add(nameLabel);
		main.add(levelLabel);
		main.add(Box.createGlue());
		main.add(start);
		main.add(settings);
		main.add(Box.createGlue());

        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        settings.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        
        // *** START  ***
        JLabel startLabel = new JLabel(new ImageIcon("./start.jpg"));
        startPanel = new JPanel();
        JButton two = new JButton(new ImageIcon("./two.jpg"));
        JButton three = new JButton(new ImageIcon("./three.jpg"));
        two.setBorder(BorderFactory.createEmptyBorder());
		two.setContentAreaFilled(false);
		three.setBorder(BorderFactory.createEmptyBorder());
		three.setContentAreaFilled(false);
        startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));
        startPanel.add(Box.createGlue());
        startPanel.add(startLabel);
        startPanel.add(Box.createGlue());
        startPanel.add(Box.createGlue());
        startPanel.add(two);
        startPanel.add(Box.createGlue());
        startPanel.add(three);
        startPanel.add(Box.createGlue());
        startPanel.add(Box.createGlue());
        startLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        two.setAlignmentX(Component.CENTER_ALIGNMENT);
        three.setAlignmentX(Component.CENTER_ALIGNMENT);
        startPanel.setBackground(Color.WHITE);
        
        two.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
			
				numPlayers = 2;
				play();
			}	
        });
        
        three.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
			
				numPlayers = 3;
				play();
			}	
        });	
        
        // *** GAME INTERFACE ***
       
		play = new JPanel();
		play.setBackground(Color.WHITE);
		play.setLayout(new BoxLayout(play, BoxLayout.Y_AXIS));
		
		gamePanel = new JPanel();		
		JLabel smallLogo = new JLabel(new ImageIcon("./small.jpg"));
		JLabel round = new JLabel("ROUND " + roundCount);
		round.setFont(new Font(nameLabel.getFont().getName(), Font.BOLD, 36));
		JLabel word = new JLabel("Your word:");
		JTextField input = new JTextField(50);
		input.setMaximumSize( new Dimension(350, 250));
		
		JLabel bank = new JLabel("Your bank:");
		JPanel bankP = new JPanel();
		gamePanel.setLayout(null);

		bankP.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		bankP.setBackground(Color.WHITE);
		bankP.setMaximumSize(new Dimension(400, 100));
		bankP.setPreferredSize(new Dimension(400, 100));

		JLabel time = new JLabel("Timer: 30");
		t = new Timer(1000, new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
			
				count--;
				if(count > -1)
					time.setText("Timer: " + Integer.toString(count));
				else{
					
					t.stop();
				}
				
			}
		});
		time.setFont(new Font(nameLabel.getFont().getName(), Font.PLAIN, 20));

		gamePanel.add(smallLogo);
		gamePanel.add(time);
		smallLogo.setBounds(2, 5, 250, 70);
		time.setBounds(470, 5, 100, 70);
		gamePanel.setBackground(Color.WHITE);
		
	
		gamePanel.add(round);
		gamePanel.add(word);
		gamePanel.add(input);
		gamePanel.add(bank);
		gamePanel.add(bankP);
		
		round.setBounds(220, 230, 200, 50);
		word.setBounds(100, 415, 200, 50);
		input.setBounds(100, 465, 400, 25);
		bank.setBounds(100, 490, 400, 50);
		bankP.setBounds(100, 540, 400, 100);


		play.add(gamePanel);
		
		// *** SETTINGS ***
		
		setting = new JPanel();
		setting.setBackground(Color.WHITE);
		setting.setLayout(new BoxLayout(setting, BoxLayout.Y_AXIS));
		setting.add(Box.createGlue());
		JLabel settingsLabel = new JLabel(new ImageIcon("./settings.jpg"));
		JLabel name = new JLabel(new ImageIcon("./name.jpg"));
		JLabel icons = new JLabel(new ImageIcon("./icon.jpg"));
		JTextField nameInput = new JTextField(username, 35);
		setting.add(settingsLabel);
		setting.add(Box.createGlue());
		setting.add(Box.createGlue());
		setting.add(icons);
		setting.add(Box.createGlue());
		setting.add(name);
		setting.add(Box.createGlue());
		setting.add(nameInput);
		nameInput.setMaximumSize( nameInput.getPreferredSize() );
	
		
		JButton save = new JButton("Save");
		setting.add(Box.createGlue());
		
		setting.add(save);
		setting.add(Box.createGlue());
		setting.add(Box.createGlue());

		settingsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		icons.setAlignmentX(Component.CENTER_ALIGNMENT);
		name.setAlignmentX(Component.CENTER_ALIGNMENT);
		nameInput.setAlignmentX(Component.CENTER_ALIGNMENT);
		save.setAlignmentX(Component.CENTER_ALIGNMENT);

		save.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				if(!nameInput.getText().isEmpty()){
					username = nameInput.getText();
					// update username in database too
					
					nameLabel.setText(username);
					cl.show(window, "Main");
				}
			}
			
			
			
		});
		
		
		main.setBackground(Color.WHITE);
		window.add(main, "Main");
		window.add(play, "Play");
		window.add(setting, "Settings");
		window.add(startPanel, "Start");

		
		cl.show(window, "Main");


		add(window);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		
	}
	
	public void play(){
		
		setSize(600, 700);
		JLabel t2 = new JLabel("30");
		t2.setFont(new Font(t2.getFont().getName(), Font.PLAIN, 20));

		tim2 = new Timer(1000, new ActionListener(){
			int count2 = 30;

			public void actionPerformed(ActionEvent arg0) {
			
				count2--;
				if(count2 > -1)
					t2.setText(Integer.toString(count2));
				else{
					
					tim2.stop();
				}
				
			}
		});
		
		JLabel user1 = new JLabel("username");
		JLabel level1 = new JLabel("level");
		gamePanel.add(user1);
		gamePanel.add(level1);
		gamePanel.add(t2);

		
		if(numPlayers == 2){
			
			user1.setBounds(100, 80, 100, 50);
			level1.setBounds(100, 180, 100, 50);
			t2.setBounds(200, 80, 50, 50);
			tim2.start();

		}
		
		else{
			JLabel t3 = new JLabel("30");
			t3.setFont(new Font(t2.getFont().getName(), Font.PLAIN, 20));

			time3 = new Timer(1000, new ActionListener(){
				int count3 = 30;

				public void actionPerformed(ActionEvent arg0) {
				
					count3--;
					if(count3 > -1)
						t3.setText(Integer.toString(count3));
					else{
						
						time3.stop();
					}
					
				}
			});
			
			JLabel user2 = new JLabel("username");
			JLabel level2 = new JLabel("level");
			gamePanel.add(user2);
			gamePanel.add(level2);
			gamePanel.add(t3);
			
			user1.setBounds(100, 80, 100, 50);
			level1.setBounds(100, 180, 100, 50);
			user2.setBounds(300, 80, 100, 50);
			level2.setBounds(300, 180, 100, 50);
			t2.setBounds(200, 80, 50, 50);
			t3.setBounds(400, 80, 50, 50);
			tim2.start();
			time3.start();

			
		}
		
		
		cl.show(window, "Play");
		t.start();

		
	}
	
	
	public static void main(String [] args){
		
		Game g = new Game();
	}

}

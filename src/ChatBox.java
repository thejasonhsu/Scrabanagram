import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;

public class ChatBox extends JPanel implements ActionListener
{
	private JTextPane pane = new JTextPane();
	private StyledDocument document = pane.getStyledDocument();
	private JTextField message = new JTextField();
	private JButton submit = new JButton("Submit");
	private String username;
	private PrintStream output = null;
	
	public ChatBox(String username, PrintStream p)
	{
		setLayout(new BorderLayout());
		this.username = username;
		
		JPanel actionPanel = new JPanel();
		submit.addActionListener(this);
		actionPanel.setLayout(new GridLayout(2, 1));

		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BorderLayout());
		pane.setEditable(false);
		pane.setPreferredSize(new Dimension(170, 1000));
		textPanel.add(pane, BorderLayout.CENTER);
		JScrollPane scrollPane = new JScrollPane( textPanel );
		
		actionPanel.add(message);
		actionPanel.add(submit);
		add(scrollPane, BorderLayout.CENTER);
		add(actionPanel, BorderLayout.SOUTH);

		output = p;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == submit && !message.getText().equals(""))
		{
			String mess = message.getText();
			message.setText("");
			if(username.equals(""))
				output.println("MESSAGE " + "ANONYMOUS" + " " + mess);
			else
				output.println("MESSAGE " + username + " " + mess);
		}
	}
	
	public void setText(String user, Color col, String outputMessage)
	{
		Style style = pane.addStyle("Times New Roman", null);
		StyleConstants.setForeground(style, col);
		        
		try 
		{ 
			document.insertString(document.getLength(), user, style);
			document.insertString(document.getLength(), ": " + outputMessage, null);
		}
		catch (BadLocationException b)
		{
		        	
		}
		repaint();
		revalidate();
	}
	
	public Color convertColorString(String col)
	{
		if(col.equals("RED"))
			return Color.RED;
		else if(col.equals("BLUE"))
			return Color.BLUE;
		else if(col.equals("GREEN"))
			return Color.GREEN;
		else
			return Color.MAGENTA;
	}
}
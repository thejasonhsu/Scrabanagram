import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class Login extends JFrame {
	private JLabel user;
	private JLabel pass;
	private JPanel main;
	private JTextField username;
	private JPasswordField password;
	private String passText;

	public Login()
	{
		super("Login");
		setLocation(200,200);
		setSize(800,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		main = new JPanel();
		main.setLayout(null);
		
		user = new JLabel("Username:");
		pass = new JLabel("Password:");
		username = new JTextField("",10);
		password = new JPasswordField("",10);
		
		ImageIcon img = new ImageIcon("src/scrabble.jpg");
		JLabel image = new JLabel(img);
		
		int x = 13;
		int y = 0;
		Dimension dim = image.getPreferredSize();
		image.setBounds(x,y,dim.width,dim.height);
		main.add(image);
		
		x = 300;
		y = 300;
		dim = user.getPreferredSize();
		user.setBounds(x,y,dim.width,dim.height);
		main.add(user);
		
		x=375;
		y=295;
		dim = username.getPreferredSize();
		username.setBounds(x,y,dim.width,dim.height);
		main.add(username);
		
		x=300;
		y=350;
		dim = pass.getPreferredSize();
		pass.setBounds(x,y,dim.width,dim.height);
		main.add(pass);
		
		x=375;
		y=345;
		dim = password.getPreferredSize();
		password.setBounds(x,y,dim.width,dim.height);
		main.add(password);
		
		JButton login = new JButton("Login");
		login.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				passText = new String(password.getPassword());
				if(username.getText().length()>0 && passText.length()>0)
				{
					
				}
				else
				{
					JDialog pop = new JDialog();
					pop.setTitle("User/Pass");
					pop.setSize(200,100);
					pop.setLocationRelativeTo(Login.this);
					JLabel empty = new JLabel("Please fill both fields");
					pop.add(empty);
					pop.setVisible(true);
				}
			}
		});
		JButton create = new JButton("Create Account");
		create.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String result = "jdbc:mysql:";
				result = result + "/" + "/" + "localhost/finalproj";
				DriverManager.registerDriver(new com.mysql.jdbc.Driver());
				System.out.println(result);
				Connection a = DriverManager.getConnection(result,"root",""); 
				PreparedStatement b = a.prepareStatement("INSERT INTO User_Info (username, password, score, status ) VALUES (?,?,?,?)");
				b.setString(1, username.getText());
				b.setString(2,passText);
				b.setString(3, "0");
				b.setString(4,"Bronze");
				b.execute();
				Statement output =  a.createStatement();
				ResultSet obj = output.executeQuery("SELECT userid,username,password FROM User_Info;");
				while(obj.next())
				{
					System.out.println(obj.getString("userid"));
					System.out.println(obj.getString("username"));
					System.out.println(obj.getString("password"));
				}
			}
		});
		
		x=360;
		y=390;
		dim = login.getPreferredSize();
		login.setBounds(x,y,dim.width,dim.height);
		main.add(login);
		
		x=330;
		y=430;
		dim = create.getPreferredSize();
		create.setBounds(x,y,dim.width,dim.height);
		main.add(create);
		
		
		add(main);
		setVisible(true);
	}
	public static void main(String []args)
	{
		new Login();
	}
}

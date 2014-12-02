import java.sql.*;

public class ServerInfo 
{
    public static int portNumber = 3333;
    public static String host = "localhost";
	public static Connection conn = null;
    
	public static final String DB_ADDRESS = "jdbc:mysql://" + host + "/";
	public static final String DB_NAME = "Scrabanagram";
	public static final String DRIVER = "com.mysql.jdbc.Driver";
	public static final String USER = "root";
	public static final String PASSWORD = "Heaty6969";
}

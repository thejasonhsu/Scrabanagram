import java.sql.*;

public class ServerInfo 
{
    public static int portNumber = 2222;
    public static String host = "localhost";
	public static Connection conn = null;
    
	public static final String DATABASE_ADDRESS = "jdbc:mysql://" + host + "/";
	public static final String DATABASE_NAME = "Scrabanagram";
	public static final String DRIVER = "com.mysql.jdbc.Driver";
	public static final String USER = "root";
	public static final String PASSWORD = "Heaty6969";
}

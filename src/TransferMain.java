import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class TransferMain 
{
	public static class AnagramWord
	{
		public String word;
		public int count;
		
		public AnagramWord(String wd, int cnt)
		{
			word = wd;
			count = cnt;
		}
	}

	private static final String DATABASE_NAME = "Scrabanagram";
	private static ArrayList<AnagramWord> wordList = new ArrayList<AnagramWord>();
	
	public static void main(String[] args) throws FileNotFoundException
	{
		Scanner input = new Scanner(new File("output.txt"));
		while(input.hasNext())
			wordList.add(new AnagramWord(input.next(), input.nextInt()));
		input.close();

		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + DATABASE_NAME, "root", "Phil413!");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DROP TABLE DICTIONARY;");
			stmt.executeUpdate("CREATE TABLE DICTIONARY(word VARCHAR(40), PRIMARY KEY(word));");
			
			for(int i = 0; i < wordList.size(); i++)
			{
				if(i % 1000 == 0)
					System.out.println(i);
				PreparedStatement insertState = conn.prepareStatement("INSERT INTO DICTIONARY (word) VALUES (?);");
				insertState.setString(1, wordList.get(i).word);
				insertState.execute();
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		catch(ClassNotFoundException e)
		{
			
		}
	}
}

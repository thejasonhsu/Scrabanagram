import java.io.*;
import java.sql.*;
import java.util.*;

public class DictionaryMain 
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
	
	public static void main(String args[]) throws FileNotFoundException
	{
		Scanner input = new Scanner(new File("words.txt"));
		while(input.hasNext())
			wordList.add(new AnagramWord(input.next(), 0));
		input.close();

		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + DATABASE_NAME, "root", "Heaty6969");
			Statement stmt = conn.createStatement();
			System.out.println("START");
			stmt.executeUpdate("DROP TABLE DICTIONARY;");
			System.out.println("DROP");
			stmt.executeUpdate("CREATE TABLE DICTIONARY(word VARCHAR(40), count INTEGER, PRIMARY KEY(word));");

			for(int i = 0; i < wordList.size(); i++)
			{
				if(i % 1000 == 0)
					System.out.println(i);
				for(int j = 0; j < wordList.size(); j++)
					if(isAnagram(wordList.get(i).word, wordList.get(j).word))
						wordList.get(j).count++;
			}
			
			PrintWriter writer = new PrintWriter(new File("output.txt"));
			for(int i = 0; i < wordList.size(); i++)
			{
				if(i % 1000 == 0)
					System.out.println(i);
				PreparedStatement insertState = conn.prepareStatement("INSERT INTO DICTIONARY (word, count) VALUES (?, ?);");
				insertState.setString(1, wordList.get(i).word);
				insertState.setInt(2, wordList.get(i).count);
				insertState.execute();
				writer.println(wordList.get(i).word + " " + wordList.get(i).count);
			}
			writer.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		catch(ClassNotFoundException e)
		{
			
		}
	}
	
	public static boolean isAnagram(String wordToFind, String wordToCompare)
	{
		int index;
		for(int i = 0; i < wordToFind.length(); i++)
		{
			if((index = wordToCompare.indexOf(wordToFind.charAt(i))) == -1)
				return false;
			wordToCompare = wordToCompare.substring(0, index) + wordToCompare.substring(index + 1);
		}
		return true;
	}
}
###Scrabanagram

####Compiling and Running
Download all files.

mySQL:
  + Create database named 'dictionary'
  + Create table named 'dictionary' under the db
  + word VARCHAR(40), count INTEGER

    + Preset/Customized wordlist:
      + To use preset wordlist:
      + Replace username and password with your own in TransferMain.java:
      ```bash
      Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + DATABASE_NAME, "username", "password");
      ```
      + Run TransferMain.java to fill db up with words and valid solutions.

    + To use customized wordlist:
    + Replace words.txt with your own.
    + Replace username and password with your own in DictionaryMain.java:
      ```bash
      Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/" + DATABASE_NAME, "username", "password");
      ```
    + Run DictionaryMain.java to fill db up with words and valid solutions.
    
  + Create database named 'scrabanagram'
  + Replace username and password with your own in ServerInfo.java:
  ```bash
  public static final String USER = "username";
  
  public static final String PASSWORD = "password";
  ```

Run GameServer.java.

Run Login.java.

Launch as many times as the number of players needed. 

package fr.jikosoft.kernel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import fr.jikosoft.objects.Account;
import fr.jikosoft.objects.Character;
import fr.jikosoft.objects.Maps;


public class SQLManager {
	private static Connection echoC;
	
	public static final boolean setUpConnection() {
		try {
			echoC = DriverManager.getConnection("jdbc:mysql://" +Echo.DB_HOST+ "/"
												+ Echo.DB_NAME, Echo.DB_USER, Echo.DB_PASS);
			echoC.setAutoCommit(false);
			
			if(!echoC.isValid(1000)) throw new Exception("Timeout");
		
			return true;
		}
		catch(Exception e) {
			System.out.println(" ! SQL ERROR: " + e.getMessage() + ".\n");
			return false;
		}
	}
	
	
	public synchronized static void closeConnection() {
		System.out.println("close : 1");
		try {
			System.out.println("close : 2");
			commitTransactions();
			System.out.println("close : 3");
			
			System.out.println("close : 4");
			echoC.close();
			System.out.println("close : 5");
		}
		catch (Exception e) {
			System.out.println("Erreur à la fermeture de la connexion SQL: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	public synchronized static PreparedStatement newTransaction(String baseQuery, Connection dbC) throws SQLException {
		PreparedStatement ps = (PreparedStatement) dbC.prepareStatement(baseQuery);
		//needCommit = true;
		
		return ps;
	}
	
	
	public synchronized static void commitTransactions() {
		System.out.println("close - commit : 1");
		try {
			System.out.println("close - commit : 2");
			if(echoC.isClosed()) {
				System.out.println("close - commit : 3");
				closeConnection();
				System.out.println("close - commit : 4");
				setUpConnection();
				System.out.println("close - commit : 5");
			}
			System.out.println("close - commit : 6");
			echoC.commit();
			System.out.println("close - commit : 7");
		}
		catch(SQLException e) {
			System.out.println(" ! SQL ERROR: " + e.getMessage() + ".\n");
			e.printStackTrace();
			
			commitTransactions();
		}
	}
	
	
	public synchronized static ResultSet executeQuery(String query) throws SQLException {
		//if(!Echo.isInit)
			//return null;
		
		Statement state = echoC.createStatement();
		ResultSet result = state.executeQuery(query);
		state.setQueryTimeout(300); //Time in seconds (5min)
		
		return result;
	}
	
	
	private static void closeResultSet(ResultSet result) {
		try {
			result.getStatement().close();
			result.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	private static void closePreparedStatement(PreparedStatement ps) {
		try {
			ps.clearParameters();
			ps.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	public static void load_Accounts() {
		try {
			ResultSet result = SQLManager.executeQuery("SELECT * FROM account;");
			
			while(result.next()) {
				World.addAccount(new Account(
						result.getInt("accountID"),
						result.getString("username").toLowerCase(),
						result.getString("password"),
						result.getString("nickname"),
						(result.getInt("isBanned") == 1)
						));
				
				String details = "             - GUID : " + result.getInt("accountID") + " | " +
						 "Account : " + result.getString("username").toLowerCase() + " | " +
						 "Pass : " + result.getString("password") + " | " +
						 "Pseudo : " + result.getString("nickname");
				System.out.println(details);
		
				/*ps.setInt(1, result.getInt("guid"));
				System.out.println(ps);
				ps.executeUpdate();*/
				
			}
			
			//closePreparedStatement(ps);
			closeResultSet(result);
		}
		catch(SQLException e) {
			System.out.println(" ! SQL ERROR: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void load_Characters() {
		try {
			ResultSet result = SQLManager.executeQuery("SELECT * FROM characters;");
			
			while(result.next()) {
				Character charac = new Character(
						result.getInt("guid"),
						result.getString("name"),
						result.getInt("class"),
						result.getInt("sex"),
						result.getInt("level"),
						result.getInt("color1"),
						result.getInt("color2"),
						result.getInt("color3"),
						result.getInt("account"),
						result.getShort("currentMap"),
						result.getShort("currentCell")
						
						);
				
				String details = "             - GUID : " + result.getInt("guid") + " | " +
						 "Name : " + result.getString("name").toLowerCase() + " | " +
						 "Sex : " + result.getString("sex") + " | " +
						 "Class : " + result.getString("class") + " | " +
						 "Color1 : " + result.getString("color1") + " | " +
						 "Color2 : " + result.getString("color2") + " | " +
						 "Color3 : " + result.getString("color3") + " | ";
				System.out.println(details);
				
				World.addCharacter(charac);
				
				if(World.getAccount(result.getInt("account")) != null)
					World.getAccount(result.getInt("account")).addCharacter(charac);
		
				//ps.setInt(1, result.getInt("guid"));
				//System.out.println(ps);
				//ps.executeUpdate();
				
			}
			
			//closePreparedStatement(ps);
			closeResultSet(result);
		}
		catch (SQLException e) {
			System.out.println(" ! SQL ERROR: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void load_IP_Bans()  {
		try {
			ResultSet result = executeQuery("SELECT ip FROM ipbans;");
			
			while (result .next()) {
				World.addIPBan(result.getString("ip"));
				System.out.println("             - IP : " + result.getString("ip"));
				
		    }
			closeResultSet(result);
		}
		catch(SQLException e) {
			System.out.println("SQL ERROR: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	public static void load_Maps() {
		try {
			ResultSet result = executeQuery("SELECT * FROM maps;");
			
			while(result.next()) {
				Maps map = new Maps(
						result.getShort("id"),
						result.getString("date"),
						result.getString("mapData"),
						result.getString("key"),
						result.getString("decryptedData")
						);
				
				World.addMap(map);
			}
			
			closeResultSet(result);
		}	
		catch(SQLException e) {
			System.out.println("SQL ERROR: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void save_Account(Account account) {
		PreparedStatement ps = null;
		try {
			ps = newTransaction("UPDATE accounts SET `nickname` = ?, `banned` = ?, `subTime` = ? WHERE `guid` = ?;", echoC);
			ps.setString(1, account.getNickname());
			ps.setInt(2, account.isBanned() ? 1 : 0);
			ps.setInt(4, account.getAccountID());
			ps.executeUpdate();
		}
		catch(SQLException e) {
			System.out.println("SQLERROR : DELETE : " + e.getMessage());
		}
		finally {
			closePreparedStatement(ps);
		}
	}
	
	
	public static void delete_Character(Character chara) {
		PreparedStatement ps = null;
		try {
			ps = newTransaction("DELETE FROM characters WHERE guid = ?", echoC);
			ps.setInt(1, chara.get_GUID());
			ps.executeUpdate();
		}
		catch (SQLException e) {
			System.out.println("SQLERROR : DELETE : " + e.getMessage());
		}
		finally {
			closePreparedStatement(ps);
		}
	}
	
	public static void add_Character(Character chara) {
		PreparedStatement ps = null;
		try {
			String query = "INSERT INTO characters(`guid`, `name`, `sex`, `class`, `level`,  `color1`, `color2`, `color3`, `account`, " + 
							"`currentMap`, `currentCell`) " + 
						   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
			ps = newTransaction(query, echoC);
			ps.setInt(1, chara.get_GUID());
			ps.setString(2, chara.get_name());
			ps.setInt(3, chara.get_sex());
			ps.setInt(4, chara.get_class());
			ps.setInt(5, chara.get_level());
			ps.setInt(6, chara.get_color1());
			ps.setInt(7, chara.get_color2());
			ps.setInt(8, chara.get_color3());
			ps.setInt(9, chara.get_account().getAccountID());
			ps.setInt(10, chara.get_currentMapID());
			ps.setInt(11, chara.get_currentCellID());
			ps.executeUpdate();
		}
		catch (SQLException e) {
			System.out.println("SQLERROR : ADD" + e.getMessage());
		}
		finally {
			closePreparedStatement(ps);
		}
	}
	
	
	
	public static int getNextCharacterGUID() {
		try {
			ResultSet result = executeQuery("SELECT guid FROM characters ORDER BY guid DESC LIMIT 1;");
			if(!result.first())
				return 1;
			
			int GUID = result.getInt("guid") + 1;
			closeResultSet(result);
			
			return GUID;
		}
		catch(SQLException e) {
			System.out.println("SQL ERROR : " + e.getMessage());
			e.printStackTrace();
		}
		
		return 0;
	}
	
	/*public static void logOut(int accountID, int logged) {
		PreparedStatement ps;
		String query = "UPDATE `accounts` SET logged=? WHERE `guid`=?;";
		
		try {
			ps = newTransaction(query, echoC);
			ps.setInt(1, logged);
			ps.setInt(2, accountID);
			ps.execute();
			
			closePreparedStatement(ps);
		}
		catch (SQLException e) {
			System.out.println("Game: SQL ERROR: "+e.getMessage());
			System.out.println("Game: Query: "+query);
		}
	}*/
	
}

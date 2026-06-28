package fr.jikosoft.database;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Locale;

import fr.jikosoft.kernel.DatabaseManager;
import fr.jikosoft.objects.Account;
import fr.jikosoft.kernel.World;

public class AccountsManager {

	
	public static void getAll() {
		DatabaseManager.query("SELECT * FROM account", AccountsManager::mapAccount).forEach(World::addAccount);
	}

	private static Account mapAccount(ResultSet resultSet) throws SQLException {
		return new Account(
			resultSet.getInt("accountID"), 
			resultSet.getString("username").toLowerCase(Locale.ROOT), 
			resultSet.getString("password"), 
			resultSet.getString("nickname"), 
			resultSet.getBoolean("isBanned")
		);
	}

	public static void update(Account account) {
		/*
		PreparedStatement ps = null;
		try {
			ps = DatabaseManager.newTransaction("UPDATE accounts SET `nickname` = ?, `banned` = ?, `subTime` = ? WHERE `guid` = ?;");
			ps.setString(1, account.getNickname());
			ps.setInt(2, account.isBanned() ? 1 : 0);
			ps.setInt(4, account.getAccountID());
			ps.executeUpdate();
		}
		catch(SQLException e) {
			System.out.println("SQLERROR : DELETE : " + e.getMessage());
		}
		finally {
			DatabaseManager.closePreparedStatement(ps);
		} */
	}

	public static void load_IP_Bans()  {
		/*
		try {
			ResultSet result = DatabaseManager.executeQuery("SELECT ip FROM ipbans;");
			
			while (result .next()) {
				World.addIPBan(result.getString("ip"));
				System.out.println("             - IP : " + result.getString("ip"));
				
			}
			DatabaseManager.closeResultSet(result);
		}
		catch(SQLException e) {
			System.out.println("SQL ERROR: "+e.getMessage());
			e.printStackTrace();
		} */
	}
}

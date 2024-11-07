package fr.jikosoft.database;

import java.util.List;
import java.util.Map;

import fr.jikosoft.kernel.DatabaseManager;
import fr.jikosoft.objects.Account;
import fr.jikosoft.kernel.World;

public class AccountsManager {
	public static void getAll() {
		try {
			List<Map<String, Object>> results = DatabaseManager.selectQuery("SELECT * FROM account");
			
			for (Map<String, Object> result : results) {
				World.addAccount(new Account(
					(int) result.get("accountID"),
					((String) result.get("username")).toLowerCase(),
					(String) result.get("password"),
					(String) result.get("nickname"),
					((int) result.get("isBanned") == 1)
				));
			}
		}
		catch(Exception e) {
			System.out.println(" ! SQL ERROR: " + e.getMessage());
			e.printStackTrace();
		}
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

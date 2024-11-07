package fr.jikosoft.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import fr.jikosoft.kernel.DatabaseManager;
import fr.jikosoft.objects.Character;
import fr.jikosoft.kernel.World;

public class CharactersManager {
    public static void load_Characters() {
        /*
		try {
			ResultSet result = DatabaseManager.executeQuery("SELECT * FROM characters;");
			
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
			}

			DatabaseManager.closeResultSet(result);
		}
		catch (SQLException e) {
			System.out.println(" ! SQL ERROR: " + e.getMessage());
			e.printStackTrace();
		}*/
	}


	public static void delete_Character(Character chara) {
        /*
		PreparedStatement ps = null;
		try {
			ps = DatabaseManager.newTransaction("DELETE FROM characters WHERE guid = ?");
			ps.setInt(1, chara.get_GUID());
			ps.executeUpdate();
		}
		catch (SQLException e) {
			System.out.println("SQLERROR : DELETE : " + e.getMessage());
		}
		finally {
			DatabaseManager.closePreparedStatement(ps);
		}*/
	}

	public static void add_Character(Character chara) {
        /*
		PreparedStatement ps = null;
		try {
			String query = "INSERT INTO characters(`guid`, `name`, `sex`, `class`, `level`,  `color1`, `color2`, `color3`, `account`, " + 
							"`currentMap`, `currentCell`) " + 
						   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
			ps = DatabaseManager.newTransaction(query);
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
			DatabaseManager.closePreparedStatement(ps);
		}*/
	}

	public static int getNextCharacterGUID() {
        return 0;
        /*
		try {
			ResultSet result = DatabaseManager.executeQuery("SELECT guid FROM characters ORDER BY guid DESC LIMIT 1;");
			if(!result.first())
				return 1;
			
			int GUID = result.getInt("guid") + 1;
			DatabaseManager.closeResultSet(result);
			
			return GUID;
		}
		catch(SQLException e) {
			System.out.println("SQL ERROR : " + e.getMessage());
			e.printStackTrace();
		}
		
		return 0;*/
	}
}

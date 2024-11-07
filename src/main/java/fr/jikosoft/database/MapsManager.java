package fr.jikosoft.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import fr.jikosoft.kernel.DatabaseManager;
import fr.jikosoft.kernel.World;
import fr.jikosoft.objects.Maps;

public class MapsManager {
	public static void load_Maps() {
        /*
		try {
			ResultSet result = DatabaseManager.executeQuery("SELECT * FROM maps;");
			
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
			
			DatabaseManager.closeResultSet(result);
		}	
		catch(SQLException e) {
			System.out.println("SQL ERROR: "+e.getMessage());
			e.printStackTrace();
		}*/
	} 
}

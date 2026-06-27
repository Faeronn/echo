package fr.jikosoft.database;

import java.util.List;
import java.util.Map;

import fr.jikosoft.kernel.DatabaseManager;
import fr.jikosoft.kernel.World;
import fr.jikosoft.objects.Maps;

public class MapsManager {
	public static void getAll() {
		try {
			List<Map<String, Object>> results = DatabaseManager.selectQuery("SELECT * FROM maps");
			
			for (Map<String, Object> result : results) {
				World.addMap(new Maps(
					(int) result.get("mapID"),
					(String) result.get("date"),
					(String) result.get("mapData"),
					(String) result.get("key"),
					(String) result.get("decryptedData")
				));
			}
		}
		catch(Exception e) {
			System.out.println(" ! SQL ERROR: " + e.getMessage());
			e.printStackTrace();
		}
	}
}

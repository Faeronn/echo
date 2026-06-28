package fr.jikosoft.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import fr.jikosoft.kernel.DatabaseManager;
import fr.jikosoft.kernel.World;
import fr.jikosoft.objects.Maps;

public class MapsManager {
	private static final String SELECT_MAPS = """
		SELECT `mapID`, `date`, `mapData`, `key`, `decryptedData`
		FROM `maps`
		""";

	public static void getAll() {
		DatabaseManager.query(SELECT_MAPS, MapsManager::mapMap).forEach(World::addMap);
	}

	private static Maps mapMap(ResultSet resultSet) throws SQLException {
		return new Maps(
			resultSet.getInt("mapID"),
			resultSet.getString("date"),
			resultSet.getString("mapData"),
			resultSet.getString("key"),
			resultSet.getString("decryptedData")
		);
	}
}

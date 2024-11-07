package fr.jikosoft.kernel;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariConfig;

public class DatabaseManager {
	private static HikariDataSource dataSource;

	public static boolean connect() {
		try {
			HikariConfig config = new HikariConfig();
			config.setJdbcUrl(String.format("jdbc:mysql://%s/%s", Echo.DB_HOST, Echo.DB_NAME));
			config.setUsername(Echo.DB_USER);
			config.setPassword(Echo.DB_PASS);
			config.setMaximumPoolSize(10);

			dataSource = new HikariDataSource(config);

			try (Connection connection = dataSource.getConnection()) {
				if (!connection.isValid(1000)) throw new Exception("Timeout");
			}

			return true;
		} catch (Exception e) {
			System.out.println(" ! SQL ERROR: " + e.getMessage() + ".\n");
			return false;
		}
	}

	public static void disconnect() {
		if (dataSource != null && !dataSource.isClosed()) dataSource.close();
	}

	public static List<Map<String, Object>> selectQuery(String query, Object... parameters) {
		List<Map<String, Object>> results = new ArrayList<>();
		try (Connection connection = dataSource.getConnection();
			 PreparedStatement statement = connection.prepareStatement(query)) {
			
			for (int i = 0; i < parameters.length; i++) statement.setObject(i + 1, parameters[i]);

			try (ResultSet resultSet = statement.executeQuery()) {
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columnCount = metaData.getColumnCount();

				while (resultSet.next()) {
					Map<String, Object> row = new HashMap<>();
					for (int i = 1; i <= columnCount; i++) {
						row.put(metaData.getColumnName(i), resultSet.getObject(i));
					}
					results.add(row);
				}
			}
		} catch (SQLException e) {
			System.out.println(" ! SQL QUERY ERROR: " + e.getMessage() + ".\n");
		}
		return results;
	}
}

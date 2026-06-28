package fr.jikosoft.kernel;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.List;

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

	@FunctionalInterface
	public interface RowMapper<T> {
		T map(ResultSet rs) throws SQLException;
	}

	public static <T> List<T> query(String query, RowMapper<T> mapper, Object... parameters) {
		List<T> results = new ArrayList<>();

		try (Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(query)) {

			bindParameters(statement, parameters);

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) results.add(mapper.map(resultSet));
			}
		} catch (SQLException sqlError) {
			throw new RuntimeException(" ! SQL QUERY ERROR: " + query, sqlError);
		}

		return results;
	}

	public static int update(String query, Object... parameters) {
		try (Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(query)) {

			bindParameters(statement, parameters);
			return statement.executeUpdate();
		} catch (SQLException sqlError) {
			throw new RuntimeException(" ! SQL UPDATE ERROR: " + query, sqlError);
		}
	}

	private static void bindParameters(PreparedStatement statement, Object... parameters) throws SQLException {
		for (int i = 0; i < parameters.length; i++) statement.setObject(i + 1, parameters[i]);
	}
}

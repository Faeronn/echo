package fr.jikosoft.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import fr.jikosoft.kernel.DatabaseManager;
import fr.jikosoft.kernel.World;
import fr.jikosoft.objects.Account;
import fr.jikosoft.objects.Character;

public class CharactersManager {
	public static void load_Characters() {
		DatabaseManager.query("SELECT * from characters ORDER BY guid;", CharactersManager::mapCharacter).forEach(CharactersManager::registerCharacter);
	}

	public static void delete_Character(Character character) {
		if (character == null) return;

		DatabaseManager.update("DELETE FROM `characters` WHERE `guid` = ?", character.get_GUID());
	}

	public static void add_Character(Character character) {
		if (character == null) return;
		if (character.get_account() == null) throw new IllegalArgumentException("Cannot save a character without an account.");

		DatabaseManager.update(
			"INSERT INTO `characters` (`guid`, `name`, `class`, `sex`, `level`, `color1`, `color2`, `color3`, `account`, `currentMap`, `currentCell`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
			character.get_GUID(),
			character.get_name(),
			character.get_class(),
			character.get_sex(),
			character.get_level(),
			character.get_color1(),
			character.get_color2(),
			character.get_color3(),
			character.get_account().getAccountID(),
			character.get_currentMapID(),
			character.get_currentCellID()
		);
	}

	public static int getNextCharacterGUID() {
		List<Integer> nextGuids = DatabaseManager.query(
			"SELECT COALESCE(MAX(`guid`), 0) + 1 AS `nextGuid` FROM `characters`",
			resultSet -> resultSet.getInt("nextGuid")
		);
		return nextGuids.get(0);
	}

	private static Character mapCharacter(ResultSet resultSet) throws SQLException {
		int guid = resultSet.getInt("guid");
		int accountID = resultSet.getInt("account");
		int currentMapID = resultSet.getInt("currentMap");

		if (World.getAccount(accountID) == null) {
			throw new SQLException("Character " + guid + " references missing account " + accountID + ".");
		}
		if (World.getMap(currentMapID) == null) {
			throw new SQLException("Character " + guid + " references missing map " + currentMapID + ".");
		}

		Character character = new Character(
			guid,
			resultSet.getString("name"),
			resultSet.getInt("class"),
			resultSet.getInt("sex"),
			resultSet.getInt("level"),
			resultSet.getInt("color1"),
			resultSet.getInt("color2"),
			resultSet.getInt("color3"),
			accountID,
			currentMapID,
			resultSet.getShort("currentCell")
		);

		if (character.get_currentCell() == null) {
			throw new SQLException("Character " + guid + " references missing cell " + resultSet.getShort("currentCell") + ".");
		}

		return character;
	}

	private static void registerCharacter(Character character) {
		World.addCharacter(character);

		Account account = character.get_account();
		if (account != null) account.addCharacter(character);
	}
}

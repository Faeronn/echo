package fr.jikosoft.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import fr.jikosoft.kernel.CryptManager;
import fr.jikosoft.kernel.SocketManager;



public class Maps {
	private short _ID;
	private String _date;
	private String _mapData;
	private String _key;
	private Map<Short, Cell> _cells = new TreeMap<Short, Cell>();
	
	
	public Maps(short id, String date, String mapData, String key, String decryptedData) {
		this._ID = id;
		this._date = date;
		this._mapData = mapData;
		this._key = key;
		
		if(!decryptedData.isEmpty()) {
			_cells = CryptManager.decompileMapData(this, decryptedData);
		}
	}
	
	public short get_ID() {
		return _ID;
	}
	
	public String get_date() {
		return _date;
	}

	public String get_mapData() {
		return _mapData;
	}
	
	public String get_key() {
		return _key;
	}
	
	public Cell get_cell(short guid) {
		return _cells.get(guid);
	}
	
	public void addPlayer(Character character) {
		SocketManager.GAME_SEND_GM_PACKET(this, character);
		character.get_currentCell().addCharacter(character);
	}
	
	public ArrayList<Character> getCharacters() {
		ArrayList<Character> characterList = new ArrayList<Character>();
		
		for(Cell cell : _cells.values()) {
			for(Character character : cell.getCharacters().values()) {
				characterList.add(character);
			}
		}
		return characterList;
	}
	
	public String getCharacterGMPackets() {
		StringBuilder packet = new StringBuilder();
		packet.append("GM");
		
		for(Cell cell : _cells.values()) {                                            //FIXME: no cells in _cells
			for(Character character : cell.getCharacters().values()) {
				packet.append("|+").append(character.getCharacterGMData());
			}
		}
		packet.append('\u0000');
		return packet.toString();
	}
	
	public static class Cell {
		private short _ID;
		private Map<Integer, Character> _characters = new TreeMap<Integer, Character>(Collections.reverseOrder());
		private boolean _isWalkable = true;
		private short _mapID;
		
		public Cell (Maps map, short ID, boolean isWalkable, boolean LoS, int objID) {
			_mapID = map.get_ID();
			_ID = ID;
			_isWalkable = isWalkable;
		}
		
		public short get_ID() {
			return _ID;
		}
		
		public void addCharacter(Character character) {
			_characters.put(character.get_GUID(), character);
		}
		
		public Map<Integer, Character> getCharacters() {
			if(_characters == null) {
				return new TreeMap<Integer, Character>();
			}
			return _characters;
		}
		
	}
}

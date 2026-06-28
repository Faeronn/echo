package fr.jikosoft.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

import fr.jikosoft.database.CharactersManager;
import fr.jikosoft.kernel.Constants;
import fr.jikosoft.kernel.CryptManager;
import fr.jikosoft.kernel.Echo;
import fr.jikosoft.kernel.DatabaseManager;
import fr.jikosoft.kernel.SocketManager;
import fr.jikosoft.kernel.World;
import fr.jikosoft.objects.Account;
import fr.jikosoft.objects.Character;
import fr.jikosoft.objects.Maps;
import fr.jikosoft.objects.Maps.Cell;

public class GameThread implements Runnable {
	private Socket _socket;
	private BufferedReader _reader;
	private PrintWriter _writer;
	private Thread _thread;
	private Account _account;
	private Character _character;
	private Map<Integer, GameAction> currentGameActions = new TreeMap<>();

	public GameThread(Socket socket) {
		try {
			_socket = socket;
			_reader = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
			_writer = new PrintWriter(_socket.getOutputStream());

			_thread = new Thread(this);
			_thread.setDaemon(true);
			_thread.start();
		} catch (IOException e) {
			try {
				System.out.println("IOException" + e.getMessage());
				if (!_socket.isClosed()) {
					_socket.close();
				}
			} catch (IOException e1) {
				System.out.println("IOException" + e.getMessage());
			}
		}
	}

	public static class GameAction {
		public int gameActionID;
		public int actionID;
		public String packet;
		public String arguments;
		
		public GameAction(int id, int actionID, String packet) {
			this.gameActionID = id;
			this.actionID = actionID;
			this.packet = packet;
		}
	}

	public void run() {
		try {
			String packet = "";
			char charCur[] = new char[1];

			SocketManager.GAME_SEND_HG_PACKET(_writer);
			while (_reader.read(charCur, 0, 1) != -1 && Echo.isRunning) {
				if (charCur[0] != '\u0000' && charCur[0] != '\n' && charCur[0] != '\r') {
					packet += charCur[0];
				} else if (!packet.isEmpty()) {
					packet = CryptManager.toUnicode(packet);
					System.out.println("Game: Recv << " + packet);
					parsePacket(packet);
					packet = "";
				}
			}
		} catch (IOException e) {
			try {
				System.out.println(e.getMessage());
				_reader.close();
				_writer.close();

				if (_account != null) {
					// _account.setGameThread(null);
					_account.setLoginThread(null);
				}
				if (!_socket.isClosed())
					_socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
			kick();
		}
	}

	private void parsePacket(String packet) {
		if (packet.length() > 3 && packet.substring(0, 4).equalsIgnoreCase("ping")) {
			SocketManager.GAME_SEND_PONG(_writer);
			return;
		}

		if (packet.length() > 4 && packet.substring(0, 5).equalsIgnoreCase("qping")) {
			SocketManager.GAME_SEND_QPONG(_writer);
			return;
		}

		switch (packet.charAt(0)) {
			case 'A':
				parseAccountPackets(packet);
				break;

			case 'B':
				parseBasicsPackets(packet);
				break;
			case 'M':
				parseMapsPackets(packet);
				/*
				 * case 'c':
				 * parseChanelPacket(packet);
				 * break;
				 * case 'D':
				 * parseDialogPacket(packet);
				 * break;
				 * case 'E':
				 * parseExchangePacket(packet);
				 * break;
				 * case 'e':
				 * parse_environementPacket(packet);
				 * break;
				 * case 'F':
				 * parse_friendPacket(packet);
				 * break;
				 * case 'f':
				 * parseFightPacket(packet);
				 * break;
				 */
			case 'G':
				parseGamePackets(packet);
				break;
			/*
			 * case 'g':
			 * parseGuildPacket(packet);
			 * break;
			 * case 'h':
			 * parseHousePacket(packet);
			 * break;
			 * case 'i':
			 * parse_enemyPacket(packet);
			 * break;
			 * case 'K':
			 * parseHouseKodePacket(packet);
			 * break;
			 * case 'O':
			 * parseObjectPacket(packet);
			 * break;
			 * case 'P':
			 * parseGroupPacket(packet);
			 * break;
			 * case 'R':
			 * parseMountPacket(packet);
			 * break;
			 * case 'S':
			 * parseSpellPacket(packet);
			 * break;
			 * case 'W':
			 * parseWaypointPacket(packet);
			 * break;
			 */ // FIXME
		}
	}

	public void parseMapsPackets(String packet) {
		switch (packet.charAt(1)) {
			case 'd':
				moveCharacter(packet);
		}
	}

	public void parseAccountPackets(String packet) {
		switch (packet.charAt(1)) {
			case 'A':
				addCharacter(packet);
				break;

			case 'B':
				break;

			case 'D':
				deleteCharacter(packet);
				break;

			case 'f':
				break;

			case 'i':
				break;

			case 'L':
				sendCharacterList();
				break;

			case 'S':
				selectCharacter(packet);
				break;

			case 'T':
				sendTicket(packet);
				break;

			case 'V':
				sendVersion();
				break;

			case 'P':
				sendRandomName();
				break;
		}
	}

	private void moveCharacter(String packet) {
		String[] split = packet.substring(2).split("\\|");
	}

	private void addCharacter(String packet) {
		String[] split = packet.substring(2).split("\\|");

		if (split[1] == "12") { // && _account.get_subscriptionTime() == 0
			SocketManager.GAME_SEND_AAEs_PACKET(_writer);
			return;
		}

		if (_account.getCharacters().size() >= 5) {
			SocketManager.GAME_SEND_AAEf_PACKET(_writer);
			return;
		}

		Character charac = new Character(CharactersManager.getNextCharacterGUID(), split[0], Integer.parseInt(split[1]),
				Integer.parseInt(split[2]), 1,
				Integer.parseInt(split[3]), Integer.parseInt(split[4]), Integer.parseInt(split[5]),
				_account.getAccountID(),
				Constants.getStartMapID(Integer.parseInt(split[1])),
				Constants.getStartCellID(Integer.parseInt(split[1])));
		_account.addCharacter(charac);
		World.addCharacter(charac);
		CharactersManager.add_Character(charac);

		SocketManager.GAME_SEND_AAK_PACKET(_writer);
		sendCharacterList();
	}

	private void deleteCharacter(String packet) {
		String[] split = packet.substring(2).split("\\|");
		int id = Integer.parseInt(split[0]);
		String answer = (split.length > 1) ? split[1] : "";

		if (!_account.getCharacters().containsKey(id)) {
			SocketManager.GAME_SEND_ADE_PACKET(_writer);
		}
		Character charac = _account.getCharacters().get(id);

		if (charac.get_level() < 20 || (charac.get_level() >= 20)) { // answer.equals(_account.get_answer().replace(" ",
																		// "%20"))
			_account.deleteCharacter(id);
			World.deleteCharacter(charac);
			CharactersManager.delete_Character(charac);
			sendCharacterList();
		} else
			SocketManager.GAME_SEND_ADE_PACKET(_writer);
	}

	private void sendCharacterList() { // ALK + subscriptionTime + | + Character Number + | + characterInfo
		// SocketManager.GAME_SEND_ALK_PACKET(_writer, _account.get_subscriptionTime(),
		// _account.getCharacters());
		SocketManager.GAME_SEND_ALK_PACKET(_writer, 1000, _account.getCharacters());
	}

	private void selectCharacter(String packet) {
		int id = Integer.parseInt(packet.substring(2));
		this._character = _account.getCharacters().get(id);
		if (_character != null) {
			SocketManager.GAME_SEND_Rx_PACKET(_character, "0"); // FIXME Rx_PACKET(_writer, charac.get_mountXpGiven());
			SocketManager.GAME_SEND_ASK_PACKET(_writer, _character);
			SocketManager.GAME_SEND_ZS_PACKET(_writer, "0"); // FIXME ZS_PACKET(_writer, charac.get_alignment());
			SocketManager.GAME_SEND_cC_PACKET(_writer, "*#%!$pi^"); // FIXME cC_PACKET(_writer, charac.get_channels());
			SocketManager.GAME_SEND_al_PACKET(_writer);
			SocketManager.GAME_SEND_SLo_PACKET(_writer, "+");// FIXME SLo_PACKET(_writer, charac.get_spellOption());
			SocketManager.GAME_SEND_SL_PACKET(_writer);
			SocketManager.GAME_SEND_AR_PACKET(_writer, "6bk");
			SocketManager.GAME_SEND_Ow_PACKET(_character);
			SocketManager.GAME_SEND_FO_PACKET(_writer, "+");// FIXME FO_PACKET(_writer, charac.get_friendsOption());
			SocketManager.GAME_SEND_Im_PACKET(_character, "189"); // 189 = "Bienvenue sur DOFUS" Message
			SocketManager.GAME_SEND_Im_PACKET(_character, "0152;" + "DATE");
			SocketManager.GAME_SEND_Im_PACKET(_character, "0153;" + "IP");

			// Tutorial
			if (this._character.get_account().getCurrentIPAddress() == "") // FIXME
				SocketManager.GAME_SEND_TB_PACKET(_character);

		} else {
			SocketManager.GAME_SEND_ASE_PACKET(_writer);
			kick();
		}
	}

	private void sendTicket(String packet) {
		try {
			int id = Integer.parseInt(packet.substring(2));
			_account = Echo.gameServer.getWaitingAccount(id);

			if (_account == null) {
				SocketManager.GAME_SEND_ATE_PACKET(_writer);
				kick();
			} else {
				String ip = _socket.getInetAddress().getHostAddress();
				_account.setGameThread(this);
				_account.setCurrentIPAddress(ip);
				Echo.gameServer.deleteWaitingAccount(_account);
				SocketManager.GAME_SEND_ATK_PACKET(_writer);
			}
		} catch (Exception e) {
			System.out.println("TICKET ERROR : " + e.getMessage());
			kick();
		}
	}

	private void sendVersion() {
		SocketManager.GAME_SEND_AV_PACKET(_writer);
	}

	private void sendRandomName() {
		SocketManager.GAME_SEND_AP_PACKET(_writer);
	}

	private void parseGamePackets(String packet) {
		switch (packet.charAt(1)) {
			case 'C':
				gameCreate();
				break;
			case 'I':
				gameInformations();
				break;
			case 'A':
				gameActions(packet);
				break;
			case 'K':
				Game_on_GK_packet(packet);
				break;
		}
	}

	private void gameActions(String packet) {
		int actionID = Integer.parseInt(packet.substring(2, 5));
		int nextGameActionID = currentGameActions.size() > 0 ? (Integer)(currentGameActions.keySet().toArray()[currentGameActions.size() - 1]) + 1 : 0;
		GameAction gameAction = new GameAction(nextGameActionID, actionID, packet);

		switch (actionID) {
			case 1: //Déplacement
				String path = packet.substring(5);
				AtomicReference<String> pathRef = new AtomicReference<String>(path);
				path = pathRef.get();
				gameAction.arguments = path;
				SocketManager.GAME_SEND_GA_PACKET(_character, 1, path);
				currentGameActions.put(gameAction.actionID, gameAction);
		}
	}

	private void Game_on_GK_packet(String packet) {	
		int gameActionID = -1;
		String[] packetData = packet.substring(3).split("\\|");
		try
		{
			gameActionID = Integer.parseInt(packetData[0]);
		}catch(Exception e){return;};
		if(gameActionID == -1) return;

		GameAction gameAction = currentGameActions.get(gameActionID);
		boolean isOk = packet.charAt(2) == 'K';
		
		switch(gameActionID) {
			case 1://Deplacement
				Maps currentMap = _character.get_currentMap();
				_character.get_currentCell().removeCharacter(_character);
				if(isOk) {
						SocketManager.GAME_SEND_BN_PACKET(_writer);
						String path = gameAction.arguments;
						//On prend la case ciblée
						Cell nextCell = currentMap.get_cell(CryptManager.convertCodeToCellID(path.substring(path.length()-2)));
						//Cell targetCell = currentMap.get_cell(CryptManager.convertCodeToCellID(gameAction.packet.substring(gameAction.packet.length()-2)));
						
						//On définie la case et on ajoute le personnage sur la case
						_character.setCurrentCell(nextCell);
						//_character.set_orientation(CryptManager.getIntByHashedValue(path.charAt(path.length()-3)));
						_character.get_currentCell().addCharacter(_character);
						//if(!_character._isGhosts) _character.set_away(false);
						//currentMap.onPlayerArriveOnCell(_character,_character.get_currentCell().get_ID());
				}
				else
				{
					//Si le joueur s'arrete sur une case
					int newCellID = -1;
					try
					{
						newCellID = Integer.parseInt(packetData[1]);
					}catch(Exception e){return;};
					if(newCellID == -1)return;

					String path = gameAction.arguments;
					_character.setCurrentCell(currentMap.get_cell(newCellID));
					//_character.set_orientation(CryptManager.getIntByHashedValue(path.charAt(path.length()-3)));
					_character.get_currentCell().addCharacter(_character);
					SocketManager.GAME_SEND_BN_PACKET(_writer);
				}
			break;

		}
	}

	private void gameCreate() {
		if (this._character == null)
			return;

		SocketManager.GAME_SEND_GCK_PACKET(_writer, _character.get_name());
		SocketManager.GAME_SEND_As_PACKET(_character);
		SocketManager.GAME_SEND_ILS_PACKET(_character);
		SocketManager.GAME_SEND_GDM_PACKET(_writer, _character.get_currentMapID(),
				_character.get_currentMap().get_date(), _character.get_currentMap().get_key());
		SocketManager.GAME_SEND_BT_PACKET(_writer, Instant.now().toEpochMilli());
		_character.get_currentMap().addPlayer(_character);
	}

	private void gameInformations() {
		if (this._character == null)
			return;

		SocketManager.GAME_SEND_GM_PACKET(_writer, _character.get_currentMap().getCharacterGMPackets());
		// SocketManager.GAME_SEND_GM_PACKET(_writer,
		// "GM|+407;1;0;-1;875;-4;9059^100;0;860000;3792b9;fed880;0,0,0,0,0;;0");
		SocketManager.GAME_SEND_GDK_PACKET(_writer);
	}

	private void parseBasicsPackets(String packet) {
		switch (packet.charAt(1)) {
			case 'D':
				sendDate();
				break;
			case 'T':
				sendTime();
				break;
			case 'A':
				parseConsoleCommand(packet);
		}

	}

	private void parseConsoleCommand(String packet) {
		String commandPacket = packet.substring(2);
		String[] commandData = commandPacket.split(" ");
		String command = commandData[0];
		if(commandData.length == 0) return;

		if(command.equalsIgnoreCase("TELEPORT")) {
			short mapID = -1;
			int cellID = -1;
			try {
				mapID = Short.parseShort(commandData[1]);
				cellID = Integer.parseInt(commandData[2]);
			}catch(Exception e){};
			if(mapID == -1 || cellID == -1 || World.getMap(mapID) == null) {
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_writer, "MapID ou cellID invalide");
				return;
			}

			Maps newMap = World.getMap(mapID);
			if(newMap.get_cell(cellID) == null) {
				SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_writer, "CellID invalide");
				return;
			}

			_character.get_currentCell().removeCharacter(_character);
			SocketManager.GAME_SEND_MAPDATA(_writer, mapID, newMap.get_date(), newMap.get_key());
			newMap.addPlayer(_character);
			SocketManager.GAME_SEND_CONSOLE_MESSAGE_PACKET(_writer, "Le joueur a ete teleporte");
		}
	}

	private void sendTime() {
		final Date _date = new Date();
		SocketManager.GAME_SEND_BT_PACKET(_writer, _date.getTime());// + 3600000L;
	}

	public void sendDate() {
		final Date _date = new Date();
		LocalDate ld = _date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		String out = (ld.getYear() - 1370) + "|" +
				(String.valueOf(ld.getMonthValue() - 1).length() > 1 ? ld.getMonthValue() - 1
						: "0" + (ld.getMonthValue() - 1))
				+ "|" +
				(String.valueOf(ld.getDayOfMonth()).length() > 1 ? ld.getDayOfMonth() : "0" + ld.getDayOfMonth());

		SocketManager.GAME_SEND_BD_PACKET(_writer, out);
	}

	public void kick() {
		try {
			Echo.gameServer.deleteClient(this);

			/*
			 * if(_account != null)
			 * _account.logOut();
			 */

			if (!_socket.isClosed())
				_socket.close();

			_reader.close();
			_writer.close();
			_thread.interrupt();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void closeSocket() {
		try {
			this._socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public PrintWriter get_writer() {
		return _writer;
	}

}

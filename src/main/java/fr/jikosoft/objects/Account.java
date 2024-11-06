package fr.jikosoft.objects;

import java.util.Map;
import java.util.TreeMap;

import fr.jikosoft.game.GameThread;
import fr.jikosoft.kernel.CryptManager;
import fr.jikosoft.kernel.World;
import fr.jikosoft.login.LoginThread;


public class Account {
	private int accountID;
	private String _name;
	private String _pass;
	private String _nickName;
	private boolean _banned = false;
	private int _status;
	
	private GameThread _gameThread;
	private LoginThread _loginThread;
	private Charact _currentCharacter;
	private String _currentIP = "";
	private Boolean _logged = false;
	
	private Map<Integer, Charact> _characters = new TreeMap<Integer, Charact>();
	
	public Account(int guid, String name, String pass, String nickName, boolean banned) {
		
		this.accountID = guid;
		this._name = name;
		this._pass = pass;
		this._nickName = nickName;
		this._banned = banned;
	}
	
	public int getAccountID() {return accountID;}
	
	public String get_name() {
		return _name;
	}

	public String get_pass() {
		return _pass;
	}
	
	public String get_nickName() {
		return _nickName;
	}
	
	public void set_nickName(String nick) {
		_nickName = nick;
	}
	
	public boolean isBanned() {
		return _banned;
	}

	public int get_status() {
		return _status;
	}
	
	public void set_status(int s) {
		_status = s;
	}
	
	public String get_currentIP() {
		return _currentIP;
	}
	
	public boolean isLogged() {
		return _logged;
	}
	
	public void set_logged(boolean bool) {
		_logged = bool;
	}
	

	public void addCharacter(Charact charac) {
		_characters.put(charac.get_GUID(), charac);
	}
	
	public void deleteCharacter(int GUID) {
		_characters.remove(GUID);
	}
	
	public Map<Integer, Charact> getCharacters() {
		return _characters;
	}
	
	public void setLoginThread(LoginThread thread) {
		_loginThread = thread;
		System.out.println("Compte "+ _name + " | LoginThread : " + thread);
	}
	
	public LoginThread getLoginThread() {
		return _loginThread;
	}

	
	public void setGameThread(GameThread thread) {
		_gameThread = thread;
	}
	
	public GameThread getGameThread() {
		return _gameThread;
	}

	
	public void setCurrentIP(String IP) {
		_currentIP = IP;
	}

	public void setCurrentCharacter(Charact character) {
		_currentCharacter = character;
	}
	
	public static boolean checkPass(String accountName, String pass, String key) {
		if(World.getAccountByName(accountName) != null && World.getAccountByName(accountName).isValidPass(pass, key))
			return true;
		else
			return false;
	}

	private boolean isValidPass(String pass, String key) {
		return pass.equals(CryptManager.encryptPassword(key, _pass));
	}
	
	/*public boolean isOnline() {
		if(_gameThread != null || _realmThread != null)
			return true;
		return false;
	}*/
	
	/*public void logOut() {
		_currentCharacter = null;
		_gameThread = null;
		_realmThread = null;
		_currentIP = "";
		
		SQLManager.logOut(get_GUID(), 0);
		resetAllChars(true);
		SQLManager.UPDATE_ACCOUNT_DATA(this);
	}*/

}

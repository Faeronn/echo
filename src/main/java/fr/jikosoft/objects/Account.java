package fr.jikosoft.objects;

import java.util.Map;
import java.util.TreeMap;

import fr.jikosoft.game.GameThread;
import fr.jikosoft.kernel.CryptManager;
import fr.jikosoft.kernel.World;
import fr.jikosoft.login.LoginThread;

public class Account {
	private boolean isBanned = false;
	private String username;
	private String password;
	private String nickname;
	private int accountID;

	private Boolean isLogged = false;
	private String currentIPAddress;
	private LoginThread loginThread;
	private GameThread gameThread;

	private Map<Integer, Character> characters = new TreeMap<Integer, Character>();

	public Account(int accountID, String username, String password, String nickname, boolean isBanned) {
		this.accountID = accountID;
		this.username = username;
		this.password = password;
		this.nickname = nickname;
		this.isBanned = isBanned;
	}

	public String getCurrentIPAddress() {return this.currentIPAddress;}
	public LoginThread getLoginThread() {return this.loginThread;}
	public GameThread getGameThread() {return this.gameThread;}
	public String getUsername() {return this.username;}
	public String getPassword() {return this.password;}
	public String getNickname() {return this.nickname;}
	public int getAccountID() {return this.accountID;}
	public boolean isBanned() {return this.isBanned;}
	public boolean isLogged() {return this.isLogged;}

	public void setCurrentIPAddress(String newIPAddress) {this.currentIPAddress = newIPAddress;}
	public void setLoginThread(LoginThread thread) {this.loginThread = thread;}
	public void setGameThread(GameThread thread) {this.gameThread = thread;}
	public void setNickname(String nickname) {this.nickname = nickname;}
	public void setLogged(boolean isLogged) {this.isLogged = isLogged;}

	public void addCharacter(Character character) {this.characters.put(character.get_GUID(), character);}
	public Map<Integer, Character> getCharacters() {return this.characters;}
	public void deleteCharacter(int GUID) {this.characters.remove(GUID);}
	
	public static boolean checkPass(String accountName, String pass, String key) {
		return (World.getAccountByName(accountName) != null && World.getAccountByName(accountName).isValidPass(pass, key));
	}

	private boolean isValidPass(String pass, String key) {
		return pass.equals(CryptManager.encryptPassword(key, password));
	}
}

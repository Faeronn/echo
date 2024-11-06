package fr.jikosoft.kernel;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import fr.jikosoft.objects.Account;
import fr.jikosoft.objects.Charact;
import fr.jikosoft.objects.Maps;

public class World {
	private static Map<Integer, Charact> Characters = new TreeMap<Integer, Charact>();
	private static Map<Integer, Account> Accounts = new TreeMap<Integer, Account>();
	private static ArrayList<String> IP_Bans = new ArrayList<String>();
	private static Map<Short, Maps> Maps = new TreeMap<Short, Maps>();
	
	public static void createWorld() {
		System.out.println("\t#### Chargement des Données Dynamiques ####");

		System.out.println("\t\t> Chargement des Maps ....");
		//SQLManager.load_Maps();
		System.out.println("\t\t> Chargement des Maps : OK. (" + World.Maps.size() + ")\n");

		System.out.println("\t\t> Chargement des Comptes ....");
		SQLManager.load_Accounts();
		System.out.println("\t\t> Chargement des Comptes : OK. (" + World.Accounts.size() + ")\n");

		System.out.println("\t\t> Chargement des Personnages ....");
		//SQLManager.load_Characters();
		System.out.println("\t\t> Chargement des Personnages : OK. (" + World.Characters.size() + ")\n");

		System.out.println("\t\t> Récupération des BANS IP ....");
		//SQLManager.load_IP_Bans();
		System.out.println("\t\t> Récupération des BANS IP : OK. (" + World.IP_Bans.size() + ")\n");
	}
	
	public static void addAccount(Account ac) {
		Accounts.put(ac.getAccountID(), ac);
	}
	
	public static void deleteAccount(Account ac) {
		Accounts.remove(ac.getAccountID());
	}
	
	public static Account getAccount(int guid) {
		return Accounts.get(guid);
	}
	
	public static void addMap(Maps map) {
		if(!Maps.containsKey(map.get_ID()))
			Maps.put(map.get_ID(), map);
	}
	
	public static Maps getMap(short guid) {
		return Maps.get(guid);
	}
	
	public static void addCharacter(Charact ch) {
		Characters.put(ch.get_GUID(), ch);
	}
	
	public static void deleteCharacter(Charact ch) {
		Characters.remove(ch.get_GUID(), ch);
	}

	public static Account getAccountByName(String accountName) {
		for(int i=1; i <= Accounts.size(); i++) {
			String name = Accounts.get(i).get_name();
			
			if(name.equals(accountName)) {
				return Accounts.get(i);
			}
		}
		return null;
	}
	
	public static void addIPBan(String ip) {
		IP_Bans.add(ip);
	}
	
	public static boolean compareIPtoIPBans(String ip) {
		for(String ipsplit : IP_Bans) {
			if(ip.compareTo(ipsplit) == 0) 
				return true;
		}
		return false;
	}
	
	public static boolean compareNicknameToDB(String nick) {
		for(int i=1; i <= Accounts.size(); i++) {
			String nickName = Accounts.get(i).get_nickName();
			
			if(nickName.equals(nick)) 
				return true;
		}
		return false;
	}
	
	public static boolean compareNametoDB(String name) {
		for(int i=1; i <= Characters.size(); i++) {
			String _name = Characters.get(i).get_name();
			
			if(_name.equals(name)) 
				return true;
		}
		return false;
	}
}

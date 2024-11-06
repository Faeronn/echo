package fr.jikosoft.kernel;

import java.io.BufferedReader;
import java.io.FileReader;

import fr.jikosoft.game.GameServer;
import fr.jikosoft.login.LoginServer;


public class Echo {
	private static final String CONFIG_FILE = "config.txt";
	
	public static boolean DEBUG_MODE;
	
	public static String HOST_IP = "127.0.0.1";
	public static int GAME_PORT;
	public static int LOGIN_PORT;
	
	public static String DB_HOST;
	public static String DB_USER;
	public static String DB_PASS;
	public static String DB_NAME;
	
	public static boolean isRunning = false;
	public static GameServer gameServer;
	public static LoginServer loginServer;

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
	        public void run() {
	        	closeServer();
	        }
	    }
	    );
	    System.out.println("\n############################################################################");
	    
	    System.out.println("> Chargement de la config ....");
	    readConfig();
	    System.out.println("> Config : OK.\n");
	    
	    System.out.println("> Connexion à la Base de Données ....");
	    if(!SQLManager.setUpConnection()) {
			System.out.println(" ! ERREUR : Connexion invalide.\n");
			System.exit(0);
		}
		System.out.println("> Connexion : OK.\n");
	    
	    System.out.println("> Création du Monde ....");
	    long startT = System.currentTimeMillis();
		World.createWorld();
		System.out.println("> Monde : OK. (" + (float) ((System.currentTimeMillis() - startT)/1000) + " s)\n");
		
		
		isRunning = true;
		//System.out.println("> Lancement du Serveur JEU .... (Port : " + GAME_PORT + ").\n");
		//gameServer = new GameServer();
		System.out.println("> Lancement du Serveur CONNEXION .... (Port : " + LOGIN_PORT + ").\n");
		loginServer = new LoginServer();
	}
	
	
	public static void closeServer() {
		System.out.println("> Arrêt du Serveur demandé.");
		if(isRunning) SQLManager.closeConnection();
		System.out.println("> Arrêt du Serveur : OK.");
	}
	
	
	private static void readConfig() {
		try {
			BufferedReader config = new BufferedReader(new FileReader(CONFIG_FILE));
			String line;
			
			while ((line = config.readLine()) != null) {
				if(line.split("=").length != 2)
					continue;
				
				String param = line.split("=")[0].trim();
				String value = line.split("=")[1].trim();
				System.out.println("    - Param : " + param + " - " + value);
				
				switch(param.toUpperCase()) {
					case "DEBUG_MODE":
						if(value.equalsIgnoreCase("true")) {
							DEBUG_MODE = true;
							System.out.println("> @!  Mode DEBUG activé.");
						}
						break;
					case "HOST_IP":
						HOST_IP = value;
						break;
					case "GAME_PORT":
						GAME_PORT = Integer.parseInt(value);
						break;
					case "LOGIN_PORT":
						LOGIN_PORT = Integer.parseInt(value);
						break;
					case "DB_HOST":
						DB_HOST = value;
						break;
					case "DB_USER":
						DB_USER = value;
						break;
					case "DB_PASS":
						if(value == null)
							value = "";
						DB_PASS = value;
						break;
					case "DB_NAME":
						DB_NAME = value;
						break;
				}
			}
			config.close();
			
			if(DB_HOST == null || DB_USER == null || DB_PASS == null || DB_NAME == null)
				throw new Exception("Veuillez vérifier le fichier config.");
		}
		catch (Exception e) {
			System.out.println(" ! ERREUR : " + e.getMessage() + ".\n");
			System.exit(1);
		}
	}

}

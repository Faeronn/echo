package fr.jikosoft.kernel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;

import fr.jikosoft.game.GameServer;
import fr.jikosoft.login.LoginServer;

public class Echo {
	private static final String CONFIG_FILE = "config.txt";
	public static String HOST_IP = "127.0.0.1";
	public static boolean DEBUG_MODE;
	public static String DB_HOST;
	public static String DB_USER;
	public static String DB_PASS;
	public static String DB_NAME;
	public static int LOGIN_PORT;
	public static int GAME_PORT;

	public static boolean isRunning = false;
	public static LoginServer loginServer;
	public static GameServer gameServer;

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(Echo::closeServer, "Shutdown-Hook-Thread"));
		System.out.println("\n############################################################################");
		System.out.println("> Chargement de la config ....");
		readConfig();
		System.out.println("> Config : OK.\n");

		System.out.println("> Connexion à la Base de Données ....");
		if(!DatabaseManager.connect()) System.exit(1);
		System.out.println("> Connexion : OK.\n");

		System.out.println("> Création du Monde ....");
		long startT = System.currentTimeMillis();
		World.createWorld();
		System.out.println("> Monde : OK. (" + (float) ((System.currentTimeMillis() - startT)/1000) + " s)\n");

		isRunning = true;
		System.out.println("> Lancement du Serveur JEU .... (Port : " + GAME_PORT + ").\n");
		gameServer = new GameServer();
		System.out.println("> Lancement du Serveur CONNEXION .... (Port : " + LOGIN_PORT + ").\n");
		loginServer = new LoginServer();
	}

	public static void closeServer() {
		System.out.println("> Arrêt du Serveur demandé.");
		if(isRunning) DatabaseManager.disconnect();
		System.out.println("> Arrêt du Serveur : OK.");
	}

	private static void readConfig() {
		Properties properties = new Properties();
		try (BufferedReader configReader = new BufferedReader(new FileReader(CONFIG_FILE))) {
			properties.load(configReader);

			DEBUG_MODE = Boolean.parseBoolean(properties.getProperty("DEBUG_MODE", "false"));
			LOGIN_PORT = Integer.parseInt(properties.getProperty("LOGIN_PORT", "0"));
			GAME_PORT = Integer.parseInt(properties.getProperty("GAME_PORT", "0"));
			HOST_IP = properties.getProperty("HOST_IP", HOST_IP);
			DB_PASS = properties.getProperty("DB_PASS", "");
			DB_HOST = properties.getProperty("DB_HOST");
			DB_USER = properties.getProperty("DB_USER");
			DB_NAME = properties.getProperty("DB_NAME");
			if (DB_HOST == null || DB_USER == null || DB_NAME == null) throw new IllegalArgumentException("Veuillez vérifier le fichier config. Paramètres manquants.");

			if (DEBUG_MODE) {
				System.out.println("> @! Mode DEBUG activé.");
				properties.forEach((key, value) -> System.out.println("    - Param : " + key + " - " + value));
			}
		} catch (Exception e) {
			System.out.println(" ! ERREUR : " + e.getMessage() + ".\n");
			System.exit(1);
		}
	}
}

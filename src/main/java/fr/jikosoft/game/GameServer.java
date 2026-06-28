package fr.jikosoft.game;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Date;

import fr.jikosoft.kernel.Echo;
import fr.jikosoft.objects.Account;

public class GameServer implements Runnable {
	
	private ArrayList<GameThread> clients = new ArrayList<GameThread>();
	private ArrayList<Account> waitingAccounts = new ArrayList<Account>();
	private ServerSocket serverSocket;
	private Thread thread;
	
	private int allowedPlayerAmount = 0;
	private long serverStartTime;
	
	public GameServer() {
		try {
			serverSocket = new ServerSocket(Echo.GAME_PORT);
			serverStartTime = System.currentTimeMillis();
			
			thread = new Thread(this);
			thread.start();
		}
		catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
			System.exit(1);
		}
	}
	
	public void run() {
		while(Echo.isRunning) {
			try {
				GameThread clientThread = new GameThread(serverSocket.accept());
				System.out.println("> Game : New Client - " + clientThread.toString());
				clients.add(clientThread);
				if(clients.size() > allowedPlayerAmount) allowedPlayerAmount = clients.size();
			}
			catch(IOException e) {
				System.out.println("IOException: " + e.getMessage());
				try {
					if(!serverSocket.isClosed()) serverSocket.close();
					System.exit(1);
				}
				catch(IOException e1){
					System.out.println("IOException: " + e.getMessage());
				}
			}
		}
	}
	
	public void deleteClient(GameThread gameThread) {
		System.out.println("> Game : Shutting Client - " + gameThread.toString());
		clients.remove(gameThread);
		
		if(clients.size() > allowedPlayerAmount) allowedPlayerAmount = clients.size();
	}
	
	
	public synchronized Account getWaitingAccount(int GUID) {
		for (int i = 0; i < waitingAccounts.size(); i++) {
			if(waitingAccounts.get(i).getAccountID() == GUID) return waitingAccounts.get(i);
		}
		return null;
	}
	
	public synchronized void deleteWaitingAccount(Account account) {
		waitingAccounts.remove(account);
	}
	
	public synchronized void addWaitingAccount(Account account) {
		waitingAccounts.add(account);
	}
	
	public long getServerTime() {
		final Date date = new Date();
		return date.getTime();// + 3600000L;
	}

	public long getServerStartTime() {
		return serverStartTime;
	}
}

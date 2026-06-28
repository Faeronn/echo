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
	
	private int _maxPlayer = 0;
	private long _startTime;
	
	public GameServer() {
		try {
			serverSocket = new ServerSocket(Echo.GAME_PORT);
			_startTime = System.currentTimeMillis();
			
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
				clients.add(new GameThread(serverSocket.accept()));
				if(clients.size() > _maxPlayer)
					_maxPlayer = clients.size();
			}
			catch(IOException e) {
				System.out.println("IOException: " + e.getMessage());
				try {
					if(!serverSocket.isClosed())
						serverSocket.close();
					System.exit(1);
				}
				catch(IOException e1){
					System.out.println("IOException: " + e.getMessage());
				}
			}
		}
	}
	
	public void deleteClient(GameThread gameThread) {
		clients.remove(gameThread);
		
		if(clients.size() > _maxPlayer)
			_maxPlayer = clients.size();
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
	
	public static long getServerTime() {
		final Date _date = new Date();
		return _date.getTime();// + 3600000L;
	}

}

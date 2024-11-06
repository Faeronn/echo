package fr.jikosoft.game;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import fr.jikosoft.kernel.Echo;
import fr.jikosoft.kernel.World;
import fr.jikosoft.objects.Account;

public class GameServer implements Runnable {
	
	private ArrayList<GameThread> _clients = new ArrayList<GameThread>();
	private ArrayList<Account> _waitingAccounts = new ArrayList<Account>();
	private ServerSocket _serverSocket;
	private Thread _thread;
	
	private int _maxPlayer = 0;
	private long _startTime;
	
	public GameServer() {
		try {
			_serverSocket = new ServerSocket(Echo.GAME_PORT);
			_startTime = System.currentTimeMillis();
			
			_thread = new Thread(this);
			_thread.start();
		}
		catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
			System.exit(1);
		}
	}
	
	public void run() {
		while(Echo.isRunning) {
			try {
				_clients.add(new GameThread(_serverSocket.accept()));
				if(_clients.size() > _maxPlayer)
					_maxPlayer = _clients.size();
			}
			catch(IOException e) {
				System.out.println("IOException: " + e.getMessage());
				try {
					if(!_serverSocket.isClosed())
						_serverSocket.close();
					System.exit(1);
				}
				catch(IOException e1){
					System.out.println("IOException: " + e.getMessage());
				}
			}
		}
	}
	
	public void deleteClient(GameThread gameThread) {
		_clients.remove(gameThread);
		
		if(_clients.size() > _maxPlayer)
			_maxPlayer = _clients.size();
	}
	
	
	public synchronized Account getWaitingAccount(int GUID) {
		for (int i = 0; i < _waitingAccounts.size(); i++) {
			if(_waitingAccounts.get(i).getAccountID() == GUID)
				return _waitingAccounts.get(i);
		}
		return null;
	}
	
	public synchronized void deleteWaitingAccount(Account acc) {
		_waitingAccounts.remove(acc);
	}
	
	public synchronized void addWaitingAccount(Account acc) {
		_waitingAccounts.add(acc);
	}
	
	public static long getServerTime() {
		final Date _date = new Date();
		return _date.getTime();// + 3600000L;
	}

}

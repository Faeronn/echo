package fr.jikosoft.login;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import fr.jikosoft.kernel.Echo;
import fr.jikosoft.login.LoginThread;

public class LoginServer implements Runnable {
	
	private ServerSocket _serverSocket;
	private Thread _thread;
	private List<LoginThread> _clients = new ArrayList<LoginThread>();
	
	public LoginServer() {
		try {
			_serverSocket = new ServerSocket(Echo.LOGIN_PORT);
			_thread = new Thread(this);
			_thread.setDaemon(true);
			_thread.start();
		}
		catch(IOException e) {
			System.out.println("IOException: " + e.getMessage());
			System.exit(1);
		}
	}
	
	public void run() {
		while(Echo.Server_isRunning) {
			try {
				_clients.add(new LoginThread(_serverSocket.accept()));
			}
			catch(IOException e) {
				System.out.println("IOException: " + e.getMessage());
				try {
					System.out.println("Fermeture du serveur de connexion");	
					if(!_serverSocket.isClosed())
						_serverSocket.close();
				}
				catch(IOException e1) {
					System.out.println("IOException: " + e.getMessage());
				}
			}
		}
	}
	
	public void removeClient(LoginThread loginThread) {
		loginThread.kick();
		System.out.println("Suppression du LoginThread : " + loginThread);
		_clients.remove(loginThread);
		getClients();
	}
	
	public void getClients() {
		System.out.println("\n\n################################ DEBUG ################################");
		System.out.println("Liste des loginClients :");
		for(int i=0; i<_clients.size(); i++) {
			System.out.println("	> " + _clients.get(i));
		}
		System.out.println("\n\n");
	}

}
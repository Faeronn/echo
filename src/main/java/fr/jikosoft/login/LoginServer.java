package fr.jikosoft.login;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import fr.jikosoft.kernel.Echo;

public class LoginServer implements Runnable {
	private List<LoginThread> clients = new ArrayList<LoginThread>();
	private ServerSocket serverSocket;
	private Thread thread;
	
	public LoginServer() {
		try {
			serverSocket = new ServerSocket(Echo.LOGIN_PORT);
			thread = new Thread(this);
			thread.start();
		}
		catch(IOException e) {
			System.out.println("IOException: " + e.getMessage());
			System.exit(1);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Socket clientSocket = serverSocket.accept();
				LoginThread clientThread = new LoginThread(clientSocket);
				clients.add(clientThread);
			}
			catch(IOException e) {
				System.out.println("IOException: " + e.getMessage());
				if (serverSocket.isClosed()) {
					System.out.println("ServerSocket is closed. Exiting run loop.");
					break;
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
package fr.jikosoft.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import fr.jikosoft.database.AccountsManager;
import fr.jikosoft.kernel.Constants;
import fr.jikosoft.kernel.CryptManager;
import fr.jikosoft.kernel.Echo;
import fr.jikosoft.kernel.SocketManager;
import fr.jikosoft.kernel.World;
import fr.jikosoft.objects.Account;

public class LoginThread implements Runnable {
	private BufferedReader reader;
	private PrintWriter writer;
	private Thread thread;
	private Socket socket;
	private Account account;
	private Status status;

	private volatile boolean isRunning = true;
	private String hashKey;
	
	private String gameVersion;
	private String accountName;
	private String password;
	
	public LoginThread(Socket socket) {
		try {
			this.socket = socket;
			this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.writer = new PrintWriter(this.socket.getOutputStream());

			thread = new Thread(this);
			thread.setDaemon(true);
			thread.start();
			this.isRunning = true;
		}
		catch(IOException e) {
			System.out.println("IOException : " + e.getMessage());
			kick();
		}
		finally {
			if(this.account != null) {
				this.account.setLoginThread(null);
				this.account.setCurrentIPAddress("");
			}
		}
	}
	
	@Override
	public void run() {
		try {
			this.status = Status.wait_version;
			String packet = "";
			char[] charCur = new char[1];
	        
			this.hashKey = SocketManager.LOGIN_SEND_HC_PACKET(this.writer);
	        
	    	while(this.isRunning && this.reader.read(charCur, 0, 1) != -1 && Echo.isRunning) {
	    		if(this.status == Status.error) return;

	    		if (charCur[0] != '\u0000' && charCur[0] != '\n' && charCur[0] != '\r') packet += charCur[0];
	    		else if(!packet.isEmpty()) {
	    			if(Echo.DEBUG_MODE) System.out.println("Login: Recv << " + packet);
		    		
		    		parsePacket(packet);
		    		packet = "";
		    	}
	    	}
    	}
		catch(IOException error) {
			System.out.println("IOException : " + this + ": " + error.getMessage());
    	}
    	finally {
    		try {
	    		this.reader.close();
	    		this.writer.close();
	    		
	    		if(this.account != null) {
	    			this.account.setLoginThread(null);
	    			this.account.setCurrentIPAddress("");
	    		}
	    		if(!this.socket.isClosed())
	    			this.socket.close();
	    		
	    		this.thread.interrupt();
	    	}
    		catch(IOException e1){
    			System.out.println("IOException : " + e1.getMessage());
    			e1.printStackTrace();
    		}
    	}
	}

	private void parsePacket(String packet) {
		switch(this.status) {
			case wait_version://Game Version
				this.gameVersion = packet;
				this.status = Status.wait_account;
				break;
				
			case wait_account://Account Name
				this.accountName = packet;
				this.status = Status.wait_password;
				break;

			case wait_password://HashPass : #1hashpass
				this.password = packet;
				this.status = Status.wait_server;
				break;
				
			case wait_nickname:
				System.out.println("PHASE " + this.status);
				final String[] banList = { "admin", "modo", " ", "&", "\u00e9", "\"", "-", "'", "(", "\u00e8", "_", "\u00e7",
			        				       "\u00e0", ")", "=", "~", "#", "{", "[", "|", "`", "^", "@", "]", "}", "�", "+", "^",
			        				       "$", "\u00f9", "*", ",", ";", ":", "!", "<", ">", "�", "�", "%", "�", "?", ".", "/", "�", "\n" };

			    for(int i=0; i < banList.length; i++) {
			    	if (packet.contains(banList[i])) {
			    		SocketManager.LOGIN_SEND_AlEs_PACKET(this.writer); //Bad NickName Packet
			    		break;
			    	}
			    }
			    if(World.compareNicknameToDB(packet)) {
			    	SocketManager.LOGIN_SEND_AlEs_PACKET(this.writer); //Bad NickName Packet
		    		break;
			    }
			    
			    this.account.setNickname(packet);
			    AccountsManager.update(this.account);
			    this.status = Status.wait_server;
			    tryToLog();
				break;
				
			case wait_server:
				switch (packet.substring(0, 2)) {
					case "AF": {
						if(!World.compareNicknameToDB(packet.substring(2))) {
							SocketManager.LOGIN_SEND_AF_PACKET(this.writer, "null");
						}
						SocketManager.LOGIN_SEND_AF_PACKET(this.writer, "5" + "," + this.account.getCharacters().size());
						break;
					}
					case "AX": {
						SocketManager.LOGIN_SEND_AXK_PACKET(this.writer, this.account.getAccountID()); //Encrypted Game IP:Port Packet
						Echo.gameServer.addWaitingAccount(this.account);
						//SocketManager.LOGIN_SEND_AYK_PACKET(this.writer, this.account.get_GUID()); //Unencrypted Game IP:Port Packet
						break;
					}
					case "Af": {
						tryToLog();
						break;
					}
					case "Ax": {
						//SocketManager.LOGIN_SEND_AxK_PACKET(this.writer, this.account.get_subscriptionTime(),  5, this.account.getCharacters().size());
						break;
					}
					default:
						kick();
						break;
				}
				break;
			default:
				kick();
				break;
		}
	}
	
	private void tryToLog() {
		String ip = this.socket.getInetAddress().getHostAddress();
		if(!this.gameVersion.equalsIgnoreCase(Constants.REQUIRED_CLIENT_VERSION)) {
			SocketManager.LOGIN_SEND_AlEv_PACKET(this.writer); // Bad GameVersion Packet
			kick();
			return;
		}
		
		if(World.getAccountByName(this.accountName.toLowerCase()) == null) {
			SocketManager.LOGIN_SEND_AlEf_PACKET(this.writer); //Bad Account / Password Packet
			kick();
			return;
		}

		this.account = World.getAccountByName(this.accountName.toLowerCase());

		if(!CryptManager.encryptPassword(this.account.getPassword(), this.hashKey).equals(this.password)) {
			SocketManager.LOGIN_SEND_AlEf_PACKET(this.writer); //Bad Account / Password Packet
			kick();
			return;
		}
		
		if(this.account.isBanned() || World.compareIPtoIPBans(ip)) {
			SocketManager.LOGIN_SEND_AlEb_PACKET(this.writer); //Banned Account Packet
			kick();
			return;
		}
		
		if("".equals(this.account.getNickname()) || this.account.getNickname() == null) {
			SocketManager.LOGIN_SEND_AlEr_PACKET(this.writer); //Choose NickName Packet
			this.status = Status.wait_nickname;
			return;
		}
		
		if(this.account.isLogged() && this.account.getGameThread() == null) {
			SocketManager.LOGIN_SEND_AlEd_PACKET(this.writer); //"You've just disconnected a character already using this account"  Packet
			//Echo.loginServer.removeClient(this);
			//Echo.loginServer.removeClient(World.getAccount(this.account.getAccountID()).getLoginThread());
			return;
		}
		
		this.account.setLogged(true);
		this.account.setLoginThread(this);
		
		//SocketManager.LOGIN_SEND_Ad_PACKET(this.writer, this.account.getNickname());
		//SocketManager.LOGIN_SEND_Ac_PACKET(this.writer);
		//SocketManager.LOGIN_SEND_AH_PACKET(this.writer, 5, 1, 110, 1);
	}
	
	public void kick() {
		this.isRunning = false;
	    try {
			this.socket.shutdownInput();
			this.socket.close();
			Echo.loginServer.removeClient(this);
		}
	    catch (final Exception e) {
			System.out.println("kick: " + e.getMessage());
			e.printStackTrace();
		}	    
	}
	
	
	private enum Status {
		wait_version("wait_version", 0),
		wait_account("wait_account", 1),
		wait_password("wait_password", 2),
		wait_nickname("wait_nickname", 3),
		wait_server("wait_server", 4),
		error("error", 5);
		
		private Status(final String s, final int n) {
		}
		
	}
	
}


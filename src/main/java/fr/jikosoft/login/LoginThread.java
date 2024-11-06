package fr.jikosoft.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import fr.jikosoft.kernel.Constants;
import fr.jikosoft.kernel.CryptManager;
import fr.jikosoft.kernel.Echo;
import fr.jikosoft.kernel.SQLManager;
import fr.jikosoft.kernel.SocketManager;
import fr.jikosoft.kernel.World;
import fr.jikosoft.objects.Account;

public class LoginThread implements Runnable {
	private BufferedReader _reader;
	private PrintWriter _writer;
	private Thread _thread;
	private Socket _socket;
	private Account _account;
	private Status _status;

	private String _hashKey;
	
	private String _gameVersion;
	private String _accountName;
	private String _password;
	
	public LoginThread(Socket socket) {
		try {
			_socket = socket;
			_reader = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
			_writer = new PrintWriter(_socket.getOutputStream());
			_thread = new Thread(this);
			_thread.setDaemon(true);
			_thread.start();
		}
		catch(IOException e) {
			System.out.println("IOException : " + e.getMessage());
			try {
				if(!_socket.isClosed())
					_socket.close();
			}
			catch (IOException e1) {
				System.out.println("IOException : " + e1.getMessage());
			}
		}
		finally {
			if(_account != null) {
				_account.setLoginThread(null);
				//_account.setGameThread(null);
				_account.setCurrentIP("");
			}
		}
	}
	
	public void run() {
		try {
			_status = Status.wait_version;
			String packet = "";
			char[] charCur = new char[1];
			/*if(Echo.CONFIG_POLICY)
				SocketManager.REALM_SEND_POLICY_FILE(_out);*/
	        
			_hashKey = SocketManager.LOGIN_SEND_HC_PACKET(_writer);
	        
	    	while(_reader.read(charCur, 0, 1) != -1 && Echo.isRunning) {
	    		if(_status == Status.error) {
	    			return;
	    		}
	    		if (charCur[0] != '\u0000' && charCur[0] != '\n' && charCur[0] != '\r') {
	    			packet += charCur[0];
		    	}
	    		else if(!packet.isEmpty()) {
	    			if(Echo.DEBUG_MODE)
	    				System.out.println("Login: Recv << " + packet);
		    		
		    		parsePacket(packet);
		    		packet = "";
		    	}
	    	}
    	}
		
		catch(IOException e) {
			System.out.println("IOException : " + this + "\n" + e.getMessage());
			e.printStackTrace();
    		try {
	    		_reader.close();
	    		_writer.close();
	    		
	    		if(_account != null) {
	    			_account.setCurrentCharacter(null);
	    			//_account.setGameThread(null);
	    			_account.setLoginThread(null);
	    			_account.setCurrentIP("");

					System.out.println("Pourquoi tu fais �a chef ?");
	    		}
	    		if(!_socket.isClosed())
	    			_socket.close();
	    		
	    		_thread.interrupt();
	    	}
    		catch(IOException e1) {
    			System.out.println("IOException : " + e1.getMessage());
    		}
    	}
    	finally {
    		System.out.println("finally : " + this + "\n");
    		try {
	    		_reader.close();
	    		_writer.close();
	    		
	    		if(_account != null) {
	    			_account.setCurrentCharacter(null);
	    			//_account.setGameThread(null);
	    			_account.setLoginThread(null);
	    			_account.setCurrentIP("");
	    			
					System.out.println("Pourquoi tu fais �a chef 2?");
	    			
	    		}
	    		if(!_socket.isClosed())
	    			_socket.close();
	    		
	    		_thread.interrupt();
	    	}
    		catch(IOException e1){
    			System.out.println("IOException : " + e1.getMessage());
    			e1.printStackTrace();
    		}
    	}
	}

	private void parsePacket(String packet) {
		switch(_status) {
		
			case wait_version://Game Version
				_gameVersion = packet;
				_status = Status.wait_account;
				break;
				
			case wait_account://Account Name
				_accountName = packet;
				_status = Status.wait_password;
				break;

			case wait_password://HashPass : #1hashpass
				_password = packet;
				_status = Status.wait_server;
				break;
				
			case wait_nickname:
				System.out.println("PHASE " + _status);
				final String[] banList = { "admin", "modo", " ", "&", "\u00e9", "\"", "-", "'", "(", "\u00e8", "_", "\u00e7",
			        				       "\u00e0", ")", "=", "~", "#", "{", "[", "|", "`", "^", "@", "]", "}", "�", "+", "^",
			        				       "$", "\u00f9", "*", ",", ";", ":", "!", "<", ">", "�", "�", "%", "�", "?", ".", "/", "�", "\n" };

			    for(int i=0; i < banList.length; i++) {
			    	if (packet.contains(banList[i])) {
			    		SocketManager.LOGIN_SEND_AlEs_PACKET(_writer); //Bad NickName Packet
			    		break;
			    	}
			    }
			    if(World.compareNicknameToDB(packet)) {
			    	SocketManager.LOGIN_SEND_AlEs_PACKET(_writer); //Bad NickName Packet
		    		break;
			    }
			    
			    _account.set_nickName(packet);
			    SQLManager.save_Account(_account);
			    _status = Status.wait_server;
			    tryToLog();
				break;
				
			case wait_server:
				System.out.println("PHASE " + _status);
				switch (packet.substring(0, 2)) {
					case "AF": {
						if(!World.compareNicknameToDB(packet.substring(2))) {
							SocketManager.LOGIN_SEND_AF_PACKET(_writer, "null");
						}
						SocketManager.LOGIN_SEND_AF_PACKET(_writer, "5" + "," + _account.getCharacters().size());
						break;
					}
					case "AX": {
						SocketManager.LOGIN_SEND_AXK_PACKET(_writer, _account.get_GUID()); //Encrypted Game IP:Port Packet
						Echo.gameServer.addWaitingAccount(_account);
						//SocketManager.LOGIN_SEND_AYK_PACKET(_writer, _account.get_GUID()); //Unencrypted Game IP:Port Packet
						break;
					}
					case "Af": {
						tryToLog();
						break;
					}
					case "Ax": {
						//SocketManager.LOGIN_SEND_AxK_PACKET(_writer, _account.get_subscriptionTime(),  5, _account.getCharacters().size());
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
		
		String ip = _socket.getInetAddress().getHostAddress();
		System.out.println(ip);
		
		if(!_gameVersion.equalsIgnoreCase(Constants.REQUIRED_CLIENT_VERSION)) {
			SocketManager.LOGIN_SEND_AlEv_PACKET(_writer); // Bad GameVersion Packet
			kick();
			return;
		}
		
		SocketManager.LOGIN_SEND_Af_PACKET(_writer, 1, 1, 0, "", -1);
		//client.send("Af0|0|0|1|-1");
		
		if(World.getAccountByName(_accountName.toLowerCase()) == null) {
			SocketManager.LOGIN_SEND_AlEf_PACKET(_writer); //Bad Account / Password Packet
			kick();
			return;
		}
		
		_account = World.getAccountByName(_accountName.toLowerCase());
		
		if(!CryptManager.encryptPassword(_account.get_pass(), _hashKey).equals(_password)) {
			SocketManager.LOGIN_SEND_AlEf_PACKET(_writer); //Bad Account / Password Packet
			kick();
			return;
		}
		
		if(_account.isBanned() || World.compareIPtoIPBans(ip)) {
			SocketManager.LOGIN_SEND_AlEb_PACKET(_writer); //Banned Account Packet
			kick();
			return;
		}
		
		if(_account.get_nickName().equals("")) {
			SocketManager.LOGIN_SEND_AlEr_PACKET(_writer); //Choose NickName Packet
			_status = Status.wait_nickname;
			return;
		}
		
		if(_account.isLogged() && _account.getGameThread() == null) {
			SocketManager.LOGIN_SEND_AlEd_PACKET(_writer); //"You've just disconnected a character already using this account"  Packet
			Echo.loginServer.removeClient(this);
			Echo.loginServer.removeClient(World.getAccount(_account.get_GUID()).getLoginThread());
			return;
		}
		
		/*if(_account.isLogged() && _account.getGameThread() != null) {
			_account.getGameThread().closeSocket();
		}*/
		
		_account.set_logged(true);
		_account.setLoginThread(this);
		
		
		Echo.loginServer.getClients();
		
		
		//client.send("Af0|0|0|1|-1");
		SocketManager.LOGIN_SEND_Ad_PACKET(_writer, _account.get_nickName());
		SocketManager.LOGIN_SEND_Ac_PACKET(_writer);
		SocketManager.LOGIN_SEND_AH_PACKET(_writer, 5, 1, 110, 1); //AH[ID];[State];[Completion];[CanLog]
		//SocketManager.LOGIN_SEND_AlK_PACKET(_writer, (_account.get_gmLevel() > 0 ? true : false));
		//SocketManager.LOGIN_SEND_AQ_PACKET(_writer, _account.get_question());
	}
	
	public void kick() {
	    try {
			
			_socket.shutdownInput();
			_socket.close();
			
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


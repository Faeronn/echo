package fr.jikosoft.kernel;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Random;

import fr.jikosoft.objects.Charact;
import fr.jikosoft.objects.Maps;



public class SocketManager {
	public static void send(PrintWriter writer, String packet) {
		if(writer != null && !packet.equals("") && !packet.equals(""+(char)0x00)) {
			packet = CryptManager.toUtf(packet);
			writer.print(packet + (char)0x00);
			writer.flush();
		}
	}
	
	public static void sendToCharacter(Charact charac, final String packet) {
		if (charac != null && charac.get_account() != null && charac.get_account().getGameThread() != null) {
			send(charac.get_account().getGameThread().get_writer(), packet);
		}
}
	
	
	/*###################################################################################################
	 * 							            LOGIN PACKETS                                               *
	 ####################################################################################################*/
	
	public static String LOGIN_SEND_HC_PACKET(PrintWriter writer) { //Key Packet : HC + 32 random letters
		String alpha = "abcdefghijklmnopqrstuvwxyz";
		StringBuilder hashkey = new StringBuilder();
        Random rand = new Random();
        
        for (int i=0; i < 32; i++) {
               hashkey.append(alpha.charAt(rand.nextInt(alpha.length())));
        }
        String packet = "HC" + hashkey;
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);
		
		return hashkey.toString();
	}

	public static void LOGIN_SEND_AlEv_PACKET(PrintWriter writer) { // Bad GameVersion Packet
		String packet = "AlEv" + Constants.REQUIRED_CLIENT_VERSION;
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);
	}
	
	public static void LOGIN_SEND_AlEf_PACKET(PrintWriter writer) { //Bad Account / Password Packet
		String packet = "AlEf";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);
	}
	
	public static void LOGIN_SEND_AlEb_PACKET(PrintWriter writer) { //Banned Account Packet
		String packet = "AlEb";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);
	}
	
	public static void LOGIN_SEND_AlEc_PACKET(PrintWriter writer) { //Already Connected
		String packet = "AlEc";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);	
	}
	
	public static void LOGIN_SEND_AlEd_PACKET(PrintWriter writer) { //Disconnected account Packet
		String packet = "AlEd";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);	
	}
	
	public static void LOGIN_SEND_AlEr_PACKET(PrintWriter writer) { //Choose NickName Packet
		String packet = "AlEr";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);	
	}
	
	public static void LOGIN_SEND_AlEs_PACKET(PrintWriter writer) { //Bad NickName Packet
		String packet = "AlEs";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);	
		
	}
	
	public static void LOGIN_SEND_Af_PACKET(PrintWriter writer, int position, int totalSub, int totalNonSub, String subscribe, int queueID) {
		String packet = "Af" + position + "|" + totalSub + "|"+ totalNonSub + "|" + subscribe + "|" + queueID;
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet.toString());
	}
	
	public static void LOGIN_SEND_Ad_PACKET(PrintWriter writer, String nickName) { //Get NickName Packet
		String packet = "Ad" + nickName;
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);
	}
	
	public static void LOGIN_SEND_Ac_PACKET(PrintWriter writer) { //??? Packet
		String packet = "Ac0";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);
	}
	
	public static void LOGIN_SEND_AH_PACKET(PrintWriter writer, int serverId, int state, int max, int canLog) { //Server Infos Packet
		String packet = "AH" + serverId + ";" + state + ";" + max + ";" + canLog;
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);
	}
	
	public static void LOGIN_SEND_AlK_PACKET(PrintWriter writer, boolean isGM) { //GM Check Packet
		String packet = "AlK" + (isGM? "1" : "0");
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);
	}
	
	public static void LOGIN_SEND_AQ_PACKET(PrintWriter writer, String question) { //Secret Question Packet
		String packet = "AQ" + question;
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);
	}
	
	public static void LOGIN_SEND_AxK_PACKET(PrintWriter writer, long subscriptionTime, int serverId, int characters) { //Characters info Packet
		String packet = "AxK"+ subscriptionTime;
		for(int i=0; i < characters; i++) {
			packet += "|" + serverId + "," + characters;
		}
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);
	}
	
	public static void LOGIN_SEND_AXEr_PACKET(PrintWriter writer) {//Server : Forbidden Access Packet (GM Server)
		String packet = "AXEr";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);
	}
	
	public static void LOGIN_SEND_AXEd_PACKET(PrintWriter writer) {//Server : Forbidden Access Packet (State !=1)
		String packet = "AXEd";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);
	}
	
	public static void LOGIN_SEND_AXEf_PACKET(PrintWriter writer) {//Server : Forbidden Access Packet (Server Full)
		String packet = "AXEf";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);
	}
	
	public static void LOGIN_SEND_AF_PACKET(PrintWriter writer, String data) {//Find Friend Packet
		String packet = "AF" + data; //TODO : Characters on multiples servers
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);
	}
	
	public static void LOGIN_SEND_AXK_PACKET(PrintWriter writer, int GUID) { //Encrypted Game Server IP Packet
		String packet = "AXK";
		
		if(!Echo.HOST_IP.equals("127.0.0.1"))
			packet += CryptManager.encryptIP(Echo.HOST_IP) + CryptManager.encryptPort(Echo.GAME_PORT) + GUID;
		else
			packet += CryptManager.encryptIP("127.0.0.1") + CryptManager.encryptPort(Echo.GAME_PORT) + GUID;
		
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);
	}
	
	public static void LOGIN_SEND_AYK_PACKET(PrintWriter writer, int GUID) { //Unencrypted Game Server IP Packet
		String packet = "AYK" + Echo.HOST_IP + ":" + Echo.GAME_PORT + ";" + GUID;
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);
	}
	
	public static void LOGIN_SEND_WA_PACKET(PrintWriter writer, int guid) {//???? Packet
		String packet = "WA" + guid;
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Login: Send >> " + packet);
	}
	
	
	
	/*###################################################################################################
	 * 							            GAME PACKETS                                                *
	 ####################################################################################################*/
	
	public static void GAME_SEND_HG_PACKET(PrintWriter writer) { //HelloGame Packet
		String packet = "HG";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	
	public static void GAME_SEND_ATK_PACKET(PrintWriter writer) { //Ticket Succes Packet
		String packet = "ATK0";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);	
	}
	
	public static void GAME_SEND_ATE_PACKET(PrintWriter writer) { //Ticket Failure Packet
		String packet = "ATE";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);	
	}
	
	public static void GAME_SEND_AV_PACKET(PrintWriter writer) { //Send Regional Version Packet
		String packet = "AV0";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);	
	}
	
	public static void GAME_SEND_ALK_PACKET(PrintWriter writer, long subscriptionTime, Map<Integer, Charact> characters) {
		String packet = "ALK" + subscriptionTime + "|" + characters.size(); //Character List Packet
		
		for(Map.Entry<Integer, Charact> entry : characters.entrySet()) {
			packet += entry.getValue().getCharacterData();
		}
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);	
	}
	
	
	public static void GAME_SEND_AP_PACKET(PrintWriter writer) { //Send Random Name Packet
		String packet = "APK";
		String[] syllableList = {"a", "e", "i", "o", "u", "y", "p", "b", "n"};
		String name = "";
		
		for(int i=0; i < new Random().nextInt(3) + 2; i++) {
			name += syllableList[new Random().nextInt(syllableList.length)];
		}
		packet += name.substring(0, 1).toUpperCase() + name.substring(1);
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	
	public static void GAME_SEND_ASK_PACKET(PrintWriter writer, Charact charac) {
		String packet = "ASK|" + charac.get_GUID() + "|" + charac.get_name() + "|" + charac.get_level() + "|" + "1" + "|" + 
				        charac.get_sex() + "|"  + charac.get_class() + charac.get_sex() + "|" + charac.get_color1() + "|" + 
				        charac.get_color2() + "|" + charac.get_color3() + "|" + "";
		
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_ASE_PACKET(PrintWriter writer) { //
		String packet = "ASE";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_ADE_PACKET(PrintWriter writer) {// Delete Character Error Packet
		String packet = "ADE";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_AAEs_PACKET(PrintWriter writer) {// Create Character Error Packet : Subscription out 
		String packet = "AAEs";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_AAEf_PACKET(PrintWriter writer) {// Create Character Error Packet : Full Character List
		String packet = "AAEf";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_AAEa_PACKET(PrintWriter writer) {// Create Character Error Packet : Name Already Exists
		String packet = "AAEa";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_AAEn_PACKET(PrintWriter writer) {// Create Character Error Packet : Bad Name
		String packet = "AAEn";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_AAEe_PACKET(PrintWriter writer) {// Create Character Error Packet
		String packet = "AAEe";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_AAK_PACKET(PrintWriter writer) {// Create Character Succes Packet
		String packet = "AAK";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	
	public static void GAME_SEND_Rx_PACKET(Charact charac, String xp) {//Mount XP Given Packet
		final String packet = "Rx" + xp;
		sendToCharacter(charac, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_ZS_PACKET(PrintWriter writer, String alignment) { //Alignment Packet 
		String packet = "ZS" + alignment;
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_cC_PACKET(PrintWriter writer, String channels) { //Channels Packet 
		String packet = "cC+" + channels;
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_al_PACKET(PrintWriter writer) { //SubArea Packet
		String packet = "al|"; //FIXME "al|" + subArea List;
		packet += "270;0|49;1|319;0|98;0|147;0|466;2|245;0|515;2|294;0|73;1|122;2|171;2|441;0|220;0|490;2|269;0|48;1|318;0|97;0|146;0|465;0|244;0|514;1|23;2|293;0|72;2|121;2|170;2|440;0|219;0|268;0|47;1|317;0|96;0|145;0|464;1|243;0|513;1|22;2|292;0|71;2|120;2|169;2|218;0|488;2|267;0|46;1|316;2|95;0|144;0|463;0|512;1|21;0|291;0|70;2|119;2|168;2|217;0|487;0|266;0|536;0|45;1|315;2|94;0|143;2|462;0|511;2|20;0|290;0|69;2|339;0|118;2|167;2|216;0|486;2|44;1|314;2|93;2|461;2|510;2|19;0|289;0|68;2|338;0|117;2|166;2|215;0|485;2|43;1|313;0|92;0|141;0|460;0|509;2|18;0|288;0|67;2|337;0|116;2|165;2|214;0|484;2|42;0|312;0|91;2|140;0|459;0|508;2|17;0|287;0|66;2|336;0|115;2|164;2|213;0|483;2|41;0|311;0|139;0|507;2|16;0|286;0|65;2|335;0|114;2|163;2|212;0|482;2|261;0|40;0|310;0|89;0|138;0|457;2|236;0|506;2|15;0|285;0|64;2|334;2|113;2|162;2|211;0|481;2|260;0|39;0|309;0|88;0|137;0|235;2|505;2|14;0|284;0|63;2|333;0|112;2|161;2|210;0|480;2|259;0|38;2|308;0|87;2|136;0|455;1|234;2|504;2|13;0|62;2|332;0|111;2|209;2|479;2|258;0|37;1|307;0|86;0|135;0|454;2|233;2|503;2|12;2|61;2|331;0|110;0|159;0|208;0|478;2|257;0|306;0|85;0|134;0|453;2|232;2|502;2|11;2|281;0|60;0|330;0|109;2|158;0|207;0|477;2|256;0|35;0|84;0|133;0|182;2|452;0|231;2|501;0|10;2|280;2|59;2|329;0|108;2|157;0|206;0|476;2|255;0|34;0|304;0|83;0|132;0|181;0|451;0|230;2|500;2|9;2|279;1|328;0|107;2|156;0|205;0|254;0|33;2|303;0|82;0|131;0|180;0|450;0|229;0|499;2|8;2|278;0|57;2|327;0|106;2|155;0|204;0|474;0|253;2|32;2|302;0|81;2|130;0|179;2|449;0|228;0|498;0|7;2|277;2|56;2|326;0|105;2|154;0|203;0|473;0|252;0|31;2|301;0|80;2|129;0|178;2|448;0|227;0|497;0|6;2|276;2|55;2|325;0|153;0|202;0|472;1|251;0|30;0|300;0|79;2|128;0|177;2|447;0|226;0|496;0|5;2|275;2|54;2|324;0|103;2|152;2|201;0|471;2|250;0|29;2|299;0|78;0|127;0|446;0|225;0|495;0|4;2|274;0|53;2|323;0|102;2|151;0|200;0|470;0|249;0|28;2|298;0|77;0|126;0|175;0|445;0|224;0|494;0|3;2|273;0|322;0|101;0|150;0|469;1|248;0|27;2|297;0|76;2|125;0|174;0|444;0|223;0|493;0|2;2|272;0|51;1|321;0|100;0|149;0|468;2|247;0|26;0|296;0|75;2|124;0|173;0|443;0|222;0|492;2|1;2|271;0|50;1|320;0|99;0|148;0|467;2|246;0|25;2|295;0|74;2|123;2|442;0|221;0|491;0|0;0";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_SLo_PACKET(PrintWriter writer, String option) { //See Spells Packet
		String packet = "SLo" + option;
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_SL_PACKET(PrintWriter writer) { //Spell List Packet
		String packet = "SL" + "121~1~b;125~1~c;128~1~d;"; //FIXME "SL" + "spellList";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_AR_PACKET(PrintWriter writer, String restrictions) { //Restrictions Packet
		String packet = "AR" + restrictions; //FIXME "AR" + restrictions ["6bk"];
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_Ow_PACKET(Charact charac) { //Pods Packet
		String packet = "Ow" + "0|1000";//charac.get_podsUsed() + "|" + charac.get_maxPods();
		sendToCharacter(charac, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_FO_PACKET(PrintWriter writer, String option) {//See Friends Online Option Packet
		String packet = "FO" + option;
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_Im_PACKET(Charact charac, String extraData) { //Message Packet
		String packet = "Im" + extraData;
		sendToCharacter(charac, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_TB_PACKET(Charact charac) { //Tutorial Begin Packet
		String packet = "TB";
		sendToCharacter(charac, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_GCK_PACKET(PrintWriter writer, String name) { //Game Create Packet
		String packet = "GCK|1|" + name;
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_As_PACKET(Charact charac) { //Stats Packet
		String packet = "As" + charac.getCharacterStats();
		sendToCharacter(charac, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_ILS_PACKET(Charact charac) { //Regen Rate Packet
		String packet = "ILS2000";
		sendToCharacter(charac, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_GDM_PACKET(PrintWriter writer, int id, String date, String key) { //MapData Packet
		String packet = "GDM|" + id + "|" + date + "|" + key;
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	
	public static void GAME_SEND_BT_PACKET(PrintWriter writer, String epochDate) { //Time Reference Packet
		String packet = "BT" + epochDate;
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_BD_PACKET(PrintWriter writer, String date) { //Date yyy|mm|dd Packet
		String packet = "BD" + date;
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_BT_PACKET(PrintWriter writer, long date) { //Date ms Packet
		String packet = "BT" + date;
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_GM_PACKET(PrintWriter writer, String data) { //Map Infos (players, npcs, ...)
		String packet = data;
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_GM_PACKET(Maps map, Charact character) { //Map Infos (players, npcs, ...)
		String packet = "GM|+" + character.getCharacterGMData();
		
		for(Charact c : map.getCharacters()) {
			if(c != character) {
				sendToCharacter(c, packet);	
				
				if(Echo.DEBUG_MODE)
					System.out.println("Game: " + c.get_name() + " | Send >> " + packet);
			}
		}
	}
	
	public static void GAME_SEND_GDK_PACKET(PrintWriter writer) { //Map Loaded Packet
		String packet = "GDK";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_EW_PACKET(PrintWriter writer, Charact character) { //Map Loaded Packet
		String packet = "EW+" + character.get_GUID();
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	
	
	
	public static void GAME_SEND_BN_PACKET(PrintWriter writer) {
		String packet = "BN";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
	
	public static void GAME_SEND_PONG(PrintWriter writer) {
		String packet = "pong";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}

	public static void GAME_SEND_QPONG(PrintWriter writer) {
		String packet = "qpong";
		send(writer, packet);
		
		if(Echo.DEBUG_MODE)
			System.out.println("Game: Send >> " + packet);
	}
}

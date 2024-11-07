package fr.jikosoft.kernel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.jikosoft.objects.Maps;
import fr.jikosoft.objects.Maps.Cell;

public class CryptManager {
	
	public static String toUtf(String in) { //ANSI(Unicode) -> UTF-8
		String out = "";
		try {
			out = new String(in.getBytes("UTF8"));
		}
		catch(Exception e) {
			System.out.println (" ! ERROR : Conversion en UTF-8 echouée ! : " + e.getMessage());
		}
		
		return out;
	}
	
	
	
	
	
	public static String toUnicode(String in) {
		String out = "";

		try {
			out = new String(in.getBytes(), "UTF8");	
		}
		catch(Exception e) {
			System.out.println ("Conversion en UTF-8 echoue! : " + e.getMessage());
		}
		
		return out;
	}

	public static String encryptPassword(String pass, String key) {
		char[] charList = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
						   't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
						   'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U','V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4',
						   '5', '6', '7', '8', '9', '-', '_'};

	        String out = "#1";

	        for (int i=0; i < pass.length(); i++) {
	            char cPass = pass.charAt(i);
	            char cKey = key.charAt(i);

	            out += charList[( (int)cPass / 16 + (int)cKey) % charList.length];
	            out += charList[( (int)cPass % 16 + (int)cKey) % charList.length];
	        }
	        return out;
	}
	
	public static String encryptIP(String ip) {
		String[] numbers = ip.split("\\.");
		String out = "";
        int count = 0;
        
        for(int i=0; i < 16; i++) {
            for(int j=0; j < 16; j++) {
                if((i*16 + j) == Integer.parseInt(numbers[count])) {
                    Character A = (char)(i + 48);
                    Character B = (char)(j + 48);
                    
                    out += A.toString() + B.toString();
                    
                    i = j = 0;
                    count++;
                    
                    if (count > 3)
                        return out;
                }
            }
        }
        return "DD";
    }
	
	public static String encryptPort(int port) {
		char[] charList = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
				   't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
				   'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U','V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4',
				   '5', '6', '7', '8', '9', '-', '_'};
		
		int p = port;
		String out = "";
		
		for(int a=2; a >= 0; a--) {
			out += charList[(int)(p / Math.pow(64, a))];
			p = (int)(p % Math.pow(64, a));
		}
		
		return out;
	}

	
	public static Map<Short, Cell> decompileMapData(Maps map, String dData) {
		Map<Short, Cell> cells = new TreeMap<Short, Cell>();
		
		for (int f = 0; f < dData.length(); f += 10) {
			String cellData = dData.substring(f, f + 10);
			List<Byte> cellInfo = new ArrayList<Byte>();
			
		    for (int i = 0; i < cellData.length(); i++) {
		    	cellInfo.add((byte)getIntByHashedValue(cellData.charAt(i)));
		    }
		    
		    int Type = (cellInfo.get(2) & 56) >> 3;
		    boolean IsSightBlocker = (cellInfo.get(0) & 1) != 0;
		    boolean layerObject2Interactive = ((cellInfo.get(7) & 2) >> 1) != 0;
		    int layerObject2 = ((cellInfo.get(0) & 2) << 12) + ((cellInfo.get(7) & 1) << 12) + (cellInfo.get(8) << 6) + cellInfo.get(9); 
		    int obj = (layerObject2Interactive?layerObject2:-1);
		  
		    cells.put((short) (f/10), new Cell(map, (short) (f/10), Type!=0, IsSightBlocker, obj));
	    }
		return cells;
	}
	
	
	public static int getIntByHashedValue(char c) {
		char[] HASH = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
	            't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
	            'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'};
		
		for(int a = 0; a<HASH.length; a++) {
			if(HASH[a] == c) {
				return a;
			}
		}	
		return -1;
	}
}

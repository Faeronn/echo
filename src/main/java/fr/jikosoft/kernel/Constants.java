package fr.jikosoft.kernel;

public class Constants {
	public static final	String REQUIRED_CLIENT_VERSION = "1.29.1";
	
	public static short getStartMapID(int classID) {
        short mapID = 10298;
        switch (classID) {
            case 1:
                mapID = 10300;
                break;
            case 2:
                mapID = 10284;
                break;
            case 3:
                mapID = 10299;
                break;
            case 4:
                mapID = 10285;
                break;
            case 5:
                mapID = 10298;
                break;
            case 6:
                mapID = 10276;
                break;
            case 7:
                mapID = 10283;
                break;
            case 8:
                mapID = 10294;
                break;
            case 9:
                mapID = 10292;
                break;
            case 10:
                mapID = 10279;
                break;
            case 11:
                mapID = 10296;
                break;
            case 12:
                mapID = 10289;
                break;
        }

        return mapID;
    }

    public static short getStartCellID(int classID) {
        short cellID = 314;
        switch (classID) {
            case 1:
                cellID = 323;
                break;
            case 2:
                cellID = 372;
                break;
            case 3:
                cellID = 271;
                break;
            case 4:
                cellID = 263;
                break;
            case 5:
                cellID = 300;
                break;
            case 6:
                cellID = 296;
                break;
            case 7:
                cellID = 299;
                break;
            case 8:
                cellID = 309;
                break;
            case 9:
                cellID = 284;
                break;
            case 10:
                cellID = 254;
                break;
            case 11:
                cellID = 243;
                break;
            case 12:
                cellID = 236;
                break;
        }
        return cellID;
    }
}

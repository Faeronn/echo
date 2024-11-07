package fr.jikosoft.kernel;

public class Constants {
	public static final	String REQUIRED_CLIENT_VERSION = "0.0.1";

	public static short getStartMapID(int classID) {
		short[] mapIDs = {10298, 10300, 10284, 10299, 10285, 10298, 10276, 10283, 10294, 10292, 10279, 10296, 10289};
		return (classID >= 1 && classID <= 12) ? mapIDs[classID] : 10298;
	}

	public static short getStartCellID(int classID) {
		short[] cellIDs = {314, 323, 372, 271, 263, 300, 296, 299, 309, 284, 254, 243, 236};
		return (classID >= 1 && classID <= 12) ? cellIDs[classID] : 314;
	}
}

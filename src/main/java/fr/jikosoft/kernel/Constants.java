package fr.jikosoft.kernel;

public class Constants {
	public static final	String REQUIRED_CLIENT_VERSION = "1.29.0";

	public static final int CLASS_FECA = 1;
	public static final int CLASS_OSAMODAS = 2;
	public static final int CLASS_ENUTROF = 3;
	public static final int CLASS_SRAM = 4;
	public static final int CLASS_XELOR = 5;
	public static final int CLASS_ECAFLIP = 6;
	public static final int CLASS_ENIRIPSA = 7;
	public static final int CLASS_IOP = 8;
	public static final int CLASS_CRA = 9;
	public static final int CLASS_SADIDA = 10;
	public static final int CLASS_SACRIEUR = 11;
	public static final int CLASS_PANDAWA = 12;

	private static final int[][] START_SPELLS = {
		{},
		{3, 6, 17},
		{34, 21, 23},
		{51, 43, 41},
		{61, 72, 65},
		{82, 81, 83},
		{102, 103, 105},
		{125, 128, 121},
		{143, 141, 142},
		{161, 169, 164},
		{183, 200, 193},
		{432, 431, 434},
		{686, 692, 687}
	};

	private static final String[] START_SPELL_PLACES = {"1", "2", "3"};

	public static int getStartMapID(int classID) {
		int[] mapIDs = {10298, 10300, 10284, 10299, 10285, 10298, 10276, 10283, 10294, 10292, 10279, 10296, 10289};
		return (classID >= 1 && classID <= 12) ? mapIDs[classID] : 10298;
	}

	public static short getStartCellID(int classID) {
		short[] cellIDs = {314, 323, 372, 271, 263, 300, 296, 299, 309, 284, 254, 243, 236};
		return (classID >= 1 && classID <= 12) ? cellIDs[classID] : 314;
	}

	public static int[] getStartSpells(int classID) {
		return (classID >= 1 && classID < START_SPELLS.length) ? START_SPELLS[classID].clone() : new int[0];
	}

	public static String getStartSpellPlace(int spellIndex) {
		return (spellIndex >= 0 && spellIndex < START_SPELL_PLACES.length) ? START_SPELL_PLACES[spellIndex] : "";
	}
}

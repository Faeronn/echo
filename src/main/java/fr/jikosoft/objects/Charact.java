package fr.jikosoft.objects;

import java.util.Map;
import java.util.TreeMap;

import fr.jikosoft.kernel.SQLManager;
import fr.jikosoft.kernel.World;
import fr.jikosoft.objects.Maps.Cell;

public class Charact {
	private int _GUID;
	private String _name;
	private int _sex;
	private int _class;
	private int _level;
	private int _color1;
	private int _color2;
	private int _color3;
	private Account _account;
	private Maps _currentMap;
	private Cell _currentCell;
	private Map<Integer, Stat> _stats = new TreeMap<Integer, Stat>();
	
	public Charact(int guid, String name, int cClass, int sex, int level, int color1, int color2, int color3, int accountID,
				   short currentMapID, short currentCellID) {
		this._GUID = guid;
		this._name = name;
		this._class = cClass;
		this._sex = sex;
		this._level = level;
		this._color1 = color1;
		this._color2 = color2;
		this._color3 = color3;
		this._account = World.getAccount(accountID);
		this._currentMap = World.getMap(currentMapID);
		this._currentCell = _currentMap.get_cell(currentCellID);
	}
	
	public int get_GUID() { 
		return _GUID;
	}
	
	public String get_name() {
		return _name;
	}
	
	public int get_sex() {
		return _sex;
	}
	
	public int get_class() {
		return _class;
	}
	
	public int get_level() {
		return _level;
	}
	
	public int get_color1() {
		return _color1;
	}
	
	public int get_color2() {
		return _color2;
	}
	
	public int get_color3() {
		return _color3;
	}
	
	public Account get_account() {
		return _account;
	}
	
	public int get_currentMapID() {
		return _currentMap.get_ID();
	}
	
	public Maps get_currentMap() {
		return _currentMap;
	}
	
	public short get_currentCellID() {
		return _currentCell.get_ID();
	}
	
	public Cell get_currentCell() {
		return _currentCell;
	}
	
	public String getCharacterData() {
		String data = "|";
		String patern =  "ID" + ";" + "Name" + ";" + "level" + ";" + "gfxID" + ";" + "color1" + ";" + "color2" + ";" + 
				"color3" + ";"  + "Accessories : 1, 2, 3, 4" + ";" + "isMerchant [0,1]" + ";" + "serverID" + ";" + "isDead [0,1]" + ";" + 
				"deathCount" + "lvlMax : 200" + ";";
		String color1 = (_color1 != -1) ? Integer.toHexString(_color1): "-1";
		String color2 = (_color2 != -1) ? Integer.toHexString(_color2): "-1";
		String color3 = (_color3 != -1) ? Integer.toHexString(_color3): "-1";
		
		data += _GUID + ";" + _name + ";" + _level + ";" + _class + _sex + ";" + color1 + ";" + color2 + ";" + color3 + ";" + 
				"" + ";" + "0" + ";" + "5" + ";" + "0" + ";" +  "" + "200" + ";";
		
		return data;
	}
	
	public String getCharacterStats() {
		String data = "";
		String patern =  "xp" + "," + "minXp" + "," + "maxXp" + "|" + "kamas" + "|" + "capital" + "|" + "spellPoints" + "|" + 
				"align" + "~"  + "align" + "," + "alignLevel" + "," + "grade" + "," + "honor" + "," + "dishonor" + "," + "showWings" + "|" +
				"hp" + "," + "maxHp" + "|" + "energy" + "," + "maxEnergy" + "|" + "initiative"+ "|" + "prospecting" + "|" +
				"baseAP" + "," + "bonusItemAP"  + "," + "donAP" + "," + "buffAP" + "," + "totalAP" + "|" +
				"baseMP" + "," + "bonusItemMP"  + "," + "donMP" + "," + "buffMP" + "," + "totalMP" + "|";
		
		data+= "0,0,100|" + "1000|" + "0|" + "0|" + "0~0,0,0,0,0,0|" + "55,55|" + "10000,10000|" + "0|" + "100|" + "6,0,0,0,6|" + "3,0,0,0,3|"; 
		return data;
	}
	
	
	public String getCharacterGMData() {
		String patern = "currentCellID" + ";" + "orientation" + ";" + "0(???)" + ";" + "GUID" + ";" + "name" + ";"  + "class"  + ";" +
						"gfxID" + "^" + "size" + ";" + "sex" + "hasTitle ? (',' + title) : ';'" + "alignement, ???, _showWings ? getGrade() : '0', level + GUID" + ";" +
						"color1" + ";" + "color2" + ";" + "color3" + ";" + "weaponID, hatID, cloackID, petID, shieldID" + ";" + "auraLevel" + ";" +
						";" + ";" + "";
		
		
		
		StringBuilder str = new StringBuilder();
		str.append(_currentCell.get_ID() + ";");
		str.append("1;");      				     						//FIXME: ORIENTATION
		str.append("0;");												//FIXME:?
		str.append(_GUID + ";");
		str.append(_name + ";");
		str.append(_class + ";");   					           //FIXME: (_character.get_title() > 0 ? ("," + this.get_title() + ";") : ";")
		str.append(_class + "" + _sex + "^" + "100;");            //FIXME: _character.get_gfxID() + "^" + _character.get_size() + ";"
		str.append(_sex + ";");                                   
		str.append("0,0,0,");										   //FIXME: //alignement, ???, _showWings ? getGrade() : "0", level+guid
		str.append(_level + _GUID).append(";");
		str.append((_color1 == -1 ? "-1" : Integer.toHexString(_color1))).append(";");
        str.append((_color2 == -1 ? "-1" : Integer.toHexString(_color2))).append(";");
        str.append((_color3 == -1 ? "-1" : Integer.toHexString(_color3))).append(";");
		str.append(",,,,").append(";");                              //getGMStuffString() 
		str.append((_level > 99 ? (_level > 199 ? (2) : (1)) :(0))).append(";");
		str.append("1;");//Emote
		str.append(";");//Emote timer
		str.append(";;0;;");
		/*if (this._guildMember != null && this._guildMember.getGuild().haveTenMembers())
            str.append(this._guildMember.getGuild().getName()).append(";").append(this._guildMember.getGuild().getEmblem()).append(";");
        else
            str.append(";;");
        if (this.dead == 1 && !this.isGhost)
            str.append("-1");
        str.append(getSpeed()).append(";");//Restriction
        str.append((_onMount && _mount != null ? _mount.getStringColor(parsecolortomount()) : "")).append(";");
        str.append(this.isDead()).append(";");*/
	
		return str.toString();
	}
	
	
	/*
	public String parseToGM() {
        StringBuilder str = new StringBuilder();
        
        str.append(curCell.getId()).append(";").append(_orientation).append(";");
        str.append("0").append(";");//FIXME:?
        str.append(this.getId()).append(";").append(this.getName()).append(";").append(this.getClasse());
        str.append((this.get_title() > 0 ? ("," + this.get_title() + ";") : (";")));
        int gfx = gfxId;
        if (this.getObjetByPos(Constant.ITEM_POS_ROLEPLAY_BUFF) != null)
            if (this.getObjetByPos(Constant.ITEM_POS_ROLEPLAY_BUFF).getTemplate().getId() == 10681)
                    gfx = 8037;
            str.append(gfx).append("^").append(_size);//gfxID^size
            if (this.getObjetByPos(Constant.ITEM_POS_PNJ_SUIVEUR) != null)
                str.append(",").append(Constant.getItemIdByMascotteId(this.getObjetByPos(Constant.ITEM_POS_PNJ_SUIVEUR).getTemplate().getId())).append("^100");
            str.append(";").append(this.getSexe()).append(";");
            str.append(_align).append(",");
            str.append("0").append(",");//FIXME:?
            str.append((_showWings ? getGrade() : "0")).append(",");
            str.append(this.getLevel() + this.getId());
            if (_showWings && _deshonor > 0) {
                str.append(",");
                str.append(_deshonor > 0 ? 1 : 0).append(';');
            } else {
                str.append(";");
            }
            int color1 = this.getColor1(), color2 = this.getColor2(), color3 = this.getColor3();
            if (this.getObjetByPos(Constant.ITEM_POS_MALEDICTION) != null)
                if (this.getObjetByPos(Constant.ITEM_POS_MALEDICTION).getTemplate().getId() == 10838) {
                    color1 = 16342021;
                    color2 = 16342021;
                    color3 = 16342021;
                }

            str.append((color1 == -1 ? "-1" : Integer.toHexString(color1))).append(";");
            str.append((color2 == -1 ? "-1" : Integer.toHexString(color2))).append(";");
            str.append((color3 == -1 ? "-1" : Integer.toHexString(color3))).append(";");
            str.append(getGMStuffString()).append(";");
            if (hasEquiped(10054) || hasEquiped(10055) || hasEquiped(10056)
                    || hasEquiped(10058) || hasEquiped(10061)
                    || hasEquiped(10102)) {
                str.append(3).append(";");
                set_title(2);
            } else {
                if (get_title() == 2)
                    set_title(0);
                Group g = this.getGroupe();
                int level = this.getLevel();
                if (g != null)
                    if (!g.isPlayer() || this.get_size() <= 0) // Si c'est un groupe non joueur ou que l'on est invisible on cache l'aura
                        level = 1;
                str.append((level > 99 ? (level > 199 ? (2) : (1)) : (0))).append(";");
            }
            str.append(";");//Emote
            str.append(";");//Emote timer
            if (this._guildMember != null
                    && this._guildMember.getGuild().haveTenMembers())
                str.append(this._guildMember.getGuild().getName()).append(";").append(this._guildMember.getGuild().getEmblem()).append(";");
            else
                str.append(";;");
            if (this.dead == 1 && !this.isGhost)
                str.append("-1");
            str.append(getSpeed()).append(";");//Restriction
            str.append((_onMount && _mount != null ? _mount.getStringColor(parsecolortomount()) : "")).append(";");
            str.append(this.isDead()).append(";");
        
        return str.toString();
    }
	
	public String getCharacterGear() {
        StringBuilder str = new StringBuilder();

        GameObject object = getObjetByPos(Constant.ITEM_POS_ARME);

        if (object != null)
            str.append(Integer.toHexString(object.getTemplate().getId()));

        str.append(",");

        object = getObjetByPos(Constant.ITEM_POS_COIFFE);

        if (object != null) {
            object.parseStatsString();

            Integer obvi = object.getStats().getEffects().get(970);
            if (obvi == null) {
                str.append(Integer.toHexString(object.getTemplate().getId()));
            } else {
                str.append(Integer.toHexString(obvi)).append("~16~").append(object.getObvijevanLook());
            }
        }

        str.append(",");

        object = getObjetByPos(Constant.ITEM_POS_CAPE);

        if (object != null) {
            object.parseStatsString();

            Integer obvi = object.getStats().getEffects().get(970);
            if (obvi == null) {
                str.append(Integer.toHexString(object.getTemplate().getId()));
            } else {
                str.append(Integer.toHexString(obvi)).append("~17~").append(object.getObvijevanLook());
            }
        }

        str.append(",");

        object = getObjetByPos(Constant.ITEM_POS_FAMILIER);

        if (object != null)
            str.append(Integer.toHexString(object.getTemplate().getId()));

        str.append(",");

        object = getObjetByPos(Constant.ITEM_POS_BOUCLIER);

        if (object != null)
            str.append(Integer.toHexString(object.getTemplate().getId()));

        return str.toString();
    }*/
	
	public class Stat {
		private int _id;
		private int _amount;
		
		public Stat(int id, int amount) {
			_id = id;
			_amount = amount;
		}
		
		public int get_id() {
			return _id;
		}
		
		public int get_amount() {
			return _amount;
		}
	}
}

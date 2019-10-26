package de.netbeacon.xeniadiscord.util.extperm.permission;

public class ExtPerm {

    public static ExtPerm none = new ExtPerm(-1);
    // guild_gob
    public static ExtPerm admin = new ExtPerm(0);                        // all permissions
    public static ExtPerm permission_manage = new ExtPerm(1);            // can manage permissions
    // music
    public static ExtPerm music_all = new ExtPerm(10);                    // all music permissions
    public static ExtPerm music_play = new ExtPerm(11);                   // play, list
    public static ExtPerm music_stop = new ExtPerm(12);                   // stop
    public static ExtPerm music_manage_queue = new ExtPerm(13);           // skip, shuffle etc
    public static ExtPerm music_manage_off = new ExtPerm(14);             // off
    // member
    public static ExtPerm membermanagement_all = new ExtPerm(20);             // all membermanagement permission
    public static ExtPerm membermanagement_kick = new ExtPerm(21);        // kick member
    public static ExtPerm membermanagement_ban = new ExtPerm(22);         // ban member
    // ghost
    public static ExtPerm ghost = new ExtPerm(30);                        // spooky
    // blacklist
    public static ExtPerm blacklist_manage = new ExtPerm(40);             // manage blacklist
    // twitchhook
    public static ExtPerm twitchhooks_manage = new ExtPerm(50);           // manage twitchhooks



    private int value;
    private ExtPerm(int value){
        this.value = value;
    }

    public int getValue(){ return value; }
}

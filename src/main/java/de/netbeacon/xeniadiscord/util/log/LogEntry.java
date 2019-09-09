package de.netbeacon.xeniadiscord.util.log;

import java.util.Date;

public class LogEntry {

    private static int entrys = 0;
    private int index;
    private String name;
    private String description;
    private int errorlevel;
    private Date date;

    LogEntry(){} // not useless

    LogEntry(String name, String description, int errorlevel){
        entrys++;
        this.index = entrys;
        this.name = name;
        this.description = description;
        this.errorlevel = errorlevel;
        this.date = new Date();
    }

    /*          GET         */

    int getIndex(){ return this.index; }
    public String getName(){ return this.name; }
    public String getDescription(){ return this.description; }
    public int getErrorlevel(){ return this.errorlevel; }
    public Date getDate(){ return this.date; }

    /*          SET         */

    void resetentrycounter(){ entrys = 0; }

}

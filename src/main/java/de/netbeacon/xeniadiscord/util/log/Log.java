package de.netbeacon.xeniadiscord.util.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Log {

    private static List<LogEntry> entries;

    public Log(){
        if(entries == null){
            entries = new ArrayList<LogEntry>();
        }
    }

    public void addEntry(String name, String description, int errorlevel){
        entries.add(new LogEntry(name, description, errorlevel));
    }

    public List<LogEntry> getEntrys(int errorlevel){
        // get all entrys with an errorlevel equal and above the defined value
        List<LogEntry> selected = new ArrayList<LogEntry>();
        for(LogEntry logEntry : entries){
            if(logEntry.getErrorlevel() >= errorlevel){
                selected.add(logEntry);
            }
        }
        return selected;
    }

    public boolean export(){
        try{
            if(!entries.isEmpty()){
                long time = (System.currentTimeMillis() / 1000L);
                File dir = new File("./logs/");
                if(!dir.exists()){
                    dir.mkdir();
                }
                String filename = "./logs/"+time+".log";
                File log = new File(filename);
                if(log.exists()){ //not rly needed anymore
                    log.delete();
                }
                log.createNewFile();
                // write to file
                BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
                for(LogEntry entry : entries){
                    // index, date, lvl, name, description
                    String line = entry.getIndex()+"|"+entry.getDate()+"|"+errorleveltostring(entry.getErrorlevel())+"|"+entry.getName()+"|"+entry.getDescription();
                    writer.write(line);
                    writer.newLine();
                }
                writer.flush();
                writer.close();
                // clear log
                resetLog();
            }
            return true;
        }catch (Exception e){
            // welp plz, lots broken :c
            e.printStackTrace();
            return false;
        }
    }

    public int count(int errorlevel){
        int counter = 0;
        for(LogEntry logEntry : entries){
            if(logEntry.getErrorlevel() >= errorlevel){
                counter++;
            }
        }
        return counter;
    }

    public void resetLog(){
        entries.clear(); // reset list
        new LogEntry().resetentrycounter(); // reset counter
    }
    private String errorleveltostring(int errorlevel){
        switch(errorlevel){
            case 0:
                return "Info";
            case 1:
                return "Minor";
            case 2:
                return "Moderate";
            case 3:
                return "Major";
            case 4:
                return "Critical";
            case 5:
                return "Catastrophic";
            default:
                return "Unknown";
        }
    }
}

package de.netbeacon.xeniadiscord.util;

import de.netbeacon.xeniadiscord.util.log.Log;

import java.io.*;
import java.util.ArrayList;

public class BlackListUtility {

    private static ArrayList<String> blacklist;

    public BlackListUtility(){
        // init blacklist if it hasn't happened before
        if(blacklist == null){
            System.out.println("[INFO] Init blacklist");
            new Log().addEntry("Blacklist", "Init blacklist", 0);
            initblacklist();
            System.out.println(">> "+blacklist.size()+" entrys found.");
        }
    }

    private void initblacklist(){
        try{
            File path = new File("./data/storage/");
            if(!path.exists()){
                path.mkdirs();
            }
            // check if blackist.txt exists
            File blacklistfile = new File("./data/storage/blacklist.storage");
            if (!blacklistfile.exists()) {
                //Create the file
                blacklistfile.createNewFile();
            }
            // init array list
            blacklist = new ArrayList<String>();
            // read file & add to list
            BufferedReader br = new BufferedReader(new FileReader(blacklistfile));
            String line;
            while((line = br.readLine()) != null){
                blacklist.add(line);
            }
            br.close();
        }catch (Exception e){
            new Log().addEntry("Blacklist", "Could not read blacklist from file: "+e.toString(), 5);
            e.printStackTrace();
        }
    }

    public boolean isincluded(String channel){
        // return if channel is included in blacklist
        return blacklist.contains(channel);
    }

    public boolean add(String channel){
        // add channel to blacklist
        if(!blacklist.contains(channel)){
            blacklist.add(channel);
            // return true if add was successfull
            return true;
        }
        // return false if channel was already included
        return false;
    }

    public boolean remove(String channel){
        // remove channel to blacklist
        if(!blacklist.contains(channel)){
            blacklist.remove(channel);
            // return true if remove was successfull
            return true;
        }
        // return false if channel isnt included
        return false;
    }

    public boolean writetofile(){
        // this should be called every couple minutes to write modifications to the file (reduces file modifications)
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("./data/storage/blacklist.storage"));
            for(String line : blacklist){
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
            new Log().addEntry("Blacklist", "Could not write blacklist to file: "+e.toString(), 5);
            return false;
        }
        return true;
    }

    public int count(){
        return blacklist.size();
    }
}

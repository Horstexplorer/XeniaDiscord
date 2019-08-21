package de.netbeacon.xeniadiscord.util;

import java.io.*;
import java.util.ArrayList;

public class BlackListUtility {

    private static ArrayList<String> blacklist;

    public BlackListUtility(){
        // init blacklist if it hasn't happened before
        if(blacklist == null){
            System.out.println("[INFO] Init blacklist");
            initblacklist();
            System.out.println(">> "+blacklist.size()+" entrys found");
        }
    }

    private void initblacklist(){
        try{
            // check if blackist.txt exists
            File blacklistfile = new File("blacklist.storage");
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
            new ErrorLog(2, "Could not read blacklist from file: "+e);
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
            BufferedWriter writer = new BufferedWriter(new FileWriter("blacklist.storage"));
            for(String line : blacklist){
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
            new ErrorLog(3, "Could not write blacklist to file: "+e);
            return false;
        }
        return true;
    }

    public int count(){
        return blacklist.size();
    }
}

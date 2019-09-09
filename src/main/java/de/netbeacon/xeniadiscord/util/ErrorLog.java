package de.netbeacon.xeniadiscord.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ErrorLog {

    private static List<String> errors;

    public ErrorLog(int lvl, String cause){
        if(errors == null){
            errors = new ArrayList<String>();
        }
        if(!cause.isEmpty()){   // we may call it just to get the object so we dont need to add it as error
            // set error level
            String level = "";
            switch(lvl){
                case 1:
                    level = "Minor";
                    break;
                case 2:
                    level = "Moderate";
                    break;
                case 3:
                    level = "Major";
                    break;
                case 4:
                    level = "Critical";
                    break;
                default:
                    level = "Unknown";
                    break;
            }
            // add to list
            String error = new Date()+" |#| "+level+" |#| "+cause;
            errors.add(error);
        }
    }

    public List<String> getErrors(){
        return errors;
    }

    public boolean export(){
        try{
            if(!errors.isEmpty()){
                long time = (System.currentTimeMillis() / 1000L);
                File dir = new File("./logs/");
                if(!dir.exists()){
                    dir.mkdir();
                }
                File errorlog = new File("./logs/"+time+"_error.log");
                if(errorlog.exists()){ //not rly needed anymore
                    errorlog.delete();
                }
                errorlog.createNewFile();
                // write to file
                BufferedWriter writer = new BufferedWriter(new FileWriter("./logs/"+time+"_error.log"));
                for(String line : errors){
                    writer.write(line);
                    writer.newLine();
                }
                writer.flush();
                writer.close();
                // clear log
                reset();
            }
            return true;
        }catch (Exception e){
            // welp plz, lots broken :c
            e.printStackTrace();
            return false;
        }
    }
    public void reset() { errors.clear(); }
    public int count(){
        return errors.size();
    }
}

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
        // set error level
        String level = "";
        switch(lvl){
            case 0:
                level = "Minor";
                break;
            case 1:
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

    public List<String> getErrors(){
        return errors;
    }

    public boolean export(){
        try{
            File errorlog = new File("error.log");
            if(errorlog.exists()){
                errorlog.delete();
            }
            errorlog.createNewFile();
            // write to file
            BufferedWriter writer = new BufferedWriter(new FileWriter("error.log"));
            for(String line : errors){
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
            writer.close();
            return true;
        }catch (Exception e){
            // welp plz lot broken :c
            e.printStackTrace();
            return false;
        }
    }

    public int count(){
        return errors.size();
    }
}

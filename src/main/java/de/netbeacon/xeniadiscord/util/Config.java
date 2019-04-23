package de.netbeacon.xeniadiscord.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    public Config(){
        //Check if config file exist
        File configfile = new File("sys.config");
        if (!configfile.exists()) {
            //Create the file
            createconfigfile();
        }
    }

    private void createconfigfile(){
        Properties prop = new Properties();

        try {
            prop.setProperty("activated", "false");

            prop.setProperty("bot_token", "");
            prop.setProperty("bot_command_indicator", "x!");
            prop.setProperty("bot_activate_modules", "false");
            prop.setProperty("bot_activate_coremodule", "false");
            prop.setProperty("bot_activate_coremodule_backgroundtask", "false");
            prop.setProperty("bot_status", "with humans");
            prop.setProperty("bot_admin_id", "");


            prop.store(new FileOutputStream("sys.config"), null);
        }catch(Exception e) {
            e.printStackTrace();
            System.exit(-1); //should quit; something is definitely wrong here
        }
    }

    public String load(String  property) {
        //get value from property
        Properties prop = new Properties();
        InputStream input;
        String result = "";
        try {
            input = new FileInputStream("sys.config");
            prop.load(input);
            result = prop.getProperty(property);
            input.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String version() {
        String vers= "1.0.1.0";
        String build = "2304192317";
        return vers+"-"+build;
    }
}
package de.netbeacon.xeniadiscord.util;

import de.netbeacon.xeniadiscord.util.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Config {

    private static Properties properties;

    public Config(){
        // init blacklist if it hasn't happened before
        if(properties == null){
            System.out.println("[INFO] Init config");
            new Log().addEntry("Config", "Init config", 0);
            if(!initproperties()){
                System.out.println("[ERROR] Init config failed");
            }
            // try updating config
            updateconfig();
        }
    }

    private boolean initproperties(){
        //Check if config file exist
        File configfile = new File("sys.config");
        if (!configfile.exists()) {
            //Create the file
            createconfigfile();
        }
        // load properties
        properties = new Properties();
        InputStream input;
        try {
            input = new FileInputStream("sys.config");
            properties.load(input);
            input.close();
        }catch (Exception e){
            e.printStackTrace();
            new Log().addEntry("Config", "Could not init properties from sys.config "+e.toString(), 5);
            return false;
        }
        return true;
    }

    private void createconfigfile(){
        properties = new Properties();

        properties.setProperty("activated", "false");
        properties.setProperty("bot_token", "");
        properties.setProperty("bot_command_indicator", "x!");
        properties.setProperty("bot_activate_modules", "false");
        properties.setProperty("bot_activate_coremodule", "false");
        properties.setProperty("bot_status", "with humans");
        properties.setProperty("bot_admin_id", "");
        properties.setProperty("bot_sayhellotonew", "true");
        properties.setProperty("bot_gui_activate", "false");
        properties.setProperty("bot_gui_exitonclose", "true");

        writetofile();
    }

    private void updateconfig(){
        // check if our config is updated to the latest version
        // ( all properties should be included, add them if not )

        // create list containing all properties
        HashMap<String, String> propcheck = new HashMap<String, String>();
        // add properies and values
        propcheck.put("activated","false");
        propcheck.put("bot_token","");
        propcheck.put("bot_command_indicator","x!");
        propcheck.put("bot_activate_modules","false");
        propcheck.put("bot_activate_coremodule","false");
        propcheck.put("bot_status","with humans");
        propcheck.put("bot_admin_id","");
        propcheck.put("bot_sayhellotonew","true");
        propcheck.put("bot_gui_activate", "false");
        propcheck.put("bot_gui_exitonclose", "true");
        // check if the properties from the list are in our config.
        for(Map.Entry<String, String> entry : propcheck.entrySet()){
            if(properties.getProperty(entry.getKey()) == null){
                // add property to properties
                properties.setProperty(entry.getKey(), entry.getValue());
            }
        }
        // write to file
        writetofile();
    }

    public boolean updateproperties(String property, String value){
        // Update property
        // check if property exists
        if(properties.getProperty(property) != null){
            // property exists
            properties.setProperty(property, value);
            // write to file
            writetofile();

            new Log().addEntry("Config", "Updated property "+property, 0);
            return true;
        }

        new Log().addEntry("Config", "Could not update property "+property, 2);
        return false;
    }

    private void writetofile(){
        try{
            properties.store(new FileOutputStream("sys.config"), null);
        }catch (Exception e){
            e.printStackTrace();
            new Log().addEntry("Config", "Could not write properties to sys.config "+e.toString(), 5);
            System.exit(-1); //should quit; something is definitely wrong here
        }
    }

    public String load(String  property) {
        //get value from property
        String result = "";
        try {
            result = properties.getProperty(property);
        }catch(Exception e) {
            e.printStackTrace();
            new Log().addEntry("Config", "Could not load property "+property, 2);
        }
        return result;
    }

    public String version() {
        String vers= "1.1.5.1";
        String build = "1910231006";
        return vers+"-"+build;
    }
}
package de.netbeacon.xeniadiscord.util.twitchwrap.config;

import de.netbeacon.xeniadiscord.util.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TwitchConfig {

    private static Properties properties;

    public TwitchConfig(){
        if(properties == null){
            System.out.println("[INFO] Init TwitchConfig");
            new Log().addEntry("TC", "Init TwitchConfig", 0);
            if(!initproperties()){
                System.out.println("[ERROR] Init TwitchConfig failed.");
                System.exit(0); // exit will prevent undefined errors
            }
            updateconfig(); // try updating the config
        }
    }


    private boolean initproperties(){
        //Check if config file exist
        File configfile = new File("twitch.config");
        if (!configfile.exists()) {
            //Create the file
            createconfigfile();
        }
        // load properties
        properties = new Properties();
        InputStream input;
        try {
            input = new FileInputStream("twitch.config");
            properties.load(input);
            input.close();
        }catch (Exception e){
            new Log().addEntry("TC", "Could not load twitch config: "+e.toString(), 5);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void createconfigfile(){
        properties = new Properties();
        properties.setProperty("twitch_client_id", "");
        properties.setProperty("twitch_client_secret", "");
        properties.setProperty("twitch_bearer_token", "");
        properties.setProperty("twitch_bearer_token_validuntil", "");
        properties.setProperty("twitch_worker_max", "12");
        writetofile();
    }

    private void updateconfig(){
        // check if our config is updated to the latest version
        // ( all properties should be included, add them if not )
        // create list containing all properties
        HashMap<String, String> propcheck = new HashMap<String, String>();
        // add properies and values
        propcheck.put("twitch_client_id","");
        propcheck.put("twitch_client_secret","");
        propcheck.put("twitch_bearer_token","");
        propcheck.put("twitch_bearer_token_validuntil","");
        propcheck.put("twitch_worker_max","12");
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

    private void writetofile(){
        try{
            properties.store(new FileOutputStream("twitch.config"), null);
        }catch (Exception e){
            new Log().addEntry("TC", "Could not write twitch config to file: "+e.toString(), 5);
            e.printStackTrace();
        }
    }

    public boolean update(String property, String value){
        // Update property
        // check if property exists
        if(properties.getProperty(property) != null){
            // property exists
            properties.setProperty(property, value);
            // write to file
            writetofile();

            new Log().addEntry("TC", "Updated property "+property, 0);
            return true;
        }

        new Log().addEntry("TC", "Could not update property "+property, 2);
        return false;
    }

    public String get(String property) {
        //get value from property
        String result = "";
        try {
            result = properties.getProperty(property);
        }catch(Exception e) {
            result = "";
            new Log().addEntry("TC", "Could not load property "+property+": "+e.toString(), 5);
            e.printStackTrace();
        }
        return result;
    }
}

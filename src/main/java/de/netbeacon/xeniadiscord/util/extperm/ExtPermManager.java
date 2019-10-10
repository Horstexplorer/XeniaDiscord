package de.netbeacon.xeniadiscord.util.extperm;

import de.netbeacon.xeniadiscord.util.extperm.permission.ExtPerm;
import de.netbeacon.xeniadiscord.util.log.Log;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExtPermManager {

    private static List<ExtPermStorage> data;

    public ExtPermManager(){

    }

    public boolean addPermission(Role role, ExtPerm permission){
        // add permission
        for(ExtPermStorage eps : data){
            if(eps.getRoleid().equals(role.getId())){
                return eps.addPermission(permission);
            }
        }
        // create new
        ExtPermStorage eps = new ExtPermStorage(role);
        eps.addPermission(permission);
        data.add(eps);
        return true;
    }

    public boolean removePermission(Role role, ExtPerm permission){
        for(ExtPermStorage eps : data){
            if(eps.getRoleid().equals(role.getId())){
                return eps.removePermission(permission);
            }
        }
        return false;
    }

    public boolean hasPermission(Member member, String permission){
        List<Role> roles = member.getRoles();
        if(getPermission(permission) != ExtPerm.none){
            for(ExtPermStorage eps : data){
                for(Role r : roles){
                    if(eps.getRoleid().equals(r.getId())){
                        if(eps.haspermission(getPermission(permission))){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    public boolean hasPermission(Member member, ExtPerm permission){
        List<Role> roles = member.getRoles();
        for(ExtPermStorage eps : data){
            for(Role r : roles){
                if(eps.getRoleid().equals(r.getId())){
                    if(eps.haspermission(permission)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private ExtPerm getPermission(int value){
        switch (value) {
            case 0:
                return ExtPerm.admin;
            case 1:
                return ExtPerm.permission_manage;
            case 10:
                return ExtPerm.music_all;
            case 11:
                return ExtPerm.music_play;
            case 12:
                return ExtPerm.music_stop;
            case 13:
                return ExtPerm.music_manage_queue;
            case 14:
                return ExtPerm.music_manage_off;
            case 20:
                return ExtPerm.membermanagement_all;
            case 21:
                return ExtPerm.membermanagement_kick;
            case 22:
                return ExtPerm.membermanagement_ban;
            case 30:
                return ExtPerm.ghost;
            case 40:
                return ExtPerm.blacklist_manage;
            case 50:
                return ExtPerm.twitchhooks_manage;
            default:
                return ExtPerm.none;
        }
    }
    public ExtPerm getPermission(String permission){
        switch (permission){
            case "admin":
                return ExtPerm.admin;
            case "permission_manage":
                return ExtPerm.permission_manage;
            case "music_all":
                return ExtPerm.music_all;
            case "music_play":
                return ExtPerm.music_play;
            case "music_stop":
                return ExtPerm.music_stop;
            case "music_manage_queue":
                return ExtPerm.music_manage_queue;
            case "music_manage_off":
                return ExtPerm.music_manage_off;
            case "membermanagement_all":
                return ExtPerm.membermanagement_all;
            case "membermanagement_kick":
                return ExtPerm.membermanagement_kick;
            case "membermanagement_ban":
                return ExtPerm.membermanagement_ban;
            case "ghost":
                return ExtPerm.ghost;
            case "blacklist_manage":
                return ExtPerm.blacklist_manage;
            case "twitchhooks_manage":
                return ExtPerm.twitchhooks_manage;
            default:
                return ExtPerm.none;
        }
    }

    private void loadfromfile(){
        try{
            // check if file exists
            File permfile = new File("extperm.storage");
            if (!permfile.exists()) {
                //Create the file
                permfile.createNewFile();
            }
            // init list
            data = new ArrayList<ExtPermStorage>();
            // read file
            BufferedReader br = new BufferedReader(new FileReader(permfile));
            String line;
            while((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    JSONObject jsonObject = new JSONObject(line);
                    String roleid = jsonObject.getString("roleid");
                    ExtPermStorage eps = new ExtPermStorage(roleid);
                    for(int i = 0; i < jsonObject.getJSONArray("permissions").length(); i++){
                        ExtPerm ep = getPermission(jsonObject.getJSONArray("permissions").getInt(i));
                        if(ep != ExtPerm.none){
                            eps.addPermission(ep);
                        }
                    }
                    data.add(eps);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            new Log().addEntry("ExtPermManager", "Could not write data to file: "+e, 4);
        }
    }
    public void writetofile(){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("extperm.storage"));
            for(ExtPermStorage eps : data){
                writer.write(eps.toString());
                writer.newLine();
            }
            writer.flush();
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
            new Log().addEntry("ExtPermManager", "Could not write data to file: "+e, 4);
        }
    }
}

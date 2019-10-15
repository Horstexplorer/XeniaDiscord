package de.netbeacon.xeniadiscord.util.extperm;

import de.netbeacon.xeniadiscord.util.extperm.permission.ExtPerm;
import de.netbeacon.xeniadiscord.util.log.Log;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ExtPermManager {

    private static JDA jda;
    private static AtomicReference<HashMap<String, List<ExtPerm>>> data = new AtomicReference<>();

    public ExtPermManager(){}
    public ExtPermManager(JDA jda_){
        if(jda == null){
            jda = jda_;
        }
        if(data.get() == null){
            System.out.println("[INFO] Init ExtPermManager");
            loadfromfile();
            System.out.println(">> "+data.get().size()+" entrys found.");
        }
    }

    public boolean addPermission(Role role, ExtPerm[] permission){
        // add permission
        if(data.get().containsKey(role.getId())){
            for(ExtPerm extPerm : permission){
                if(extPerm != ExtPerm.none && !data.get().get(role.getId()).contains(permission)){
                    data.get().get(role.getId()).add(extPerm);
                }
            }
            return true;
        }
        // create new entry
        List<ExtPerm> perm = new ArrayList<ExtPerm>(); // create new list
        for(ExtPerm extPerm : permission){
            perm.add(extPerm);
        }
        data.get().put(role.getId(), perm);
        return true;
    }
    public boolean removePermission(Role role, ExtPerm[] permission){
        if(data.get().containsKey(role.getId())){
            for(ExtPerm extPerm : permission){
                data.get().get(role.getId()).remove(extPerm);
            }
            return true;
        }
        return false;
    }
    public boolean hasPermission(Member member, ExtPerm permission){
        List<Role> roles = member.getRoles();
        for(Role r : roles){
            if(data.get().containsKey(r.getId())){
                if(data.get().get(r.getId()).contains(permission)){
                    return true;
                }
            }
        }
        return false;
    }
    public boolean hasPermission(Member member, ExtPerm[] permission){
        List<Role> roles = member.getRoles();
        for(Role r : roles){
            if(data.get().containsKey(r.getId())){
                for(ExtPerm ep : permission){
                    if(data.get().get(r.getId()).contains(ep)){
                        return true;
                    }
                }
            }
        }
        return false;
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
    public String getPermission(ExtPerm permission){
        switch (permission.getValue()){
            case 0:
                return "admin";
            case 1:
                return "permission_manage";
            case 10:
                return "music_all";
            case 11:
                return "music_play";
            case 12:
                return "music_stop";
            case 13:
                return "music_manage_queue";
            case 14:
                return "music_manage_off";
            case 20:
                return "membermanagement_all";
            case 21:
                return "membermanagement_kick";
            case 22:
                return "membermanagement_ban";
            case 30:
                return "ghost";
            case 40:
                return "blacklist_manage";
            case 50:
                return "twitchhooks_manage";
            default:
                return "none";
        }
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
    private void selftest(){
        Set<String> set = new HashSet<>();
        for(Map.Entry<String, List<ExtPerm>> entry : data.get().entrySet()) {
            try{
                jda.getRoleById(entry.getKey()).getGuild();
            }catch (Exception e){
               set.add(entry.getKey());
            }
        }
        if(set.size() > 0){
            data.get().keySet().removeAll(set);
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
            data.set(new HashMap<String, List<ExtPerm>>());
            // read file
            BufferedReader br = new BufferedReader(new FileReader(permfile));
            String line;
            while((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    JSONObject jsonObject = new JSONObject(line);
                    String roleid = jsonObject.getString("roleid");
                    List<ExtPerm> eps = new ArrayList<ExtPerm>();
                    for(int i = 0; i < jsonObject.getJSONArray("permissions").length(); i++){
                        ExtPerm ep = getPermission(jsonObject.getJSONArray("permissions").getInt(i));
                        if(ep != ExtPerm.none){
                            eps.add(ep);
                        }
                    }
                    data.get().put(roleid, eps);
                }
            }
            selftest();
        }catch (Exception e){
            e.printStackTrace();
            new Log().addEntry("ExtPermManager", "Could not write data to file: "+e, 4);
        }
    }
    public void writetofile(){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("extperm.storage"));
            for(Map.Entry<String, List<ExtPerm>> entry : data.get().entrySet()) {
                String roleid = entry.getKey();
                String perm = "";
                for(int i = 0; i < entry.getValue().size(); i++){
                    perm += entry.getValue().get(i).getValue();
                    if(i < entry.getValue().size()-1){
                        perm += ", ";
                    }
                }
                writer.write("{\"roleid\":\""+roleid+"\",\"permissions\":["+perm+"]}");
            }
            writer.flush();
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
            new Log().addEntry("ExtPermManager", "Could not write data to file: "+e, 4);
        }
    }
    public String listPermission(Role role){
        String permissions = "";
        if(data.get().containsKey(role.getId())){
            for(int i = 0; i < data.get().get(role.getId()).size(); i++){
                permissions += getPermission(data.get().get(role.getId()).get(i));
                if(i < data.get().get(role.getId()).size()-1){
                    permissions += ", ";
                }
            }
        }
        if(permissions.isEmpty()){
            permissions = "none";
        }
        return permissions;
    }

}

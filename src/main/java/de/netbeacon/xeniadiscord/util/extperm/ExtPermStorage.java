package de.netbeacon.xeniadiscord.util.extperm;

import de.netbeacon.xeniadiscord.util.extperm.permission.ExtPerm;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;

public class ExtPermStorage {

    private String roleid;
    private List<ExtPerm> permissions = new ArrayList<ExtPerm>();

    public ExtPermStorage(Role role){
        this.roleid = role.getId();
    }

    public ExtPermStorage(String roleid){
        this.roleid = roleid;
    }

    public String getRoleid(){ return this.roleid; }

    public boolean addPermission(ExtPerm permission){
        if(!permissions.contains(permission)){
            permissions.add(permission);
            return true;
        }
        return false;
    }

    public boolean removePermission(ExtPerm permission){
        if(permissions.contains(permission)){
            permissions.remove(permission);
            return true;
        }
        return false;
    }

    public boolean haspermission(ExtPerm permission){ return permissions.contains(permission); }

    public String toString(){
        String perm = "";
        for(int i = 0; i < permissions.size(); i++){
            perm += permissions.get(i).getValue();
            if(i<permissions.size()-1){
                perm += ", ";
            }
        }
        return "{\"roleid\":\""+roleid+"\",\"permissions\":["+perm+"]}";
    }
}

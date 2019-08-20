package de.netbeacon.xeniadiscord.core;

import de.netbeacon.xeniadiscord.core.localcommands.Help;
import de.netbeacon.xeniadiscord.core.localcommands.LocalCommands;
import de.netbeacon.xeniadiscord.util.Config;
import net.dv8tion.jda.api.JDA;

import java.util.HashMap;
import java.util.Scanner;

public class LCL implements Runnable{

    private JDA jda;
    private HashMap<String, LocalCommands> commands = new HashMap<>();

    LCL(JDA jda){
        this.jda = jda;
        if(commands.isEmpty()){
            registercommands();
        }
    }

    private void registercommands(){
        // commands from localcommands
        commands.put("help", new Help());
    }

    private String[] getargs(String raw){
        // remove bot_command_indicator from string
        raw = raw.replace(new Config().load("bot_command_indicator"), "").trim();
        // split string to args
        return raw.split(" ");
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while(true){
            String command = scanner.nextLine();
            // parse arguments from command
            String[] args = getargs(command);
            // check if requested command exists
            if(commands.containsKey(args[0])){
                // execute command
                commands.get(args[0]).execute(jda, args);
            }else {
                System.out.println("> Unknown command: Use help for help.");
            }
        }
    }
}

package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.PlayerClient;
import mineserver.Server;

public class GamemodeCommand extends AbstractCommand {

    public GamemodeCommand(Server server) {
        super(server);
    }
    
    @Override
    public String getName() {
        return "Gamemode";
    }

    public String getHelp() {
        return "Change player's gamemode.";
    }
    
    public String getUsage() {
        return "[creative/survival] [playername]";
    }

    public List<String> getAliases() {
        return Arrays.asList(new String[] {"creative", "survival"});
    }
    
    @Override
    public void execute(Client client, String line) {
        String[] args = line.split("\\s+");
        boolean creative = args[0].equalsIgnoreCase("creative");
        if (args.length >= 2) {
            PlayerClient target = server.getTarget(client, args[1]);
            if (target != null) {
                server.runCommand("gamemode", new String[] {target.getName(), creative ? "1" : "0"});
                client.info("Set " + target.getName() + "'s mode to " + (creative ? "creative" : "survival") + ".");
                return;
            }
            return;
        }
        server.runCommand("gamemode", new String[] {client.getName(), creative ? "1" : "0"});
        client.info("Set your mode to " + (creative ? "creative" : "survival") + ".");
    }
    
}

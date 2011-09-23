package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.PlayerClient;
import mineserver.Server;

public class BanCommand extends AbstractCommand {
    
    public BanCommand(Server server) {
        super(server);
    }

    @Override
    public String getName() {
        return "Ban";
    }

    public String getHelp() {
        return "Ban a player.";
    }
    
    public String getUsage() {
        return "ban|pardon [playername] [reason]";
    }
    
    public List<String> getAliases() {
        return Arrays.asList(new String[] {"ban", "pardon"});
    }

    @Override
    public void execute(Client client, String line) {
        String[] args = line.split("\\s+");
        if (args[0].equalsIgnoreCase("ban")) {
            if (args.length == 2) {
                PlayerClient target = server.getTarget(client, args[1]);
                if (target != null) {
                    target.kick("");
                    server.runCommand("ban", new String[] {target.getName()});
                    client.info("You banned " + target.getName() + " from the server.");
                }
                return;
            }
            if (args.length >= 3) {
                PlayerClient target = server.getTarget(client, args[1]);
                if (target != null) {
                    String reason = extractReason(line);                
                    target.kick(reason);
                    server.runCommand("ban", new String[] {target.getName()});
                    client.info("You banned " + target.getName() + " from the server.");
                    return;
                }
                return;
            }
            client.usage(getUsage());
        }
        if (args[0].equalsIgnoreCase("pardon")) {
            if (args.length >= 2) {
                server.runCommand("pardon", new String[] {args[1]});
                client.info("You pardoned " + args[1] + ".");
                return;
            }
            client.usage(getUsage());
        }
    }
    
    private String extractReason(String line) {
        boolean space = false;
        int n = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) != ' ' && space) {
                n++;
                if (n == 2) {
                    return line.substring(i);
                }
            } else if (line.charAt(i) == ' '){
                space = true;
            }
            if (line.charAt(i) != ' ') {
                space = false;
            }
        }
        return "";
    }
    
}

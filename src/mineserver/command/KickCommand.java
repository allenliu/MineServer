package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.PlayerClient;
import mineserver.Server;

public class KickCommand extends AbstractCommand {
    
    public KickCommand(Server server) {
        super(server);
    }
    
    @Override
    public String getName() {
        return "Kick";
    }

    public String getHelp() {
        return "Kick players from server.";
    }
    
    public String getUsage() {
        return "kick [playername] [reason]";
    }

    public List<String> getAliases() {
        return Arrays.asList(new String[] {"kick"});
    }
    
    @Override
    public void execute(Client client, String line) {
        String[] args = line.split("\\s+");
        if (args.length == 2) {
            PlayerClient target = server.getTarget(client, args[1]);
            if (target != null) {
                target.kick("");
            }
            return;
        }
        if (args.length >= 3) {
            PlayerClient target = server.getTarget(client, args[1]);
            if (target != null) {
                String reason = extractReason(line);                
                target.kick(reason);
                client.info("You kicked " + target.getName() + " from the server.");
                return;
            }
            return;
        }
        client.usage(getUsage());
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

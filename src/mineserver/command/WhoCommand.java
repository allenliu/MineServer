package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.PlayerClient;
import mineserver.Server;

public class WhoCommand extends AbstractCommand {

    public WhoCommand(Server server) {
        super(server);
    }

    @Override
    public String getName() {
        return "Who";
    }

    public String getHelp() {
        return "Find out player information.";
    }
    
    public String getUsage() {
        return "who [playername]";
    }
    
    public List<String> getAliases() {
        return Arrays.asList(new String[] {"who"});
    }

    @Override
    public void execute(Client client, String line) {
        String[] args = line.split("\\s+");
        if (args.length >= 2) {
            PlayerClient target = server.getTarget(client, args[1]);
            if (target != null) {
                client.info("Group: ");
                client.info("Time online: ");
                client.info("IP: " + target.getIPAddress());
                return;
            }
            return;
        }
        client.usage(getUsage());
    }
}

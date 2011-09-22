package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.PlayerClient;
import mineserver.Server;

public class TeleportCommand extends AbstractCommand {

    public TeleportCommand(Server server) {
        super(server);
    }
    
    @Override
    public String getName() {
        return "Teleport";
    }

    public String getHelp() {
        return "Teleport around.";
    }
    
    public String getUsage() {
        return "[tp|tphere] [playername] [target playername]";
    }

    public List<String> getAliases() {
        return Arrays.asList(new String[] {"tp", "tphere"});
    }
    
    @Override
    public void execute(Client client, String line) {
        String[] args = line.split("\\s+");
        if (args[0].equalsIgnoreCase("tp")) {    
            if (args.length >= 3) {
                PlayerClient target1 = server.getTarget(client, args[1]);
                PlayerClient target2 = server.getTarget(client, args[2]);
                if (target1 != null && target2 != null) {
                    target1.teleport(target2);
                    client.info(target1.getName() + " has been teleported to " + target2.getName() + ".");
                    return;
                }
                return;
            }
            if (args.length >= 2) {
                PlayerClient target = server.getTarget(client, args[1]);
                if (target != null) {
                    client.teleport(target);
                    client.info("You have been teleported to " + target.getName() + ".");
                }
                return;
            }
            return;
        }
        if (args[0].equalsIgnoreCase("tphere")) {
            if (args.length >= 2) {
                PlayerClient target = server.getTarget(client, args[1]);
                if (target != null) {
                    target.teleport(client);
                    client.info(target.getName() + " has been teleported to you.");
                }
                return;
            }
            return;
        }
        client.usage(getUsage());
    }
}

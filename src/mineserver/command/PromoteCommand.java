package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.PlayerClient;
import mineserver.Server;

public class PromoteCommand extends AbstractCommand {

    public PromoteCommand(Server server) {
        super(server);
    }

    @Override
    public String getName() {
        return "Promote";
    }

    public String getHelp() {
        return "Promote or demote a player.";
    }
    
    public String getUsage() {
        return "promote|demote [playername]";
    }
    
    public List<String> getAliases() {
        return Arrays.asList(new String[] {"promote", "demote"});
    }

    @Override
    public void execute(Client client, String line) {
        String[] args = line.split("\\s+");
        if (args.length >= 2) {
            PlayerClient target = server.getTarget(client, args[1]);
            if (target != null) {
                if (args[0].equalsIgnoreCase("promote")) {
                    String success = groupConfig.promote(target.getName());
                    if (success == null) {
                        client.warning(target.getName() + " can not be promoted any higher.");
                        return;
                    } else {
                        target.info("You have been promoted to group: " + success + ".");
                        client.info(target.getName() + " has been promoted to group: " + success + ".");
                        return;
                    }
                } else {
                    String success = groupConfig.demote(target.getName());
                    if (success == null) {
                        client.warning(target.getName() + " can not be demoted any lower.");
                        return;
                    } else {
                        target.warning("You have been demoted to group: " + success + ".");
                        client.info(target.getName() + " has been demoted to group: " + success + ".");
                        return;
                    }
                }
            }
            return;
        }
        client.usage(getUsage());
    }
}

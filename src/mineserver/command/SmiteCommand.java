package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.PlayerClient;
import mineserver.Server;

public class SmiteCommand extends AbstractCommand {
    
    public SmiteCommand(Server server) {
        super(server);
    }

    @Override
    public String getName() {
        return "Smite";
    }

    public String getHelp() {
        return "Call down a thunderbolt.";
    }

    public String getUsage() {
        return "[smite] [playername]";
    }

    public List<String> getAliases() {
        return Arrays.asList(new String[] {"smite"});
    }

    @Override
    public void execute(Client client, String line) {
        String[] args = line.split("\\s+");
        if (args.length >= 2) {
            PlayerClient target = server.getTarget(client, args[1]);
                if (target != null) {
                    server.smite(target);
                    target.info("You were smote by " + client.getName() + ".");
                    client.info("You have smote " + target.getName() + ".");
                return;
            }
            return;
        }
        if (client instanceof PlayerClient) {
            server.smite((PlayerClient) client);
            client.info("You smote yourself.");
            return;
        }
        client.usage(getUsage());
    }
}

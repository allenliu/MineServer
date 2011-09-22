package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.PlayerClient;
import mineserver.Server;

public class VanishCommand extends AbstractCommand {

    public VanishCommand(Server server) {
        super(server);
    }

    @Override
    public String getName() {
        return "Vanish";
    }

    public String getHelp() {
        return "Become invisible to other players.";
    }

    public String getUsage() {
        return "[vanish|unvanish] [playername]";
    }

    public List<String> getAliases() {
        return Arrays.asList(new String[] {"vanish", "unvanish"});
    }

    @Override
    public void execute(Client client, String line) {
        String[] args = line.split("\\s+");
        boolean vanish = args[0].equalsIgnoreCase("vanish");
        if (args.length >= 2) {
            PlayerClient target = server.getTarget(client, args[1]);
            if (target != null) {
                if (vanish) {
                    target.vanish();
                    target.warning("You are now invisible.");
                    client.info("You have made " + target.getName() + " invisible.");
                    return;
                }
                target.unvanish();
                target.warning("You are no longer invisible.");
                client.info("You have made " + target.getName() + " visible.");
                return;
            }
            return;
        }
        if (client instanceof PlayerClient) {
            if (args.length >= 1) {
                if (vanish) {
                    ((PlayerClient) client).vanish();
                    client.info("You are now invisible.");
                    return;
                }
                ((PlayerClient) client).unvanish();
                client.info("You are no longer invisible.");
                return;
            }
        }
        client.usage(getUsage());
    }
}

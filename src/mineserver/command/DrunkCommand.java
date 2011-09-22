package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.PlayerClient;
import mineserver.Server;

public class DrunkCommand extends AbstractCommand {

    public DrunkCommand(Server server) {
        super(server);
    }

    @Override
    public String getName() {
        return "Drunk";
    }

    public String getHelp() {
        return "Get bent.";
    }

    public String getUsage() {
        return "/drunk [playername]";
    }

    public List<String> getAliases() {
        return Arrays.asList(new String[] {"drunk"});
    }

    @Override
    public void execute(Client client, String line) {
        String[] args = line.split("\\s+");
        if (args.length >= 2) {
            PlayerClient target = server.getTarget(client, args[1]);
                if (target != null) {
                    server.confuse(target);
                    target.info(client.getName() + " gave you a drink.");
                    client.info("You gave " + target.getName() + " a drink.");
                return;
            }
            return;
        }
        if (client instanceof PlayerClient) {
            server.confuse((PlayerClient) client);
            client.info("You drank a bit too much.");
            return;
        }
        client.usage(getUsage());
    }
}

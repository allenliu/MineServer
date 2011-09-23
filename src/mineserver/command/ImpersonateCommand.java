package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.PlayerClient;
import mineserver.Server;
import mineserver.event.DestroyEntityEvent;
import mineserver.event.SpawnFakeEntityEvent;

public class ImpersonateCommand extends AbstractCommand {

    public ImpersonateCommand(Server server) {
        super(server);
    }

    @Override
    public String getName() {
        return "Impersonate";
    }

    public String getHelp() {
        return "Pretend to be someone else.";
    }
    
    public String getUsage() {
        return "impersonate [playername]";
    }
    
    public List<String> getAliases() {
        return Arrays.asList(new String[] {"imp", "impersonate"});
    }

    @Override
    public void execute(Client client, String line) {
        if (client instanceof PlayerClient) {
            PlayerClient player = (PlayerClient) client;
            String[] args = line.split("\\s+");
            if (args.length >= 2) {
                for (PlayerClient otherClient : server.getClients()) {
                    if (otherClient != player) {
                        otherClient.addEvent(new DestroyEntityEvent(player));
                        otherClient.addEvent(new SpawnFakeEntityEvent(player, args[1]));
                    }
                }
                client.info("You are now impersonating " + args[1] + "!");
                return;
            }
            client.usage(getUsage());
        }
    }
}

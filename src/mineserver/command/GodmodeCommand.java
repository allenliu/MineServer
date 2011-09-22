package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.PlayerClient;
import mineserver.Server;

public class GodmodeCommand extends AbstractCommand {

    public GodmodeCommand(Server server) {
        super(server);
    }
    
    @Override
    public String getName() {
        return "Godmode";
    }

    public String getHelp() {
        return "Turn off player damage.";
    }
    
    public String getUsage() {
        return "[god|ungod] [playername]";
    }

    public List<String> getAliases() {
        return Arrays.asList(new String[] {"god", "ungod"});
    }
    
    @Override
    public void execute(Client client, String line) {
        String[] args = line.split("\\s+");
        boolean god = args[0].toLowerCase().equals("god");
        if (args.length >= 2) {
            PlayerClient target = server.getTarget(client, args[1]);
            if (target != null) {
                if (god) {
                    target.god();
                    target.warning("You are now invincible.");
                    client.info("You have made " + target.getName() + " invincible.");
                    return;
                }
                target.ungod();
                target.warning("You are no longer invincible.");
                client.info("You have made " + target.getName() + " no longer invincible.");
                return;
            }
            return;
        }
        if (client instanceof PlayerClient) {
            if (args.length >= 1) {
                if (god) {
                    ((PlayerClient) client).god();
                    client.info("You are now invincible.");
                    return;
                }
                ((PlayerClient) client).ungod();
                client.info("You are no longer invincible.");
                return;
            }
        }
        client.usage(getUsage());
    }
}

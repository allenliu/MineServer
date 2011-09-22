package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.PlayerClient;
import mineserver.Server;

public class SpeedCommand extends AbstractCommand {

    public SpeedCommand(Server server) {
        super(server);
    }
    
    @Override
    public String getName() {
        return "Speed";
    }

    public String getHelp() {
        return "Move around faster.";
    }
    
    public String getUsage() {
        return "[speed|unspeed] [playername]";
    }

    public List<String> getAliases() {
        return Arrays.asList(new String[] {"speed", "unspeed"});
    }
    
    @Override
    public void execute(Client client, String line) {
        String[] args = line.split("\\s+");
        boolean speed = args[0].equalsIgnoreCase("speed");
        if (args.length >= 2) {
            PlayerClient target = server.getTarget(client, args[1]);
            if (target != null) {
                if (speed) {
                    target.speed();
                    target.warning("You are now on speed.");
                    client.info("You have given " + target.getName() + " speed.");
                    return;
                }
                target.unspeed();
                target.warning("You are no longer on speed.");
                client.info("You have taken " + target.getName() + " off speed.");
                return;
            }
            return;
        }
        if (client instanceof PlayerClient) {
            if (args.length >= 1) {
                if (speed) {
                    ((PlayerClient) client).speed();
                    client.info("You are now on speed.");
                    return;
                }
                ((PlayerClient) client).unspeed();
                client.info("You are no longer on speed.");
                return;
            }
        }
        client.usage(getUsage());
    }
    
}

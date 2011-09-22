package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.PlayerClient;
import mineserver.Server;

public class MuteCommand extends AbstractCommand {
    
    public MuteCommand(Server server) {
        super(server);
    }
    
    @Override
    public String getName() {
        return "Mute";
    }

    public String getHelp() {
        return "Mute a player";
    }
    
    public String getUsage() {
        return "mute [playername]";
    }

    public List<String> getAliases() {
        return Arrays.asList(new String[] {"mute", "unmute"});
    }
    
    @Override
    public void execute(Client client, String line) {
        String[] args = line.split("\\s+");
        if (args.length >= 2) {
            PlayerClient target = server.getTarget(client, args[1]);
            if (target != null) {
                if (args[0].equalsIgnoreCase("mute")) {
                    target.mute();
                    target.warning("You have been muted.");
                    client.info(target.getName() + " has been muted.");
                } else {
                    target.unmute();
                    target.warning("You have been unmuted.");
                    client.info(target.getName() + " has been unmuted.");
                }
                return;
            }
            return;
        }
        client.usage(getUsage());
    }

}

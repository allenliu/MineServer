package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.PlayerClient;
import mineserver.Server;

public class LockCommand extends AbstractCommand {

    public LockCommand(Server server) {
        super(server);
    }

    @Override
    public String getName() {
        return "Lock";
    }

    public String getHelp() {
        return "Lock and unlock chests.";
    }

    public String getUsage() {
        return "[lock|unlock]";
    }

    public List<String> getAliases() {
        return Arrays.asList(new String[] {"lock", "unlock"});
    }

    @Override
    public void execute(Client client, String line) {
        if (client instanceof PlayerClient) {
            PlayerClient player = (PlayerClient) client;
            String[] args = line.split("\\s+");
            if (args[0].equalsIgnoreCase("lock")) {
                player.toggleLocking();
                if (player.isLocking()) {
                    player.info("You will lock any chests you open or place.");
                } else {
                    player.info("You will no longer lock chests.");
                }
            } else {
                player.toggleUnlocking();
                if (player.isUnlocking()) {
                    player.info("You will unlock the next chest you open.");
                } else {
                    player.info("You will no longer unlock chests.");
                }
            }
        }            
    }

}

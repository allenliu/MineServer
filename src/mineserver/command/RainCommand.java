package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.Server;

public class RainCommand extends AbstractCommand {
    
    public RainCommand(Server server) {
        super(server);
    }
    
    @Override
    public String getName() {
        return "Rain";
    }

    public String getHelp() {
        return "Toggle rain.";
    }
    
    public String getUsage() {
        return "rain";
    }
    
    public List<String> getAliases() {
        return Arrays.asList(new String[] {"rain"});
    }

    @Override
    public void execute(Client client, String line) {
        server.toggleRain();
        if (server.isRaining()) {
            client.info("You summoned a storm.");
        } else {
            client.info("The storm subsides.");
        }
    }

}

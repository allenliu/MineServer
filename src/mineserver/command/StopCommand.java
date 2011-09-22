package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.Server;

public class StopCommand extends AbstractCommand {

    public StopCommand(Server server) {
        super(server);
    }
    
    @Override
    public String getName() {
        return "Stop";
    }

    public String getHelp() {
        return "Stops the server.";
    }
    
    public String getUsage() {
        return "stop";
    }

    public List<String> getAliases() {
        return Arrays.asList(new String[] {"stop"});
    }
    
    @Override
    public void execute(Client client, String line) {
        server.stop();
    }
}

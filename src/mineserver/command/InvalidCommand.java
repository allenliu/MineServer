package mineserver.command;

import mineserver.Client;
import mineserver.Server;

public class InvalidCommand extends AbstractCommand {

    public InvalidCommand(Server server) {
        super(server);
    }

    @Override
    public String getName() {
        return "invalid";
    }

    public String getHelp() {
        return "invalid";
    }
    
    public String getUsage() {
        return "invalid";
    }
    
    @Override
    public void execute(Client client, String line) {
        // should never be called
    }

}

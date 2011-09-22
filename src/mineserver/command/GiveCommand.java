package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.Server;

public class GiveCommand extends AbstractCommand {

    public GiveCommand(Server server) {
        super(server);
    }

    @Override
    public String getName() {
        return "Give";
    }

    @Override
    public String getHelp() {
        return "Give items to players";
    }
    
    public List<String> getAliases() {
        return Arrays.asList(new String[] {"give", "i"});
    }

    @Override
    public void execute(Client client, String line) {
        // TODO Auto-generated method stub
        
    }

}

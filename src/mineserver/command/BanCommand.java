package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.Server;

public class BanCommand extends AbstractCommand {
    
    public BanCommand(Server server) {
        super(server);
    }

    @Override
    public String getName() {
        return "Ban";
    }

    public String getHelp() {
        return "Ban a player";
    }
    
    public String getUsage() {
        return "ban [playername] [reason]";
    }
    
    public List<String> getAliases() {
        return Arrays.asList(new String[] {"ban"});
    }

    @Override
    public void execute(Client client, String line) {
        // TODO Auto-generated method stub
        
    }
    
}

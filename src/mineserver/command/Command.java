package mineserver.command;

import java.util.List;

import mineserver.Client;

public interface Command {

    public String getName();
    public String getHelp();
    public List<String> getAliases();
    public void execute(Client client, String line);
    
}

package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.Server;

public class SaveCommand extends AbstractCommand {

    public SaveCommand(Server server) {
        super(server);
    }

    @Override
    public String getName() {
        return "Save";
    }

    public String getHelp() {
        return "Save map and resource files to disk.";
    }
    
    public String getUsage() {
        return "save";
    }
    
    public List<String> getAliases() {
        return Arrays.asList(new String[] {"save"});
    }

    @Override
    public void execute(Client client, String line) {
        server.saveResources();
        server.runCommand("save-all", null);
        client.info("Map and resource files saved.");
    }
}

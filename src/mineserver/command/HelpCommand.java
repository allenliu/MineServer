package mineserver.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import mineserver.Client;
import mineserver.Server;

public class HelpCommand extends AbstractCommand {

    private Map<String, String> helpText = new TreeMap<String, String>();
    
    public HelpCommand(Server server) {
        super(server);
    }
    
    @Override
    public String getName() {
        return "Help";
    }

    public String getHelp() {
        return "Show a list of available commands.";
    }
    
    public String getUsage() {
        return "help";
    }

    public List<String> getAliases() {
        return Arrays.asList(new String[] {"help", "?"});
    }
    
    @Override
    public void execute(Client client, String line) {
        for (String commandName : helpText.keySet()) {
            if (groupConfig.hasAccess(client.getName(), permissionConfig.allow(commandName))) {
                client.info(commandName + ": " + helpText.get(commandName));
            }
        }
    }
    
    public void loadHelpText(Set<Command> commandSet) {
        for (Command command : commandSet) {
            if (permissionConfig.show(command.getName())) {
                helpText.put(command.getName(), command.getHelp());
            }
        }
    }
}

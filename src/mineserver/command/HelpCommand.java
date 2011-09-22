package mineserver.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import mineserver.Client;
import mineserver.Server;

public class HelpCommand extends AbstractCommand {

    private List<String> helpText = new ArrayList<String>();
    
    public HelpCommand(Server server) {
        super(server);
    }
    
    @Override
    public String getName() {
        return "Help";
    }

    public String getHelp() {
        return "Display the help text";
    }
    
    public String getUsage() {
        return "help";
    }

    public List<String> getAliases() {
        return Arrays.asList(new String[] {"help"});
    }
    
    @Override
    public void execute(Client client, String line) {
        for (String help : helpText) {
            client.info(help);
        }
    }
    
    public void loadHelpText(Set<Command> commandSet) {
        for (Command command : commandSet) {
            helpText.add(command.getName() + ": " + command.getHelp());
        }
        Collections.sort(helpText);
    }
}

package mineserver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mineserver.command.Command;
import mineserver.command.HelpCommand;
import mineserver.command.InvalidCommand;
import mineserver.config.PermissionConfig;

public class CommandFactory {

    private Server server;
    private PermissionConfig permissions;
    private Map<String, Command> commands;
    private InvalidCommand invalidCommand;

    public CommandFactory(Server server, PermissionConfig permissions) {
        this.server = server;
        this.permissions = permissions;
        commands = new HashMap<String, Command>();
        invalidCommand = new InvalidCommand(server);
        loadCommands();
    }

    private void loadCommands() {
        Set<Command> commandSet = new HashSet<Command>();
        for (String commandName : permissions.getPermissions().keySet()) {
            Command command = null;
            try {
                command = (Command) Class.forName("mineserver.command." + commandName + "Command").getConstructor(Server.class).newInstance(server);
            } catch (Exception e) {
                IOHandler.println("Error loading command '" + commandName + "'.");
                e.printStackTrace();
                continue;
            }
            commandSet.add(command);
            for (String alias : command.getAliases()) {
                commands.put(alias, command);
            }
        }
        ((HelpCommand) commands.get("help")).loadHelpText(commandSet);

    }

    public Map<String, Command> getCommands() {
        return commands;
    }

    public Command getCommand(String line) {
        String[] parts = line.split("( )+");
        if (commands.containsKey(parts[0].toLowerCase())) {
            return commands.get(parts[0].toLowerCase());
        }
        return getInvalidCommand();
    }

    public Command getInvalidCommand() {
        return invalidCommand;
    }
}

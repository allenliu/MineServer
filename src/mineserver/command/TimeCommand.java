package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.Server;

public class TimeCommand extends AbstractCommand {

    private static final int DAY_TIME = 0;
    private static final int NIGHT_TIME = 14000;
    
    public TimeCommand(Server server) {
        super(server);
    }
    
    @Override
    public String getName() {
        return "Time";
    }

    public String getHelp() {
        return "Change the in-game time";
    }
    
    public String getUsage() {
        return "time [set|add] #";
    }
    
    public List<String> getAliases() {
        return Arrays.asList(new String[] {"time", "day", "night"});
    }

    @Override
    public void execute(Client client, String line) {
        String[] args = line.split("\\s+");
        if (args[0].equalsIgnoreCase("day")) {
            server.runCommand("time", new String[] {"set", Integer.toString(DAY_TIME)});
            client.info("Time was set to day.");
            return;
        }
        if (args[0].equalsIgnoreCase("night")) {
            server.runCommand("time", new String[] {"set", Integer.toString(NIGHT_TIME)});
            client.info("Time was set to night.");
            return;
        }
        // first arg must be "time"
        if (args.length >= 3) {
            int i;
            try {
                i = Integer.parseInt(args[2]);
            } catch (Exception e) {
                client.usage(getUsage());
                return;
            }
            String arg = args[1];
            if (arg.equalsIgnoreCase("set")) {
                server.runCommand("time", new String[] {"set", Integer.toString(i)});
                client.info("Time was set to " + i + ".");
                return;
            } else if (arg.equalsIgnoreCase("add")) {
                server.runCommand("time", new String[] {"add", Integer.toString(i)});
                client.info(i + " was added to the time.");
                return;
            }
        }
        client.usage(getUsage());
    }
}

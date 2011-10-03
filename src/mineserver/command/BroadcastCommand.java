package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.Server;

public class BroadcastCommand extends AbstractCommand {

    public BroadcastCommand(Server server) {
        super(server);
    }

    @Override
    public String getName() {
        return "Broadcast";
    }

    public String getHelp() {
        return "Broadcast a message to the whole server.";
    }
    
    public String getUsage() {
        return "broadcast [message]";
    }
    
    public List<String> getAliases() {
        return Arrays.asList(new String[] {"broadcast", "b"});
    }

    @Override
    public void execute(Client client, String line) {
        String message = extractMessage(line);
        if (message.length() > 0) {
            server.runCommand("say", message);
            return;
        }
        client.usage(getUsage());
    }
    
    private String extractMessage(String line) {
        boolean space = false;
        int n = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) != ' ' && space) {
                n++;
                if (n == 1) {
                    return line.substring(i);
                }
            } else if (line.charAt(i) == ' '){
                space = true;
            }
            if (line.charAt(i) != ' ') {
                space = false;
            }
        }
        return "";
    }
    
}

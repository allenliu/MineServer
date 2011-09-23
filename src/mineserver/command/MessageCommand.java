package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.PlayerClient;
import mineserver.Server;

public class MessageCommand extends AbstractCommand {

    public MessageCommand(Server server) {
        super(server);
    }

    @Override
    public String getName() {
        return "Message";
    }

    @Override
    public String getHelp() {
        return "Private message other players.";
    }
    
    public String getUsage() {
        return "message [playername] [message-contents]";
    }
    
    public List<String> getAliases() {
        return Arrays.asList(new String[] {"message", "msg", "m"});
    }

    @Override
    public void execute(Client client, String line) {
        String[] args = line.split("\\s+");
        if (args.length == 2) {
            client.usage(getUsage());
            return;
        }
        if (args.length >= 3) {
            PlayerClient target = server.getTarget(client, args[1]);
            if (target != null) {
                String message = extractMessage(line);                
                target.message(client, message);
                client.sentMessage(target, message);
                return;
            }
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
                if (n == 2) {
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

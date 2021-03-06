package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.Server;

public class ReplyCommand extends AbstractCommand {

    public ReplyCommand(Server server) {
        super(server);
    }

    @Override
    public String getName() {
        return "Reply";
    }

    @Override
    public String getHelp() {
        return "Reply to your last private message.";
    }
    
    public String getUsage() {
        return "reply [message-contents]";
    }
    
    public List<String> getAliases() {
        return Arrays.asList(new String[] {"reply", "r"});
    }

    @Override
    public void execute(Client client, String line) {
        String[] args = line.split("\\s+");
        if (args.length == 1) {
            client.usage(getUsage());
            return;
        }
        if (args.length >= 2) {
            Client target = client.getLastMessageSource();
            if (target != null) {
                String message = extractMessage(line);                
                target.message(client, message);
                client.sentMessage(target, message);
                return;
            }
            client.warning("There is no one to reply to.");
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

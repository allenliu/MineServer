package mineserver.command;

import java.util.Arrays;
import java.util.List;

import mineserver.Client;
import mineserver.Server;

public class BackupCommand extends AbstractCommand {
    
    public BackupCommand(Server server) {
        super(server);
    }

    @Override
    public String getName() {
        return "Backup";
    }

    public String getHelp() {
        return "Force a server backup.";
    }
    
    public String getUsage() {
        return "backup";
    }
    
    public List<String> getAliases() {
        return Arrays.asList(new String[] {"backup"});
    }

    @Override
    public void execute(Client client, String line) {
        server.forceBackup();
        client.info("Forcing server backup...");
    }
}

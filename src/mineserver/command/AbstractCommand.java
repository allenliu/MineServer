package mineserver.command;

import java.util.Collections;
import java.util.List;

import mineserver.Server;
import mineserver.config.GroupConfig;
import mineserver.config.PermissionConfig;

public abstract class AbstractCommand implements Command {

    protected Server server;
    protected PermissionConfig permissionConfig;
    protected GroupConfig groupConfig;
    protected String usage;

    public AbstractCommand(Server server) {
        this.server = server;
        this.permissionConfig = server.getPermissionConfig();
        this.groupConfig = server.getGroupConfig();
    }

    public List<String> getAliases() {
        return Collections.emptyList();
    }
    
}

package mineserver.command;

import java.util.Collections;
import java.util.List;

import mineserver.Server;

public abstract class AbstractCommand implements Command {

    protected Server server;
    protected String usage;

    public AbstractCommand(Server server) {
        this.server = server;
    }

    public List<String> getAliases() {
        return Collections.emptyList();
    }
    
}

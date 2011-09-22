package mineserver;

public class ServerClient implements Client {

    private final Server server;
    
    public ServerClient(Server server) {
        this.server = server;
    }
    
    public void setName(String name) {
        
    }
    
    public String getName() {
        return "ServerClient";
    }
    
    public void info(String message) {
        
    }
    
    public void usage(String message) {}
    
    public void warning(String message) {}
    
    public void mute() {}
    
    public void unmute() {}
    
    public void teleport(Client destination) {}
    
}

package mineserver;

public class ServerClient implements Client {

    private final Server server;
    
    public ServerClient(Server server) {
        this.server = server;
    }
    
    public void setName(String name) {}
    
    public String getName() {
        return "Console";
    }
    
    public void info(String message) {
        IOHandler.println(message);
    }
    
    public void usage(String message) {
        IOHandler.println("Usage: " + message);
    }
    
    public void warning(String message) {
        IOHandler.println(message);
    }
    
    public void mute() {}
    
    public void unmute() {}
    
    public void teleport(Client destination) {}
    
}

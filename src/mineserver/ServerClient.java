package mineserver;

public class ServerClient implements Client {

    private final Server server;
    
    private Client lastMessageSource;
    
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
    
    public void message(Client source, String message) {
        IOHandler.println("[" + source.getName() + " -> Console] " + message);
        lastMessageSource = source;
    }
    
    public void sentMessage(Client target, String message) {
        IOHandler.println("[Console -> " + target.getName() + "] " + message);
    }
    
    public Client getLastMessageSource() {
        return lastMessageSource;
    }
    
    public void mute() {}
    
    public void unmute() {}
    
    public void teleport(Client destination) {}
    
}

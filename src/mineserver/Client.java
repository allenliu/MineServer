package mineserver;

public interface Client {
    
    public void setName(String name);
    public String getName();
    public void info(String message);
    public void usage(String message);
    public void warning(String message);    
    
    public void mute();
    public void unmute();
    
    public void teleport(Client destination);

}

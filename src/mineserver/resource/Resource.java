package mineserver.resource;

public interface Resource {

    public static final String CONFIG_PATH = "config";
    public static final String RESOURCE_PATH = "data";
    
    public void load();
    
    public void save();
}

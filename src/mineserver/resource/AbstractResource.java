package mineserver.resource;

public abstract class AbstractResource implements Resource {
    
    protected String filename;
    
    public AbstractResource(String filename) {
        this.filename = filename;
        load();
    }

}

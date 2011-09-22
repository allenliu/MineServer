package mineserver.event;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Event {

    public void execute(DataOutputStream out) throws IOException;    
    
}

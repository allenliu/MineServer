package mineserver.event;

import java.io.DataOutputStream;
import java.io.IOException;

import mineserver.PlayerClient;

public class DestroyEntityEvent implements Event {

    private PlayerClient target;
    
    public DestroyEntityEvent(PlayerClient target) {
        this.target = target;
    }
    
    public void execute(DataOutputStream out) throws IOException {
        out.writeByte((byte) 0x1d);
        out.writeInt(target.getEntityId());
    }
    
}

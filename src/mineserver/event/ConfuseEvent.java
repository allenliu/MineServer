package mineserver.event;

import java.io.DataOutputStream;
import java.io.IOException;

import mineserver.Effect;
import mineserver.PlayerClient;

public class ConfuseEvent implements Event {
    
    private PlayerClient target;
    
    public ConfuseEvent(PlayerClient target) {
        this.target = target;
    }
    
    public void execute(DataOutputStream out) throws IOException {
        out.writeByte((byte) 0x29);
        out.writeInt(target.getEntityId());
        out.writeByte((byte) Effect.CONFUSION.id());
        out.writeByte((byte) 0x00);
        out.writeShort((short) 600);    // length of confusion
    }

}

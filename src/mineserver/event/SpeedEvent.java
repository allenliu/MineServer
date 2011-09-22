package mineserver.event;

import java.io.DataOutputStream;
import java.io.IOException;

import mineserver.Effect;
import mineserver.PlayerClient;

public class SpeedEvent implements Event {

    private PlayerClient target;
    
    public SpeedEvent(PlayerClient target) {
        this.target = target;
    }
    
    public void execute(DataOutputStream out) throws IOException {
        out.writeByte((byte) 0x29);
        out.writeInt(target.getEntityId());
        out.writeByte((byte) Effect.MOVESPEED.id());
        out.writeByte((byte) 0x05);
        out.writeShort((short) Short.MAX_VALUE);
    }
    
}

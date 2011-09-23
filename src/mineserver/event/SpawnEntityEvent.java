package mineserver.event;

import java.io.DataOutputStream;
import java.io.IOException;

import mineserver.PlayerClient;

public class SpawnEntityEvent implements Event {

    private PlayerClient target;
    
    public SpawnEntityEvent(PlayerClient target) {
        this.target = target;
    }
    
    public void execute(DataOutputStream out) throws IOException {
        out.writeByte((byte) 0x14);
        out.writeInt(target.getEntityId());
        String name = target.getName();
        out.writeShort((short) name.length());
        out.writeChars(name);
        out.writeInt((int) (target.getPosition().x() * 32.0));
        out.writeInt((int) (target.getPosition().y() * 32.0));
        out.writeInt((int) (target.getPosition().z() * 32.0));
        out.writeByte((byte) 0x00);
        out.writeByte((byte) 0x00);
        out.writeShort((short) 0);        
    }
    
}

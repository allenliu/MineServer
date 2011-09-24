package mineserver.event;

import java.io.DataOutputStream;
import java.io.IOException;

import mineserver.PlayerClient;

public class SpawnFakeEntityEvent implements Event {

    private PlayerClient target;
    private String fakeName;
    
    public SpawnFakeEntityEvent(PlayerClient target, String fakeName) {
        this.target = target;
        this.fakeName = fakeName;
    }
    
    public void execute(DataOutputStream out) throws IOException {
        out.writeByte((byte) 0x14);
        out.writeInt(target.getEntityId());
        out.writeShort((short) fakeName.length());
        out.writeChars(fakeName);
        out.writeInt((int) (target.getPosition().x() * 32.0));
        out.writeInt((int) (target.getPosition().y() * 32.0));
        out.writeInt((int) (target.getPosition().z() * 32.0));
        out.writeByte((byte) target.getPosition().yaw());
        out.writeByte((byte) target.getPosition().pitch());
        out.writeShort((short) 0);
    }
}

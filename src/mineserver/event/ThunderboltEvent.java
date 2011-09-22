package mineserver.event;

import java.io.DataOutputStream;
import java.io.IOException;

import mineserver.PlayerClient;

public class ThunderboltEvent implements Event {

    private PlayerClient target;
    
    public ThunderboltEvent(PlayerClient target) {
        this.target = target;
    }
    
    public void execute(DataOutputStream out) throws IOException {
        out.writeByte((byte) 0x47);
        out.writeInt(1337);
        out.writeBoolean(true);
        out.writeInt((int) target.getPosition().x());
        out.writeInt((int) target.getPosition().y());
        out.writeInt((int) target.getPosition().z()); 
    }
}

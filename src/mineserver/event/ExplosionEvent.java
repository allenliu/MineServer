package mineserver.event;

import java.io.DataOutputStream;
import java.io.IOException;

import mineserver.PlayerClient;

public class ExplosionEvent implements Event {
    
    private PlayerClient target;
    
    public ExplosionEvent(PlayerClient target) {
        this.target = target;
    }
    
    public void execute(DataOutputStream out) throws IOException {
        out.writeByte((byte) 0x3c);
        out.writeDouble(target.getPosition().x());
        out.writeDouble(target.getPosition().y());
        out.writeDouble(target.getPosition().z());
        out.writeFloat((float) 5);
        out.writeInt(0);
    }

}

package mineserver.event;

import java.io.DataOutputStream;
import java.io.IOException;

public class RainEvent implements Event {

    private boolean raining;
    
    public RainEvent(boolean raining) {
        this.raining = raining;
    }
    
    public void execute(DataOutputStream out) throws IOException {
        out.writeByte((byte) 0x46);
        if (raining) {
            out.writeByte((byte) 0x01);
        } else {
            out.writeByte((byte) 0x02);
        }
        out.writeByte((byte) 0x00);
    }
}

package mineserver.event;

import java.io.DataOutputStream;
import java.io.IOException;

import mineserver.Effect;
import mineserver.PlayerClient;

public class RemoveEffectEvent implements Event {

    private PlayerClient target;
    private Effect effect;
    
    public RemoveEffectEvent(PlayerClient target, Effect effect) {
        this.target = target;
        this.effect = effect;
    }
    
    public void execute(DataOutputStream out) throws IOException {
        out.writeByte((byte) 0x2a);
        out.writeInt(target.getEntityId());
        out.writeByte((byte) effect.id());
    }
    
}

package mineserver;

public enum Effect {

    MOVESPEED((byte) 0x01),
    MOVESLOWDOWN((byte) 0x02),
    CONFUSION((byte) 0x09);
    
    private byte id;
    
    Effect(byte id) {
        this.id = id;
    }
    
    public byte id() {
        return id;
    }
    
}

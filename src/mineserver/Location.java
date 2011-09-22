package mineserver;


public class Location {

    private int x;
    private byte y;
    private int z;
    
    public Location(int x, byte y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public int x() {
        return x;
    }
    
    public byte y() {
        return y;
    }
    
    public int z() {
        return z;
    }
    
    public boolean equals(Location location) {
        return location.x() == x && location.y() == y && location.z() == z;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Location) {
            return equals((Location) o);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }
    
    @Override
    public int hashCode() {
        return x + (int) y + 3 * z;
    }
       
}

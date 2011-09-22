package mineserver.resource;

import java.util.HashMap;
import java.util.Map;

import mineserver.Location;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

public class ChestList extends YamlResource {

    private static Map<Location, String> data = new HashMap<Location, String>();
    
    public ChestList() {
        super("chestlist.yaml");
    }
    
    public void load() {
        data = (Map<Location, String>) super.load(new Yaml(new LocationConstructor()), data);
    }
    
    public void save() {
        super.save(new Yaml(new LocationRepresenter()), data);
    }
    
    public boolean addLocked(String owner, Location location) {
        AdjacentStatus status = getAdjacentStatus(owner, location);
        if (status == AdjacentStatus.NONE) {
            data.put(location, owner);
            return true;
        } else if (status == AdjacentStatus.OWN_CHEST) {
            data.put(location, owner);
            return true;
        } else {
            return false;
        }
    }
    
    public boolean addUnlocked(String owner, Location location) {
        AdjacentStatus status = getAdjacentStatus(owner, location);
        if (status == AdjacentStatus.NONE) {
            return true;
        } else if (status == AdjacentStatus.OWN_CHEST) {
            data.put(location, owner);
            return true;
        } else {
            return false;
        }
    }
    
    public AdjacentStatus getAdjacentStatus(String owner, Location location) {
        int x = location.x();
        byte y = location.y();
        int z = location.z();
        String o = data.get(new Location(x + 1, y, z));
        if (o != null) {
            if (!o.equals(owner)) {
                return AdjacentStatus.OTHER_CHEST;
            }
            return AdjacentStatus.OWN_CHEST;
        }
        o = data.get(new Location(x - 1, y, z));
        if (o != null) {
            if (!o.equals(owner)) {
                return AdjacentStatus.OTHER_CHEST;
            }
            return AdjacentStatus.OWN_CHEST;
        }
        o = data.get(new Location(x, (byte) (y + 1), z));
        if (o != null) {
            if (!o.equals(owner)) {
                return AdjacentStatus.OTHER_CHEST;
            }
            return AdjacentStatus.OWN_CHEST;
        }
        o = data.get(new Location(x, (byte) (y - 1), z));
        if (o != null) {
            if (!o.equals(owner)) {
                return AdjacentStatus.OTHER_CHEST;
            }
            return AdjacentStatus.OWN_CHEST;
        }
        o = data.get(new Location(x, y, z + 1));
        if (o != null) {
            if (!o.equals(owner)) {
                return AdjacentStatus.OTHER_CHEST;
            }
            return AdjacentStatus.OWN_CHEST;
        }
        o = data.get(new Location(x, y, z - 1));
        if (o != null) {
            if (!o.equals(owner)) {
                return AdjacentStatus.OTHER_CHEST;
            }
            return AdjacentStatus.OWN_CHEST;
        }
        return AdjacentStatus.NONE;
    }
    
    public void remove(Location location) {
        data.remove(location);
    }
    
    public boolean contains(Location location) {
        return data.containsKey(location);
    }
    
    public String getOwner(Location location) {
        return data.get(location);
    }
    
    public enum AdjacentStatus {
        NONE, OWN_CHEST, OTHER_CHEST;
    }
    
    public class LocationConstructor extends SafeConstructor {
        public LocationConstructor() {
            this.yamlConstructors.put(new Tag("!L"), new ConstructLocation());
        }

        private class ConstructLocation extends AbstractConstruct {
            public Object construct(Node node) {
                String val = (String) constructScalar((ScalarNode) node);
                String[] args = val.split(",");
                Integer x = Integer.parseInt(args[0]);
                Byte y = Byte.parseByte(args[1]);
                Integer z = Integer.parseInt(args[2]);
                return new Location(x, y, z);
            }
        }
    }
    
    public class LocationRepresenter extends Representer {
        public LocationRepresenter() {
            this.representers.put(Location.class, new RepresentLocation());
        }

        private class RepresentLocation implements Represent {
            public Node representData(Object data) {
                Location location = (Location) data;
                String value = location.toString();
                return representScalar(new Tag("!L"), value);
            }
        }
    }
}

package mineserver.resource;

import java.util.HashSet;
import java.util.Set;

import mineserver.Client;

import org.yaml.snakeyaml.Yaml;

public class MuteList extends YamlResource {

    private static Set<String> data = new HashSet<String>();
    
    public MuteList() {
        super("mutelist.yaml");
    }

    public void load() {
        data = (Set<String>) super.load(new Yaml(), data);
    }
    
    public void save() {
        super.save(new Yaml(), data);
    }
    
    public void mute(Client client) {
        data.add(client.getName());
    }

    public void unmute(Client client) {
        data.remove(client.getName());
    }

    public boolean isMuted(Client client) {
        return data.contains(client.getName());
    }
}

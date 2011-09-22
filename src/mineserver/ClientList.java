package mineserver;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientList {

    private Map<String, PlayerClient> nameMap = new ConcurrentHashMap<String, PlayerClient>();
    private Map<Integer, PlayerClient> eidMap = new ConcurrentHashMap<Integer, PlayerClient>();
    
    private Map<String, Integer> nameToEid = new ConcurrentHashMap<String, Integer>();
    private Map<Integer, String> eidToName = new ConcurrentHashMap<Integer, String>();
    
    public ClientList() {
        
    }
    
    public void add(PlayerClient client) {
        nameMap.put(client.getName(), client);
        eidMap.put(client.getEntityId(), client);
        
        nameToEid.put(client.getName(), client.getEntityId());
        eidToName.put(client.getEntityId(), client.getName());
    }
    
    public synchronized void remove(PlayerClient client) {
        nameMap.remove(client.getName());
        eidMap.remove(client.getEntityId());
        nameToEid.remove(client.getName());
        eidToName.remove(client.getEntityId());
        notifyAll();
      }
    
    public boolean contains(String clientName) {
        return nameMap.containsKey(clientName);
    }
    
    public boolean contains(int eid) {
        return eidMap.containsKey(eid);
    }
    
    public PlayerClient get(String clientName) {
        return nameMap.get(clientName);
    }
    
    public PlayerClient get(int eid) {
        return eidMap.get(eid);
    }
    
    public Collection<PlayerClient> getAll() {
        return nameMap.values();
    }
    
    public void remove(String clientName) {
        nameMap.remove(clientName);
    }
}

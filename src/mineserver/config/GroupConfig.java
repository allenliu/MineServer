package mineserver.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import mineserver.IOHandler;
import mineserver.resource.AbstractResource;
import mineserver.resource.Resource;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

public class GroupConfig extends AbstractResource {
    
    private static final String DEFAULTS_PATH = "defaults";

    private Map<Integer, String> groupNames;
    private Map<Integer, Set<String>> groupMembers;
    private Map<String, Integer> memberToLevel;

    public GroupConfig() {
        super("groups.yaml");
    }
    
    @Override
    public void load() {
        File dir = new File(Resource.CONFIG_PATH);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                IOHandler.println("Error creating " + Resource.CONFIG_PATH + " folder.");
                System.exit(-1);
            }
        }
        File file = new File(dir, filename);
        if (!file.exists()) {
            IOHandler.println("Loading default " + filename);
            try {
                file.createNewFile();
                InputStream stream = getClass().getResourceAsStream(DEFAULTS_PATH + "/" + filename);
                try {
                    FileUtils.copyInputStreamToFile(stream, file);
                } finally {
                    stream.close();
                }
            } catch (Exception e) {
                IOHandler.println("Error loading default " + filename);
                e.printStackTrace();
            }
        }
        
        InputStream input;
        try {
            input = new FileInputStream(file);
            Yaml yaml = new Yaml();

            Iterator<Object> iter = yaml.loadAll(input).iterator();
            groupNames = (Map<Integer, String>) iter.next();
            groupMembers = (Map<Integer, Set<String>>) iter.next();

            input.close();
            
            memberToLevel = new HashMap<String, Integer>();
            for (Integer i : groupMembers.keySet()) {
                for (String name : groupMembers.get(i)) {
                    memberToLevel.put(name, i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void save() {
        // TODO Auto-generated method stub

    }
    
    public boolean hasAccess(String name, int level) {
        if (level == 1) {
            return true;
        }
        int foundLevel = 1;
        if (memberToLevel.containsKey(name)) {
            foundLevel = memberToLevel.get(name);
        }
        return foundLevel >= level;
    }
}

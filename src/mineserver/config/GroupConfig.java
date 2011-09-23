package mineserver.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
        FileWriter writer = null;
        try {
            writer = new FileWriter(new File(Resource.CONFIG_PATH, filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Object> data = new LinkedList<Object>();
        data.add(groupNames);
        data.add(groupMembers);
        Yaml yaml = new Yaml();
        yaml.dumpAll(data.iterator(), writer);

    }
    
    public boolean hasAccess(String name, int level) {
        if (level == 0) {
            return true;
        }
        int foundLevel = 0;
        if (memberToLevel.containsKey(name)) {
            foundLevel = memberToLevel.get(name);
        }
        return foundLevel >= level;
    }
    
    private int getMaxLevel() {
        int level = 0;
        for (Integer i : groupNames.keySet()) {
            level = Math.max(level, i);
        }
        return level;
    }
    
    public String promote(String clientName) {
        if (memberToLevel.containsKey(clientName)) {
            int max = getMaxLevel();
            int currentLevel = memberToLevel.get(clientName);
            if (currentLevel == max) {
                return null;
            }
            groupMembers.get(currentLevel).remove(clientName);
            groupMembers.get(currentLevel + 1).add(clientName);
            memberToLevel.put(clientName, currentLevel + 1);
            return groupNames.get(currentLevel + 1);
        }
        groupMembers.get(1).add(clientName);
        memberToLevel.put(clientName, 1);
        return groupNames.get(1);
    }
    
    public String demote(String clientName) {
        if (memberToLevel.containsKey(clientName)) {
            int currentLevel = memberToLevel.get(clientName);
            if (currentLevel == 0) {
                return null;
            }
            groupMembers.get(currentLevel).remove(clientName);
            groupMembers.get(currentLevel - 1).add(clientName);
            memberToLevel.put(clientName, currentLevel - 1);
            return groupNames.get(currentLevel - 1);
        }
        return null;
    }
}

package mineserver.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import mineserver.IOHandler;
import mineserver.Server;
import mineserver.resource.Resource;

public class ServerProperties implements Resource {

    private static final String DEFAULTS_PATH = "defaults";   
    private static final String MINESERVER_PROPERTIES = "mineserver.properties";
    private static final String VANILLA_PROPERTIES = "server.properties";
    private static final String VANILLA_HEADER = "Minecraft server properties\nAuto-generated, do not edit.";
    
    private static Properties properties = new Properties();
    private File propertiesFile;
    private static Properties vanilla = new Properties();
    private File vanillaFile;

    public ServerProperties() {
    }

    public void load() {
        File dir = new File(Resource.CONFIG_PATH);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                IOHandler.println("Error creating " + Resource.CONFIG_PATH + " folder.");
                System.exit(-1);
            }
        }
        propertiesFile = new File(dir, MINESERVER_PROPERTIES);
        if (!propertiesFile.exists()) {
            IOHandler.println("Loading default " + MINESERVER_PROPERTIES);
            InputStream stream = getClass().getResourceAsStream(DEFAULTS_PATH + "/" + MINESERVER_PROPERTIES);
            try {
                try {
                    properties.load(stream);
                } finally {
                    stream.close();
                }

                propertiesFile.createNewFile();
                FileOutputStream out = new FileOutputStream(propertiesFile);
                properties.store(out, null);
                
            } catch (Exception e) {
                IOHandler.println("Error loading default " + MINESERVER_PROPERTIES);
                e.printStackTrace();
            }
        } else {
            try {
                InputStream stream = new FileInputStream(propertiesFile);
                try {
                    properties.load(stream);
                } finally {
                    stream.close();
                } 
            } catch (Exception e) {
            }
        }
        
        vanillaFile = new File(Server.SERVER_PATH + "/" + VANILLA_PROPERTIES);

        try {
            OutputStream stream = new FileOutputStream(vanillaFile);
            try {
                properties.store(stream, VANILLA_HEADER);
            } finally {
                stream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // inelegant solution to prevent overwriting of server.properties file
        vanillaFile.setReadOnly();
        
    }

    public void save() {
        File file = new File(Server.SERVER_PATH + "/" + VANILLA_PROPERTIES);

        try {
            OutputStream stream = new FileOutputStream(file);
            try {
                vanilla.store(stream, VANILLA_HEADER);
            } finally {
                stream.close();
            }
        } catch (IOException e) {
        }
    }
    
    public void setReadable() {
        vanillaFile.setWritable(true);
    }
    
    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }
    
    public static int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }
    
    public static String getString(String key) {
        return properties.getProperty(key);
    }

}

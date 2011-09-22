package mineserver.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import mineserver.IOHandler;
import mineserver.resource.AbstractResource;
import mineserver.resource.Resource;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

public class PermissionConfig extends AbstractResource {

    private static final String DEFAULTS_PATH = "defaults";

    private Map<String, Map<String, String>> permissions;

    public PermissionConfig() {
        super("permissions.yaml");
    }

    @SuppressWarnings("unchecked")
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

            permissions = (Map<String, Map<String, String>>) yaml.load(input);
            input.close();
        } catch (Exception e) {
        }

    }

    @Override
    public void save() {
        // TODO Auto-generated method stub

    }

    public Map<String, Map<String, String>> getPermissions() {
        return permissions;
    }

}

package mineserver.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import mineserver.IOHandler;

import org.yaml.snakeyaml.Yaml;

public abstract class YamlResource extends AbstractResource {
    
    private File dir;
    
    public YamlResource(String filename) {
        super(filename);
    }

    public Object load(Yaml yaml, Object data) {
        dir = new File(Resource.RESOURCE_PATH);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                IOHandler.println("Error creating " + Resource.RESOURCE_PATH + " folder.");
                System.exit(-1);
            }
        }
        IOHandler.println("Loading " + filename);
        File f = new File(dir, filename);
        if (!f.exists() || !f.isFile()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            InputStream input;
            try {
                input = new FileInputStream(f);

                data = yaml.load(input);
                input.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return data;
    }

    public void save(Yaml yaml, Object data) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(new File(dir, filename));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        yaml.dump(data, writer);
    }
}

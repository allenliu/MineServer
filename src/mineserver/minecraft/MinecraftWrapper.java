package mineserver.minecraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipFile;

import mineserver.IOHandler;
import mineserver.Server;
import mineserver.config.ServerProperties;
import mineserver.thread.SystemInputQueue;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class MinecraftWrapper {

    private static final String DOWNLOAD_URL = "http://www.minecraft.net/download/minecraft_server.jar";
    private static final String SERVER_JAR = "minecraft_server.jar";
    
    private Process minecraft;
    private List<Thread> threads = new LinkedList<Thread>();
    private InputWrapper in;
    private SystemInputQueue systemInput;
    private IOHandler ioHandler;
    
    private volatile boolean loaded = false;

    public MinecraftWrapper(SystemInputQueue systemInput, IOHandler ioHandler) {
        this.systemInput = systemInput;
        this.ioHandler = ioHandler;
    }

    public boolean downloadServerJar() {
        if (verifyServerJar()) {
            return true;
        }
        IOHandler.printf("Downloading %s from minecraft.net\n", SERVER_JAR);

        HttpClient httpclient = new DefaultHttpClient();
        String responseBody;
        try {
            HttpGet httpget = new HttpGet(DOWNLOAD_URL);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            try {
                responseBody = httpclient.execute(httpget, responseHandler);
            } catch (ClientProtocolException e) {
                System.err.println("fail");
                //autodownloadError(e, "download");
                return false;
            } catch (IOException e) {
                System.err.println("fail");
                ///autodownloadError(e, "download");
                return false;
            }
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

        
        new File(Server.SERVER_PATH).mkdir();

        OutputStream outputFile;
        try {
            outputFile = new FileOutputStream(Server.SERVER_PATH + "/" + SERVER_JAR);
        } catch (FileNotFoundException e) {
            //autodownloadError(e, "save");
            return false;
        }

        try {
            outputFile.write(responseBody.getBytes("ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            //autodownloadError(e, "save");
            return false;
        } catch (IOException e) {
            //autodownloadError(e, "save");
            return false;
        }

        if (verifyServerJar()) {
            IOHandler.println("Download complete.");
            return true;
        } else {
            System.out.printf("%s is corrupt!\n", SERVER_JAR);
            return false;
        }
    }

    private boolean verifyServerJar() {
        boolean valid = false;
        try {
            ZipFile jar = new ZipFile(Server.SERVER_PATH + "/" + SERVER_JAR);
            valid = jar.size() > 200;
            jar.close();
        } catch (IOException e) {
            IOHandler.printf("Error verifying %s.\n", SERVER_JAR);
        }

        return valid;
    }

    public void start() throws InterruptedException {
        Server.serverProperties.load();
        Server.serverProperties.save();
        Runtime runtime = Runtime.getRuntime();

        try {
            minecraft = runtime.exec("java -Xms512M -Xmx1024M -jar " + SERVER_JAR + " nogui", null, new File(Server.SERVER_PATH));
            IOHandler.println("Starting server.");
        } catch (IOException e) {
            IOHandler.println(e);
            IOHandler.printf("FATAL ERROR: Could not start %s!\n");
            System.exit(-1);
        }

        threads.add(new ShutdownHook(this));
        
        threads.add(in = new InputWrapper(systemInput, minecraft.getOutputStream(), ioHandler));

        in.start();
        in.setName("InputWrapper");
        
        Thread stdout;
        Thread stderr;
        
        threads.add(stdout = new OutputWrapper(minecraft.getInputStream(), ioHandler));
        threads.add(stderr = new OutputWrapper(minecraft.getErrorStream(), ioHandler));
    
        stdout.start();
        stderr.start();
        
        stdout.setName("stdout");
        stderr.setName("stderr");
        
        ioHandler.waitUntilLoaded();
    }

    public void stop() {
        IOHandler.println("Stopping server.");
        execute("stop", null);
        
        Server.serverProperties.setReadable();
        
        for (Thread thread : threads) {
            thread.interrupt();
        }

        while (threads.size() > 0) {
            Thread thread = threads.get(0);
            try {
                thread.join();
                threads.remove(thread);
            } catch (InterruptedException e) {
            }
        }
    }

    public void execute(String command, String[] args) {
        in.injectCommand(command, args);
    }
}

package mineserver.thread;

import mineserver.IOHandler;
import mineserver.Server;
import mineserver.config.ServerProperties;

public class AutoSave {

    private static final long MILLISECONDS_PER_MINUTE =  60 * 1000;
    private static final long SAVE_INTERVAL = ServerProperties.getInt("save");
    private static final long INITIAL_SAVE_DELAY = ServerProperties.getInt("save-delay");
    
    private final Server server;
    private final SaveThread saveThread;
    
    private long lastSaveTime;
    
    private volatile boolean run = true;
    
    public AutoSave(Server server) {
        this.server = server;
        
        lastSaveTime = System.currentTimeMillis() + INITIAL_SAVE_DELAY * MILLISECONDS_PER_MINUTE;
        
        saveThread = new SaveThread();
        saveThread.start();
        saveThread.setName("AutoSave");
    }
    
    public void stop() {
        run = false;
        saveThread.interrupt();
    }
    
    private boolean shouldSave() {
        long elapsedTime = System.currentTimeMillis();
        if (elapsedTime - SAVE_INTERVAL * MILLISECONDS_PER_MINUTE > lastSaveTime) {
            return true;
        }
        return false;
    }
    
    private void announceSave() {
        IOHandler.println("Saving map...");
        server.announce("Saving map...");
    }
    
    private class SaveThread extends Thread {
        
        @Override
        public void run() {
            while (run) {
                if (shouldSave()) {
                    try {
                        server.saveLock.acquire();
                    } catch (InterruptedException e) {
                        continue;
                    }

                    announceSave();
                    
                    server.setSaving(true);
                    server.runCommand("save-all", "");
                    while (server.isSaving()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
    
                    server.saveLock.release();
                    lastSaveTime = System.currentTimeMillis();
                }
                
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}

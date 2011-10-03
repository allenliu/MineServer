package mineserver.thread;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import mineserver.IOHandler;
import mineserver.Server;
import mineserver.config.ServerProperties;
import mineserver.util.ZipUtil;

import org.apache.commons.io.FileUtils;

public class AutoBackup {

    private static final long MILLISECONDS_PER_MINUTE =  60 * 1000;
    private static final long BACKUP_INTERVAL = ServerProperties.getInt("backup");
    
    private static final File BACKUP_PATH = new File("backups");
    private static final String NAME_FORMAT = "%tF-%1$tH-%1$tM";

    private final Server server;
    private final BackupThread backupThread;

    private long lastSaveTime;

    private volatile boolean forceBackup = false;
    private volatile boolean run = true;

    public AutoBackup(Server server) {
        this.server = server;

        lastSaveTime = System.currentTimeMillis();

        backupThread = new BackupThread();
        backupThread.start();
        backupThread.setName("AutoBackup");
    }

    public void stop() {
        run = false;
        backupThread.interrupt();
    }

    public void forceBackup() {
        forceBackup = true;
        backupThread.interrupt();
    }
    
    private boolean shouldBackup() {
        if (forceBackup) {
            forceBackup = false;
            return true;
        }
        long elapsedTime = System.currentTimeMillis();
        if (elapsedTime - BACKUP_INTERVAL * MILLISECONDS_PER_MINUTE > lastSaveTime) {
            return true;
        }
        return false;
    }

    private void backup() throws IOException {
        IOHandler.println("Backing up server...");
        server.announce("Backing up server...");
        
        server.runCommand("save-off", "");
        File backup = new File(BACKUP_PATH, String.format(NAME_FORMAT, new Date()));
        FileUtils.copyDirectory(new File(Server.SERVER_PATH + "/" + "world"), backup);
        server.runCommand("save-on", "");
        
        ZipUtil.zip(backup, new File(BACKUP_PATH + "/" + backup.getName() + ".zip"));
        FileUtils.deleteDirectory(backup);
        
        IOHandler.println("Backup complete.");
        server.announce("Backup complete.");
    }

    private void announceSave() {
        IOHandler.println("Saving map...");
        server.announce("Saving map...");
    }

    private class BackupThread extends Thread {

        @Override
        public void run() {
            while (run) {
                if (shouldBackup()) {
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


                    try {
                        backup();
                    } catch (IOException e) {
                        IOHandler.println("Server backup failure!");
                        e.printStackTrace();
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

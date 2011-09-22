package mineserver;

import java.text.SimpleDateFormat;
import java.util.Date;

import mineserver.command.Command;

public class IOHandler {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
    
    Server server;
    Client client;

    private boolean loaded = false;

    public IOHandler(Server server, Client client){
        this.server= server;
        this.client = client;
    }

    public synchronized void waitUntilLoaded() throws InterruptedException {
        while (!loaded) {
            wait();
        }
    }

    public static void println(Object arg) {
        System.out.println(DATE_FORMAT.format(new Date()) + " [MineServer] " + arg);
    }

    public static void print(Object arg) {
        System.out.print(DATE_FORMAT.format(new Date()) + " [MineServer] " + arg);
    }
    
    public static void printf(String format, Object... args) {
        System.out.printf(DATE_FORMAT.format(new Date()) + " [MineServer] " + format, args);
    }
    
    public boolean parseCommand(String line) {
        CommandFactory commandFactory = server.getCommandFactory();
        Command command = commandFactory.getCommand(line);
        if (command != commandFactory.getInvalidCommand()) {
            command.execute(client, line);
            return true;
        } else {
            return false;
        }
    }

    public void handleOutput(String line) {
        if (line.contains("Preparing start region for level 0")) {
            println("Preparing start region for level 0.");
            return;
        } else if (line.contains("Preparing start region for level 1")) {
            println("=================================== [Done]");
            println("Preparing start region for level 1.");
            return;
        } else if (line.contains("Preparing spawn area:")) {
            int progress = Integer.parseInt(line.substring(line.lastIndexOf(" ") + 1, line.lastIndexOf("%")));
            StringBuilder out = new StringBuilder();
            for (int i = 0; i < 35 * progress / 100; i++) {
                out.append("=");
            }
            print(out.toString() + "\r");
            return;
        } else if (line.contains("[INFO] Done (")) {
            println("=================================== [Done]");
            println("Server is ready.");
            synchronized (this) {
                loaded = true;
                notifyAll();
            }
            return;
        } else if (line.contains("[INFO] CONSOLE: Save complete.") || line.contains("[INFO] Save complete.")) {
            server.setSaving(false);
            println("Save complete.");
            server.announce("Save complete.");
            return;
        } else if (line.contains("Can't keep up!")) {
            // filter useless messages
            return;
        } else if (line.contains("New max size:")) {
            return;
        } else if (line.equals("161 recipes") || line.equals("17 achievements")) {
            return;
        }

        System.out.println(line);
    }
}

package mineserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import mineserver.config.PermissionConfig;
import mineserver.config.ServerProperties;
import mineserver.event.ConfuseEvent;
import mineserver.event.RainEvent;
import mineserver.event.ThunderboltEvent;
import mineserver.minecraft.MinecraftWrapper;
import mineserver.resource.ChestList;
import mineserver.resource.MuteList;
import mineserver.resource.Resource;
import mineserver.thread.AutoBackup;
import mineserver.thread.AutoSave;
import mineserver.thread.SystemInputQueue;

public class Server {

    // directories
    public static final String SERVER_PATH = "server";
    
    private final Listener listener;

    private ServerSocket socket;

    public static final LocalAddressFactory addressFactory = new LocalAddressFactory();
    
    private MinecraftWrapper minecraft;
    private IOHandler ioHandler;
    private CommandFactory commandFactory;
    private ClientList clientList;

    // resources
    private List<Resource> resources;
    private PermissionConfig permissions;
    private MuteList muteList;
    private ChestList chestList;

    // threads    
    private SystemInputQueue systemInput;
    private AutoSave autoSave;
    private AutoBackup autoBackup;

    private volatile boolean run = true;

    // in game
    private boolean raining = false;
    private boolean saving = false;

    
    public Semaphore saveLock = new Semaphore(1);
    
    public Server() {
        listener = new Listener();
        listener.start();
        listener.setName("ServerListener");
    }

    private void init() {
        resources = new LinkedList<Resource>();
        resources.add(permissions = new PermissionConfig());
        resources.add(muteList = new MuteList());
        resources.add(chestList = new ChestList());

        Client dummyClient = new ServerClient(this);

        ioHandler = new IOHandler(this, dummyClient);
        commandFactory = new CommandFactory(this, permissions);

        clientList = new ClientList();

        systemInput = new SystemInputQueue();
    }

    private void startup() {
        minecraft = new MinecraftWrapper(systemInput, ioHandler);
        minecraft.downloadServerJar();
        try {
            minecraft.start();
        } catch (InterruptedException e) {
        }
        
        autoSave = new AutoSave(this);
        autoBackup = new AutoBackup(this);
    }

    public void stop() {
        run = false;
        try {
            socket.close();
        } catch (IOException e) {
        }

        listener.interrupt();
    }

    private void shutdown() {

        saving = false;
        if (!saveLock.tryAcquire()) {
            IOHandler.println("Server is currently backing up/saving...");
            while (true) {
                try {
                    saveLock.acquire();
                    break;
                } catch (InterruptedException e) {
                }
            }
        }

        kickAll();
        autoSave.stop();
        autoBackup.stop();
        saveResources();
        minecraft.stop();
    }

    private void cleanup() {
        System.exit(0);
    }

    public void runCommand(String command, String[] args) {
        minecraft.execute(command, args);
    }

    private void kickAll() {
        for (PlayerClient client : getClients()) {
            client.kick("Server shutting down.");
        }
    }

    private void saveResources() {
        for (Resource resource : resources) {
            resource.save();
        }
    }

    public void setSaving(boolean saving) {
        this.saving = saving;
    }
    
    public boolean isSaving() {
        return saving;
    }
    
    private final class Listener extends Thread {
        @Override
        public void run() {
            init();
            startup();

            int port = 25565;
            String ip = "0.0.0.0";

            InetAddress address;
            if (ip.equals("0.0.0.0")) {
                address = null;
            } else {
                try {
                    address = InetAddress.getByName(ip);
                } catch (UnknownHostException e) {
                    IOHandler.println(e);
                    IOHandler.println("Invalid listening address " + ip + "!");
                    return;
                }
            }

            try {
                socket = new ServerSocket(port, 0, address);
            } catch (IOException e) {
                IOHandler.println(e);
                IOHandler.println("Could not listen on port " + port + "!");
                IOHandler.println("Is it already in use? Exiting application...");
                return;
            }

            IOHandler.println("Wrapper listening on "
                    + socket.getInetAddress().getHostAddress() + ":"
                    + socket.getLocalPort() + " (connect here)");
            if (socket.getInetAddress().getHostAddress().equals("0.0.0.0")) {
                IOHandler.println("Note: 0.0.0.0 means all"
                        + " IP addresses; you want this.");
            }

            try {
                while (run) {
                    Socket client;
                    try {
                        client = socket.accept();
                    } catch (IOException e) {
                        if (run) {
                            IOHandler.println(e);
                            IOHandler.println("Accept failed on port " + port + "!");
                        }
                        break;
                    }
                    new PlayerClient(Server.this, client);                   
                }
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
            shutdown();
            cleanup();
        }

    }

    public void announce(String message) {
        runCommand("say", new String[] {message});
    }
    
    public void addClient(PlayerClient client) {
        clientList.add(client);
    }

    public void removeClient(PlayerClient client) {
        clientList.remove(client);
    }

    public boolean hasClient(String clientName) {
        return clientList.contains(clientName);
    }

    public boolean hasClient(int eid) {
        return clientList.contains(eid);
    }

    public PlayerClient getClient(String clientName) {
        return clientList.get(clientName);
    }

    public PlayerClient getClient(int eid) {
        return clientList.get(eid);
    }

    public Collection<PlayerClient> getClients() {
        return clientList.getAll();
    }

    public PlayerClient getTarget(Client client, String targetName) {
        if (hasClient(targetName)) {
            return getClient(targetName);
        }
        client.warning(targetName + " does not exist.");
        return null;
    }

    public LocalAddressFactory getAddressFactory() {
        return addressFactory;
    }

    public IOHandler getIOHandler() {
        return ioHandler;
    }

    public CommandFactory getCommandFactory() {
        return commandFactory;
    }

    public PermissionConfig getPermissionConfig() {
        return permissions;
    }

    public MuteList getMuteList() {
        return muteList;
    }

    public ChestList getChestList() {
        return chestList;
    }

    public void smite(PlayerClient target) {
        for (PlayerClient client : getClients()) {
            client.addEvent(new ThunderboltEvent(target));
        }
    }

    public void confuse(PlayerClient target) {
        target.addEvent(new ConfuseEvent(target));
    }

    public void toggleRain() {
        raining = !raining;
        for (PlayerClient client : getClients()) {
            client.addEvent(new RainEvent(raining));
        }
    }

    public boolean isRaining() {
        return raining;
    }
}

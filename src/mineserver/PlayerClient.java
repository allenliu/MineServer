package mineserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import mineserver.command.Command;
import mineserver.event.DestroyEntityEvent;
import mineserver.event.Event;
import mineserver.event.RainEvent;
import mineserver.event.RemoveEffectEvent;
import mineserver.event.SpawnEntityEvent;
import mineserver.event.SpeedEvent;
import mineserver.stream.StreamTunnel;

public class PlayerClient implements Client {

    private final Server server;
    private IOHandler inputHandler;

    private final Socket external;
    private Socket internal;

    private StreamTunnel serverToClient;
    private StreamTunnel clientToServer;
    private Watchdog watchdog;    

    private String name;
    private int eid;

    private Position position = new Position();

    private boolean invincible = false;
    private boolean vanished = false;
    
    private ChestAction chestAction = ChestAction.NONE;
    private Location chestPlaced;
    private Location chestOpened;

    private boolean closed = false;
    private boolean loggedIn = false;
    private boolean kicked = false;
    private String kickReason = "";

    private Queue<String> messages = new ConcurrentLinkedQueue<String>();
    private Queue<Event> events = new ConcurrentLinkedQueue<Event>();

    public PlayerClient(Server server, Socket client) {
        this.server = server;
        //this.inputHandler = server.getInputHandler();
        this.external = client;

        System.out.println("[MineServer] IP Connection from " + getIPAddress() + "!");

        try {
            InetAddress localAddress = InetAddress.getByName(server.getAddressFactory().getNextAddress());
            internal = new Socket(InetAddress.getByName(null), 25566, localAddress, 0);
        } catch (Exception e1) {
            e1.printStackTrace();
            cleanup();
            return;
        }

        watchdog = new Watchdog();
        try {
            serverToClient = new StreamTunnel(this, internal.getInputStream(), external.getOutputStream(), true);
            clientToServer = new StreamTunnel(this, external.getInputStream(), internal.getOutputStream(), false);
        } catch (IOException e) {
            e.printStackTrace();
            cleanup();
            return;
        }
        watchdog.start();
    }

    public void close() {
        if (serverToClient != null) {
            serverToClient.stop();
        }
        if (clientToServer != null) {
            clientToServer.stop();
        }
        
        if (getServer().hasClient(getEntityId())) {
            getServer().removeClient(this);
        }
    }

    private void cleanup() {
        if (!closed) {
            closed = true;

            close();

            try {
                external.close();
            } catch (Exception e) {
            }
            try {
                internal.close();
            } catch (Exception e) {
            }

            System.out.println("[MineServer] Socket Closed: " + external.getInetAddress().getHostAddress());
        }
    }

    public boolean parseCommand(String line) {
        line = line.trim();
        if (line.length() > 0 && line.startsWith("/")) {
            line = line.substring(1);
            CommandFactory commandFactory = server.getCommandFactory();
            Command command = commandFactory.getCommand(line);
            if (command != commandFactory.getInvalidCommand()) {
                command.execute(this, line);
                return true;
            } else {
                warning("That command does not exist.");
                return false;
            }
        }
        return false;
    }

    public Server getServer() {
        return server;
    }

    public String getIPAddress() {
        return external.getInetAddress().getHostAddress();
    }

    public InetAddress getInetAddress() {
        return external.getInetAddress();
    }

    public void loggedIn() {
        loggedIn = true;
        events.add(new RainEvent(getServer().isRaining()));
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public boolean isKicked() {
        return kicked;
    }

    public String getKickReason() {
        return kickReason;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEntityId(int eid) {
        this.eid = eid;
        server.addClient(this);
    }

    public int getEntityId() {
        return eid;
    }

    public Position getPosition() {
        return position;
    }

    public void info(String message) {
        addMessage(Color.GREEN + message);
    }

    public void usage(String message) {
        addMessage(Color.DARK_CYAN + "Usage: " + message);
    }

    public void warning(String message) {
        addMessage(Color.RED + message);
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public boolean hasMessages() {
        return !messages.isEmpty();
    }

    public String getMessage() {
        return messages.remove();
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public boolean hasEvents() {
        return !events.isEmpty();
    }

    public Event getEvent() {
        return events.remove();
    }

    public void kick(String reason) {
        kickReason = reason;
        kicked = true;

        serverToClient.stop();
        clientToServer.stop();
    }

    public void mute() {
        server.getMuteList().mute(this);
    }

    public void unmute() {
        server.getMuteList().unmute(this);
    }

    public boolean isMuted() {
        return server.getMuteList().isMuted(this);
    }

    public void speed() {
        events.add(new SpeedEvent(this));
    }

    public void unspeed() {
        events.add(new RemoveEffectEvent(this, Effect.MOVESPEED));
    }

    public void teleport(Client destination) {
        server.runCommand("tp", new String[] {getName(), destination.getName()});
    }

    public boolean isGod() {
        return invincible;
    }

    public void god() {
        invincible = true;
    }

    public void ungod() {
        invincible = false;
    }

    public boolean isVanished() {
        return vanished;
    }

    public void vanish() {
        vanished = true;
        for (PlayerClient client : getServer().getClients()) {
            if (client != this) {
                client.addEvent(new DestroyEntityEvent(this));
            }
        }
    }

    public void unvanish() {
        vanished = false;
        for (PlayerClient client : getServer().getClients()) {
            if (client != this) {
                client.addEvent(new SpawnEntityEvent(this));
            }
        }
    }

    public void toggleLocking() {
        if (chestAction == ChestAction.LOCK) {
            chestAction = ChestAction.NONE;
        } else {
            chestAction = ChestAction.LOCK;
        }
    }

    public boolean isLocking() {
        return chestAction == ChestAction.LOCK;
    }

    public void toggleUnlocking() {
        if (chestAction == ChestAction.UNLOCK) {
            chestAction = ChestAction.NONE;
        } else {
            chestAction = ChestAction.UNLOCK;
        }
    }

    public boolean isUnlocking() {
        return chestAction == ChestAction.UNLOCK;
    }

    private void placingChest(Location location) {
        chestPlaced = location;
    }

    public boolean placedChest(Location location) {
        return chestPlaced != null && chestPlaced.equals(location);
    }

    public void openingChest(Location location) {
        chestOpened = location;
    }

    public Location openedChest() {
        return chestOpened;
    }

    public boolean lockChest(Location location) {
        boolean success = getServer().getChestList().addLocked(getName(), location);
        if (success) {
            chestPlaced = null;
            info("This chest is now locked.");
        } else {
            warning("There is an adjacent locked chest.");
        }
        return success;
    }
    
    public boolean canPlaceChest(Location location) {
        boolean success = getServer().getChestList().addUnlocked(getName(), location);
        if (success) {
            chestPlaced = location;
        } else {
            warning("There is an adjacent locked chest.");
        }
        return success;
    }
    
    public void unlockChest(Location location) {
        getServer().getChestList().remove(location);
        chestAction = ChestAction.NONE;
        info("This chest is now unlocked");
    }
    
    public enum ChestAction {
        NONE, LOCK, UNLOCK;
    }
    
    public class Position {

        private double x;
        private double y;
        private double z;

        public Position() {

        }

        public void update(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double x() {
            return x;
        }

        public double y() {
            return x;
        }

        public double z() {
            return x;
        }
    }

    private final class Watchdog extends Thread {
        @Override
        public void run() {
            while (serverToClient.isAlive() || clientToServer.isAlive()) {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
            }

            cleanup();
        }
    }
}
package mineserver.stream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mineserver.IOHandler;
import mineserver.Location;
import mineserver.PlayerClient;
import mineserver.resource.ChestList;

public class StreamTunnel {

    private static final Pattern MESSAGE_PATTERN = Pattern.compile("^<([^>]+)> (.*)$");
    private static final Pattern COLOR_PATTERN = Pattern.compile("\u00a7[0-9a-f]");
    private static final Pattern JOIN_PATTERN = Pattern.compile("\u00a7.((\\d|\\w)*) (joined|left) the game.");
    private static final String CONSOLE_CHAT_PATTERN = "\\(CONSOLE:.*\\)";
    private static final int MESSAGE_SIZE = 60;
    private static final int MAXIMUM_MESSAGE_SIZE = 119;
    
    private static final int BUFFER_SIZE = 1024;
    private final byte[] buffer = new byte[BUFFER_SIZE];
    
    private final PlayerClient client;
    
    private DataInputStream in;
    private DataOutputStream out;

    private final Tunneler tunneler;
    private boolean isServerTunnel;
    
    private volatile boolean run = true;
    
    public StreamTunnel(PlayerClient client, InputStream in, OutputStream out, boolean isServerTunnel) {
        this.client = client;
        this.in = new DataInputStream(new BufferedInputStream(in));
        this.out = new DataOutputStream(new BufferedOutputStream(out));

        this.isServerTunnel = isServerTunnel;

        tunneler = new Tunneler();
        tunneler.start();
    }

    public void stop() {
        run = false;
    }
    
    public boolean isAlive() {
        return tunneler.isAlive();
    }
    
    private void handlePacket() throws IOException {
        int eid;
        int x, xPos;
        byte y, yPos;
        int z, zPos;
        byte direction;
        short blockId;
        byte blockCount = 0;
        short blockDamage = 0;
        byte blockType;
        byte windowId;
        byte windowType;
        String windowName;
        byte windowSlots;
        
        ChestList chestList;
        Location location;
        boolean success;
        boolean drop;
        
        byte packetId = in.readByte();
        switch (packetId) {
        case 0x00: // Keep Alive
            write(packetId);
            write(in.readInt());
            break;
        case 0x01: // Login Request
            write(packetId);
            if (isServerTunnel) {
                eid = in.readInt();
                client.setEntityId(eid);
                write(eid);
                write(readUTF16());
                write(in.readLong());
                write(in.readInt());
                write(in.readByte());
                write(in.readByte());
                write(in.readByte());
                write(in.readByte());
            } else {
                write(in.readInt());
                String name = readUTF16();
                tunneler.setName(isServerTunnel ? "server" : "client" + "Tunneler-" + name);
                client.setName(name);         
                write(name);
                write(in.readLong()); 
                write(in.readInt());
                write(in.readByte());
                write(in.readByte());
                write(in.readByte());
                write(in.readByte());
            }
            break;
        case 0x02: // Handshake
            write(packetId);
            write(readUTF16());
            break;
        case 0x03: // Chat Message            
            String message = readUTF16();
            if (isServerTunnel) {
                Matcher colorMatcher = COLOR_PATTERN.matcher(message);
                String cleanMessage = colorMatcher.replaceAll("");
                Matcher messageMatcher = MESSAGE_PATTERN.matcher(cleanMessage);
                
                if (cleanMessage.matches(CONSOLE_CHAT_PATTERN)) {
                    break;
                }
                
            } else {
                if (client.isMuted()) {
                    client.warning("You are muted.");
                    break;
                }
                if (client.parseCommand(message)) {
                    break;
                }
            }
            write(packetId);
            write(message);
            break;
        case 0x04: // Time Update
            write(packetId);
            write(in.readLong());
            break;
        case 0x05: // Entity Equipment
            write(packetId);
            write(in.readInt());
            write(in.readShort());
            write(in.readShort());
            write(in.readShort());
            break;
        case 0x06: // Spawn Position
            write(packetId);
            write(in.readInt());
            write(in.readInt());
            write(in.readInt());
            break;
        case 0x07: // Use Entity
            int user = in.readInt();
            int targetEid = in.readInt();
            PlayerClient target = client.getServer().getClient(targetEid);
            if (target != null) {
              if (target.isGod()) {
                in.readBoolean();
                break;
              }
            }
            write(packetId);
            write(user);
            write(targetEid);
            write(in.readBoolean());
            break;
        case 0x08: // Update Health
            write(packetId);
            write(in.readShort());
            write(in.readShort());
            write(in.readFloat());
            break;
        case 0x09: // Respawn
            write(packetId);
            write(in.readByte());
            write(in.readByte());
            write(in.readByte());
            write(in.readShort());
            write(in.readLong());
            break;
        case 0x0a: // Player
            write(packetId);
            write(in.readBoolean());
            break;
        case 0x0b: // Player Position
            write(packetId);
            copyPlayerLocation();
            readWriteBytes(1);
            break;
        case 0x0c: // Player Look
            write(packetId);
            copyPlayerLook();
            readWriteBytes(1);
            break;
        case 0x0d: // Player Position and Look
            write(packetId);
            copyPlayerLocation();
            copyPlayerLook();
            readWriteBytes(1);
            if (!client.isLoggedIn()) {
                client.loggedIn();
            }
            break;
        case 0x0e: // Player Digging
            byte status = in.readByte();
            x = in.readInt();
            y = in.readByte();
            z = in.readInt();
            direction = in.readByte();
            
            chestList = client.getServer().getChestList();
            location = new Location(x, y, z);
            if (chestList.contains(location)) {
                if (chestList.getOwner(location).equals(client.getName())) {
                    if (client.getGamemode() == 0 && status == 2) {
                        chestList.remove(location);
                    } else if (client.getGamemode() == 1 && status == 0) {
                        chestList.remove(location);
                    }
                } else {
                    if (status == 0) {
                        client.warning("This chest is locked by an enchantment.");
                    }
                    break;
                }
            }
            
            write(packetId);
            write(status);
            write(x);
            write(y);
            write(z);
            write(direction);
            break;
        case 0x0f: // Player Block Placement
            x = in.readInt();
            y = in.readByte();
            z = in.readInt();
            direction = in.readByte();
            xPos = x;
            yPos = y;
            zPos = z;
            switch (direction) {
              case 0:
                --yPos;
                break;
              case 1:
                ++yPos;
                break;
              case 2:
                --zPos;
                break;
              case 3:
                ++zPos;
                break;
              case 4:
                --xPos;
                break;
              case 5:
                ++xPos;
                break;
            }
            blockId = in.readShort();
            boolean place = false;
            if (blockId >= 0) {
                place = true;
                blockCount = in.readByte();
                blockDamage = in.readShort();
            }
            
            boolean remove = false;
            if (blockId == 54) {
                location = new Location(xPos, yPos, zPos);
                remove = !client.canPlaceChest(location);
            }
            
            write(packetId);
            write(x);
            write(y);
            write(z);
            write(direction);
            write(blockId);
            if (place) {
                write(blockCount);
                write(blockDamage);
            }
            client.openingChest(location = new Location(x, y, z));
            if (remove) {
                // knock it back out to prevent client-side visual bug
                write((byte) 0x0e);
                write((byte) 0x00);
                write(xPos);
                write(yPos);
                write(zPos);
                write(direction);
                write((byte) 0x0e);
                write((byte) 0x02);
                write(xPos);
                write(yPos);
                write(zPos);
                write(direction);
            }
            break;
        case 0x10: // Holding Change
            write(packetId);
            readWriteBytes(2);
            break;
        case 0x11: // Use Bed
            write(packetId);
            readWriteBytes(14);
            break;
        case 0x12: // Animation
            write(packetId);
            readWriteBytes(5);
            break;
        case 0x13: // Entity Action
            write(packetId);
            readWriteBytes(5);
            break;
        case 0x14: // Named Entity Spawn
            eid = in.readInt();
            String entityName = readUTF16();
            if (client.getServer().hasClient(entityName) && client.getServer().getClient(entityName).isVanished()) {
                skipBytes(16);
                break;
            }
            write(packetId);
            write(eid);
            write(entityName);
            readWriteBytes(16);
            break;
        case 0x15: // Pickup Spawn
            write(packetId);
            readWriteBytes(24);
            break;
        case 0x16: // Collect Item
            // hide item collection by vanished players
            int itemId = in.readInt();
            eid = in.readInt();
            if (isServerTunnel && client.getServer().hasClient(eid) && client.getServer().getClient(eid).isVanished()) {
                break;
            }
            write(packetId);
            write(itemId);
            write(eid);
            break;
        case 0x17: // Add Object/Vehicle
            write(packetId);
            write(in.readInt());
            write(in.readByte());
            write(in.readInt());
            write(in.readInt());
            write(in.readInt());
            int flag = in.readInt();
            write(flag);
            if (flag > 0) {
                write(in.readShort());
                write(in.readShort());
                write(in.readShort());
            }
            break;
        case 0x18: // Mob Spawn
            write(packetId);
            write(in.readInt());
            write(in.readByte());
            write(in.readInt());
            write(in.readInt());
            write(in.readInt());
            write(in.readByte());
            write(in.readByte());
            copyUnknownBlob();
            break;
        case 0x19: // Painting
            write(packetId);
            write(in.readInt());
            write(readUTF16());
            write(in.readInt());
            write(in.readInt());
            write(in.readInt());
            write(in.readInt());
            break;
        case 0x1a: // Experience Orb
            write(packetId);
            readWriteBytes(18);
            break;
        case 0x1b: // Stance Update (No longer used?)
            write(packetId);
            readWriteBytes(18);
            break;
        case 0x1c: // Entity Velocity
            write(packetId);
            readWriteBytes(10);
            break;
        case 0x1d: // Destroy Entity
            write(packetId);
            readWriteBytes(4);
            break;
        case 0x1e: // Entity
            eid = in.readInt();
            if (client.getServer().hasClient(eid) && client.getServer().getClient(eid).isVanished()) {
                break;
            }
            write(packetId);
            write(eid);
            break;
        case 0x1f: // Entity Relative Move
            eid = in.readInt();
            if (client.getServer().hasClient(eid) && client.getServer().getClient(eid).isVanished()) {
                skipBytes(3);
                break;
            }
            write(packetId);
            write(eid);
            readWriteBytes(3);
            break;
        case 0x20: // Entity Look
            eid = in.readInt();
            if (client.getServer().hasClient(eid) && client.getServer().getClient(eid).isVanished()) {
                skipBytes(2);
                break;
            }
            write(packetId);
            write(eid);
            readWriteBytes(2);
            break;
        case 0x21: // Entity Look and Relative Move
            write(packetId);
            readWriteBytes(9);
            break;
        case 0x22: // Entity Teleport
            write(packetId);
            readWriteBytes(18);
            break;
        case 0x26: // Entity Status
            write(packetId);
            readWriteBytes(5);
            break;
        case 0x27: // Attach Entity
            write(packetId);
            write(in.readInt());
            readWriteBytes(4);
            break;
        case 0x28: // Entity Metadata
            write(packetId);
            write(in.readInt());
            copyUnknownBlob();
            break;
        case 0x29: // Entity Effect
            write(packetId);
            write(in.readInt());
            write(in.readByte());
            write(in.readByte());
            write(in.readShort());
            break;
        case 0x2a: // Remove Entity Effect
            write(packetId);
            write(in.readInt());
            write(in.readByte());
            break;
        case 0x2b: // Experience
            write(packetId);
            write(in.readByte());
            write(in.readByte());
            write(in.readShort());
            break;
        case 0x32: // Pre-Chunk
            write(packetId);
            readWriteBytes(9);
            break;
        case 0x33: // Map Chunk
            write(packetId);
            readWriteBytes(13);
            int chunkSize = in.readInt();
            write(chunkSize);
            readWriteBytes(chunkSize);
            break;
        case 0x34: // Multi-Block Change
            write(packetId);
            readWriteBytes(8);
            short arraySize = in.readShort();
            write(arraySize);
            readWriteBytes(arraySize * 4);
            break;
        case 0x35: // Block Change
            x = in.readInt();
            y = in.readByte();
            z = in.readInt();
            blockType = in.readByte();
            byte metadata = in.readByte();

            success = true;
            if (blockType == 54) {
                chestList = client.getServer().getChestList();
                location = new Location(x, y, z);
                if (client.placedChest(location)) {
                    if (client.isLocking()) {
                        success = client.lockChest(location);
                    }
                }
            }
            
            write(packetId);
            write(x);
            write(y);
            write(z);
            write(blockType);
            write(metadata);
            break;
        case 0x36: // Block Action
            write(packetId);
            readWriteBytes(12);
            break;
        case 0x3c: // Explosion
            write(packetId);
            readWriteBytes(28);
            int recordCount = in.readInt();
            write(recordCount);
            readWriteBytes(recordCount * 3);
            break;
        case 0x3d: // Sound Effect
            write(packetId);
            write(in.readInt());
            write(in.readInt());
            write(in.readByte());
            write(in.readInt());
            write(in.readInt());
            break;
        case 0x46: // New/Invalid State
            write(packetId);
            byte state = in.readByte();
            byte gamemode = in.readByte();
            if (state == 1) {
                client.getServer().setRain(true);
            }
            if (state == 2) {
                client.getServer().setRain(false);
            }
            if (state == 3) {
                client.setGamemode(gamemode);
            }
            write(state);
            write(gamemode);
            break;
        case 0x47: // Thunder
            write(packetId);
            readWriteBytes(17);
            break;
        case 0x64: // Open Window
            boolean open = true;
            windowId = in.readByte();
            windowType = in.readByte();
            windowName = readUTF16();
            windowSlots = in.readByte();
            
            if (windowType == (byte) 0x00) {
                chestList = client.getServer().getChestList();
                location = client.openedChest();
                if (chestList.contains(location)) {
                    if (chestList.getOwner(location).equals(client.getName())) {
                        if (client.isUnlocking()) {
                            client.unlockChest(location);
                        } else {
                            windowName = client.getName() + "'s Chest";
                        }
                    } else {
                        open = false;
                        client.warning("This chest is locked by an enchantment.");
                    }
                } else {
                    if (client.isLocking()) {
                        client.lockChest(location);
                        windowName = client.getName() + "'s Chest";
                    } else if (client.isUnlocking()) {
                        client.warning("This chest was not locked to begin with.");
                    }
                }
            }
            
            if (open) {
                write(packetId);
                write(windowId);
                write(windowType);
                write(windowName);
                write(windowSlots);
            } else {
                write((byte) 0x65);
                write(windowId);
            }
            break;
        case 0x65: // Close Window
            write(packetId);
            write(in.readByte());
            break;
        case 0x66: // Window Click
            byte typeFrom = in.readByte();
            short slotFrom = in.readShort();
            byte typeTo = in.readByte();
            short slotTo = in.readShort();

            write(packetId);
            write(typeFrom);
            write(slotFrom);
            write(typeTo);
            write(slotTo);
            write(in.readBoolean());
            short moveItem = in.readShort();
            write(moveItem);
            if (moveItem != -1) {
                write(in.readByte());
                write(in.readShort());
            }
            break;
        case 0x67: // Set Slot
            byte type67 = in.readByte();
            short slot = in.readShort();
            short setItem = in.readShort();
            write(packetId);
            write(type67);
            write(slot);
            write(setItem);
            if (setItem != -1) {
                write(in.readByte());
                write(in.readShort());
            }
            break;
        case 0x68: // Window Items
            write(packetId);
            windowType = in.readByte();
            write(windowType);
            short count = in.readShort();
            write(count);
            for (int c = 0; c < count; ++c) {
                short item = in.readShort();
                write(item);

                if (item != -1) {
                    write(in.readByte());
                    write(in.readShort());
                }
            }
            break;
        case 0x69: // Update Progress Bar
            write(packetId);
            write(in.readByte());
            write(in.readShort());
            write(in.readShort());
            break;
        case 0x6a: // Transaction
            write(packetId);
            write(in.readByte());
            write(in.readShort());
            write(in.readBoolean());
            break;
        case 0x6b: // Creative Inventory Action
            write(packetId);
            write(in.readShort());
            write(in.readShort());
            write(in.readShort());
            write(in.readShort());
            break;
        case (byte) 0x82: // Update Sign
            write(packetId);
            write(in.readInt());
            write(in.readShort());
            write(in.readInt());
            write(readUTF16());
            write(readUTF16());
            write(readUTF16());
            write(readUTF16());
            break;
        case (byte) 0x83: // Map data
            write(packetId);
            write(in.readShort());
            write(in.readShort());
            byte length = in.readByte();
            write(length);
            readWriteBytes(0xff & length);
            break;
        case (byte) 0xc8: // Statistic
            write(packetId);
            readWriteBytes(5);
            break;
        case (byte) 0xc9: // Player List Item
            write(packetId);
            write(readUTF16());
            write(in.readBoolean());
            write(in.readShort());
            break;
        case (byte) 0xfe: // Server List Ping
            write(packetId);
            break;
        case (byte) 0xff: // Disconnect/Kick
            write(packetId);
            write(readUTF16());
            client.close();
            break;
        }
    }

    private void kick(String reason) throws IOException {
        write((byte) 0xff);
        write(reason);
      }
    
    private void skipBytes(int numbytes) throws IOException {
        int overflow = numbytes / buffer.length;
        for (int c = 0; c < overflow; ++c) {
          in.readFully(buffer, 0, buffer.length);
        }
        in.readFully(buffer, 0, numbytes % buffer.length);
      }
    
    private void readWriteBytes(int numBytes) throws IOException {
        int overflow = numBytes / buffer.length;
        for (int c = 0; c < overflow; ++c) {
            in.readFully(buffer, 0, buffer.length);
            out.write(buffer, 0, buffer.length);
        }
        in.readFully(buffer, 0, numBytes % buffer.length);
        out.write(buffer, 0, numBytes % buffer.length);
    }

    private void copyUnknownBlob() throws IOException {
        byte unknown = in.readByte();
        write(unknown);

        while (unknown != 0x7f) {
            int type = (unknown & 0xE0) >> 5;
    
            switch (type) {
            case 0:
                write(in.readByte());
                break;
            case 1:
                write(in.readShort());
                break;
            case 2:
                write(in.readInt());
                break;
            case 3:
                write(in.readFloat());
                break;
            case 4:
                write(readUTF16());
                break;
            case 5:
                write(in.readShort());
                write(in.readByte());
                write(in.readShort());
                break;
            case 6:
                write(in.readInt());
                write(in.readInt());
                write(in.readInt());
            }

            unknown = in.readByte();
            write(unknown);
        }
    }
    
    private void skipUnknownBlob() throws IOException {
        byte unknown = in.readByte();

        while (unknown != 0x7f) {
            int type = (unknown & 0xE0) >> 5;
    
            switch (type) {
            case 0:
                in.readByte();
                break;
            case 1:
                in.readShort();
                break;
            case 2:
                in.readInt();
                break;
            case 3:
                in.readFloat();
                break;
            case 4:
                readUTF16();
                break;
            case 5:
                in.readShort();
                in.readByte();
                in.readShort();
                break;
            case 6:
                in.readInt();
                in.readInt();
                in.readInt();
            }

            unknown = in.readByte();
        }
    }
    
    private void copyPlayerLocation() throws IOException {
        double x = in.readDouble();
        double y = in.readDouble();
        double stance = in.readDouble();
        double z = in.readDouble();
        client.getPosition().update(x, y, z);
        write(x);
        write(y);
        write(stance);
        write(z);
    }

    private void copyPlayerLook() throws IOException {
        float yaw = in.readFloat();
        float pitch = in.readFloat();
        client.getPosition().updateLook(yaw, pitch);
        write(yaw);
        write(pitch);
    }
    
    private String readUTF16() throws IOException {
        short length = in.readShort();
        byte[] bytes = new byte[length * 2 + 2];
        in.readFully(bytes, 2, length * 2);
        bytes[0] = (byte) 0xfffffffe;
        bytes[1] = (byte) 0xffffffff;
        return new String(bytes, "UTF-16");
    }

    private byte write(byte b) throws IOException {
        out.writeByte(b);
        return b;
    }

    private boolean write(boolean v) throws IOException {
        out.writeBoolean(v);
        return v;
    }

    private short write(short v) throws IOException {
        out.writeShort(v);
        return v;
    }
    
    private int write(int v) throws IOException {
        out.writeInt(v);
        return v;
    }

    private float write(float v) throws IOException {
        out.writeFloat(v);
        return v;
    }

    private long write(long v) throws IOException {
        out.writeLong(v);
        return v;
    }
    
    private double write(double v) throws IOException {
        out.writeDouble(v);
        return v;
    }
    
    private String write(String s) throws IOException {
        write((short) s.length());
        out.writeChars(s);
        return s;
    }

    private String getLastColorCode(String message) {
        String colorCode = "";
        int lastIndex = message.lastIndexOf('\u00a7');
        if (lastIndex != -1 && lastIndex + 1 < message.length()) {
            colorCode = message.substring(lastIndex, lastIndex + 2);
        }

        return colorCode;
    }
    
    private void sendMessage(String message) throws IOException {
        if (message.length() > 0) {
            if (message.length() > MESSAGE_SIZE) {
                int end = MESSAGE_SIZE - 1;
                while (end > 0 && message.charAt(end) != ' ') {
                    end--;
                }
                if (end == 0) {
                    end = MESSAGE_SIZE;
                } else {
                    end++;
                }

                if (end > 0 && message.charAt(end) == '\u00a7') {
                    end--;
                }

                String firstPart = message.substring(0, end);
                sendMessagePacket(firstPart);
                sendMessage(getLastColorCode(firstPart) + message.substring(end));
            } else {
                int end = message.length();
                if (message.charAt(end - 1) == '\u00a7') {
                    end--;
                }
                sendMessagePacket(message.substring(0, end));
            }
        }
    }

    private void sendMessagePacket(String message) throws IOException {
        if (message.length() > MESSAGE_SIZE) {
            IOHandler.println("Invalid message size: " + message);
            return;
        }
        if (message.length() > 0) {
            write((byte) 0x03);
            write(message);
        }
    }
    
    public class Tunneler extends Thread {

        @Override
        public void run() {
            while (run) {
                try {
                    handlePacket();
                    if (isServerTunnel && client.isLoggedIn()) {
                        while (client.hasMessages()) {
                            sendMessage(client.getMessage());
                        }
                        while (client.hasEvents()) {
                            client.getEvent().execute(out);
                        }
                    }
                    out.flush();
                } catch (IOException e) {
                    if (run) {
                        IOHandler.println("Error in thread " + tunneler.getName());
                        e.printStackTrace();
                    }
                    break;
                } catch (Exception e) {
                    IOHandler.println("Error in thread " + tunneler.getName());
                    e.printStackTrace();
                    break;
                }
            }
            try {
                if (client.isKicked()) {
                    kick(client.getKickReason());
                }
                out.flush();
            } catch (IOException e) {
            }
        }
    }
}

package mineserver;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class LocalAddressFactory {
    private static final int[] octets = { 0, 0, 1 };
    private static Boolean canCycle = null;
    private static boolean enabled = true;

    private void toggle(boolean enabled) {
        LocalAddressFactory.enabled = enabled;
    }

    public synchronized String getNextAddress() {
        if (!enabled || !canCycle()) {
            return "127.0.0.1";
        }

        if (octets[2] >= 255) {
            if (octets[1] >= 255) {
                if (octets[0] >= 255) {
                    octets[0] = 0;
                } else {
                    ++octets[0];
                }
                octets[1] = 0;
            } else {
                ++octets[1];
            }
            octets[2] = 2;
        } else {
            ++octets[2];
        }

        return "127." + octets[0] + "." + octets[1] + "." + octets[2];
    }

    private boolean canCycle() {
        if (canCycle == null) {
            InetAddress testDestination;
            InetAddress testSource;
            try {
                testDestination = InetAddress.getByName(null);
                testSource = InetAddress.getByName("127.0.1.2");
            } catch (UnknownHostException e) {
                canCycle = false;
                return false;
            }

            try {
                Socket testSocket = new Socket(testDestination, 80, testSource, 0);
                testSocket.close();
            } catch (BindException e) {
                canCycle = false;
                return false;
            } catch (IOException e) {
                // Probably nothing listening on port 80
            }

            canCycle = true;
        }

        return canCycle;
    }
}
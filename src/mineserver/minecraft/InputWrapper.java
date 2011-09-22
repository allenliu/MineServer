package mineserver.minecraft;

import java.io.IOException;
import java.io.OutputStream;

import mineserver.IOHandler;
import mineserver.thread.SystemInputQueue;
import mineserver.utils.StringUtil;

public class InputWrapper extends Thread {

    private final SystemInputQueue in;
    private final OutputStream out;
    private final IOHandler ioHandler;
    
    private volatile boolean run = true;

    public InputWrapper(SystemInputQueue in, OutputStream out, IOHandler ioHandler) {
        this.in = in;
        this.out = out;
        this.ioHandler = ioHandler;
    }

    public void injectCommand(String command, String[] args) {
        String line;
        if (args == null) {
            line = String.format("%s\n", command);
        } else {
            line = String.format("%s %s\n", command, StringUtil.join(args, " "));
        }
        try {
            out.write(line.getBytes());
            out.flush();
        } catch (IOException e) {
            //messageHandler.handleError(e);
        }
    }

    @Override
    public void interrupt() {
        run = false;
        super.interrupt();
    }
    
    @Override
    public void run() {
        try {
            while (run) {

                String line;
                try {
                    line = in.nextLine();
                } catch (InterruptedException e1) {
                    continue;
                }

                if (ioHandler.parseCommand(line)) {
                    continue;
                }

                line += "\n";
                try {
                    out.write(line.getBytes());
                    out.flush();
                } catch (IOException e) {
                    System.err.println("error in inputwrapper");
                    //messageHandler.handleError(e);
                    break;
                }
            }
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
    }
}

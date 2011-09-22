package mineserver.minecraft;

import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

import mineserver.IOHandler;

public class OutputWrapper extends Thread {

    private final Scanner scanner;
    private final IOHandler ioHandler;
    
    private volatile boolean run = true;
    
    public OutputWrapper(InputStream in, IOHandler ioHandler) {
        scanner = new Scanner(in);
        this.ioHandler = ioHandler;
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
                    line = scanner.nextLine();
                } catch (NoSuchElementException e) {
                    //messageHandler.handleError(e);
                    break;
                } catch (IllegalStateException e) {
                    break;
                }
                ioHandler.handleOutput(line);
            }
        } finally {
            try {
                scanner.close();
            } catch (IllegalStateException e) {
            }
        }
    }
}

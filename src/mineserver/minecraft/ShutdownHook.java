package mineserver.minecraft;

public class ShutdownHook extends Thread {

    private final MinecraftWrapper minecraft;

    public ShutdownHook(MinecraftWrapper minecraft) {
        this.minecraft = minecraft;

        Runtime.getRuntime().addShutdownHook(this);
    }

    @Override
    public void run() {
        System.out.println("Shutdown Hook Activated");
        minecraft.execute("stop", new String[] {});

    }

}

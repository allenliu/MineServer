package mineserver;

public class Main {

    private static final String LICENSE = "MineServer -- Copyright (C) 2011 MineServer authors\nThis program is licensed under The MIT License.\nSee file LICENSE for details.";
    public static final String VERSION = "1.0";
    
    public static void main(String[] args) {
        System.out.println(LICENSE);
	    System.out.println("Starting MineServer " + VERSION);
        new Server();
	}

}

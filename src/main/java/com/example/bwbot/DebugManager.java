package com.example.bwbot;

public class DebugManager {
    private static final boolean debug = true;

    public static void print(String out) {
        if(debug)
            System.out.println(out);
    }
}

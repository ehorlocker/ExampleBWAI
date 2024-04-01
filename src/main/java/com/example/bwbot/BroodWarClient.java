package com.example.bwbot;

import bwapi.*;
import bwem.BWEM;
import bwta.BWTA;
import jbweb.Blocks;
import jbweb.JBWEB;

public class BroodWarClient extends DefaultBWListener {
    private static BroodWarClient instance;
    private BWClient bwClient;
    private Game game;
    private BWEM bwem;
    //TODO: init bwta
    private BWTA bwta;
    private Player player;

    public void init() {
        if (instance == null) {
            instance = new BroodWarClient();
        }

        bwClient = new BWClient(this);
        bwClient.startGame();
    }

    @Override
    public void onStart() {
        game = bwClient.getGame();

        bwem = new BWEM(game);
        bwem.initialize();
        player = game.self();

        JBWEB.onStart(game, bwem);
        Blocks.findBlocks();
        GameManager.getInstance();
    }

    public static BWClient getBwClient() {
        return getInstance().bwClient;
    }

    public static BWEM getBwem() {
        return getInstance().bwem;
    }

    public static Game getGame() {
        return getInstance().game;
    }

    public static BWTA getBwta() {
        return getInstance().bwta;
    }

    public static Player getPlayer() {
        return getInstance().player;
    }

    public static BroodWarClient getInstance() {
        if (instance == null) {
            instance = new BroodWarClient();
        }
        return instance;
    }
}

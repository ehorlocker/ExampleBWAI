package com.example.bwbot;

import bwapi.*;
import bwem.BWEM;
import bwta.BWTA;
import jbweb.Blocks;
import jbweb.JBWEB;

import java.util.ArrayList;

public class BroodWarClient extends DefaultBWListener {
    private static BroodWarClient instance;
    private BWClient bwClient;
    private Game game;
    private BWEM bwem;
    //TODO: init bwta
    private BWTA bwta;
    private Player player;

    // Having to do this here really sucks, but we can only listen to BW events from where we start the game
    // so we need to abstract it so we can listen in other places.
    private ArrayList<BroodWarEventListener> broodWarEventListeners = new ArrayList<BroodWarEventListener>();

    public void init() {
        if (instance == null) {
            instance = new BroodWarClient();
        }

        bwClient = new BWClient(this);
        bwClient.startGame();
    }

    public void addListener(BroodWarEventListener broodWarEventListener) {
        broodWarEventListeners.add(broodWarEventListener);
    }

    public void removeListener(BroodWarEventListener broodWarEventListener) {
        broodWarEventListeners.remove(broodWarEventListener);
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

    @Override
    public void onStart() {
        game = bwClient.getGame();

        bwem = new BWEM(game);
        bwem.initialize();
        player = game.self();

        JBWEB.onStart(game, bwem);
        Blocks.findBlocks();
        //FIXME: this sucks
        GameManager.getInstance();

        for (BroodWarEventListener broodWarEventListener: broodWarEventListeners) {
            broodWarEventListener.onStart();
        }
    }

    @Override
    public void onEnd(boolean isWinner) {
        for (BroodWarEventListener broodWarEventListener: broodWarEventListeners) {
            broodWarEventListener.onEnd(isWinner);
        }
    }

    @Override
    public void onFrame() {
        for (BroodWarEventListener broodWarEventListener: broodWarEventListeners) {
            broodWarEventListener.onFrame();
        }
    }

    @Override
    public void onSendText(String text) {
        for (BroodWarEventListener broodWarEventListener: broodWarEventListeners) {
            broodWarEventListener.onSendText(text);
        }
    }

    @Override
    public void onReceiveText(Player player, String text) {
        for (BroodWarEventListener broodWarEventListener: broodWarEventListeners) {
            broodWarEventListener.onReceiveText(player, text);
        }
    }

    @Override
    public void onPlayerLeft(Player player) {
        for (BroodWarEventListener broodWarEventListener: broodWarEventListeners) {
            broodWarEventListener.onPlayerLeft(player);
        }
    }

    @Override
    public void onNukeDetect(Position position) {
        for (BroodWarEventListener broodWarEventListener: broodWarEventListeners) {
            broodWarEventListener.onNukeDetect(position);
        }
    }

    @Override
    public void onUnitDiscover(Unit unit) {
        for (BroodWarEventListener broodWarEventListener: broodWarEventListeners) {
            broodWarEventListener.onUnitDiscover(unit) ;
        }
    }

    @Override
    public void onUnitEvade(Unit unit) {
        for (BroodWarEventListener broodWarEventListener: broodWarEventListeners) {
            broodWarEventListener.onUnitEvade(unit);
        }
    }

    @Override
    public void onUnitShow(Unit unit) {
        for (BroodWarEventListener broodWarEventListener: broodWarEventListeners) {
            broodWarEventListener.onUnitShow(unit);
        }
    }

    @Override
    public void onUnitHide(Unit unit) {
        for (BroodWarEventListener broodWarEventListener: broodWarEventListeners) {
            broodWarEventListener.onUnitHide(unit);
        }
    }

    @Override
    public void onUnitCreate(Unit unit) {
        for (BroodWarEventListener broodWarEventListener: broodWarEventListeners) {
            broodWarEventListener.onUnitCreate(unit);
        }
    }

    @Override
    public void onUnitDestroy(Unit unit) {
        for (BroodWarEventListener broodWarEventListener: broodWarEventListeners) {
            broodWarEventListener.onUnitDestroy(unit);
        }
    }

    @Override
    public void onUnitMorph(Unit unit) {
        for (BroodWarEventListener broodWarEventListener: broodWarEventListeners) {
            broodWarEventListener.onUnitMorph(unit);
        }
    }

    @Override
    public void onUnitRenegade(Unit unit) {
        for (BroodWarEventListener broodWarEventListener: broodWarEventListeners) {
            broodWarEventListener.onUnitRenegade(unit);
        }
    }

    @Override
    public void onSaveGame(String text) {
        for (BroodWarEventListener broodWarEventListener: broodWarEventListeners) {
            broodWarEventListener.onSaveGame(text);
        }
    }

    @Override
    public void onUnitComplete(Unit unit) {
        for (BroodWarEventListener broodWarEventListener: broodWarEventListeners) {
            broodWarEventListener.onUnitComplete(unit);
        }
    }

    @Override
    public void onPlayerDropped(Player player) {
        for (BroodWarEventListener broodWarEventListener: broodWarEventListeners) {
            broodWarEventListener.onPlayerDropped(player);
        }
    }
}

package com.example.bwbot;

import bwapi.*;
import bwem.Base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//TODO: Switch to log4j
public class Debug extends BroodWarEventListener {
    int ONE_TILE = TilePosition.SIZE_IN_PIXELS;
    private static final boolean debug = true;
    private static final Logger logger = LogManager.getLogger("debug");

    public Debug() {
        BroodWarClient.getInstance().addListener(this);
    }

    public static void print(String out) {
        if(debug) {
            logger.info(out);
        }
    }

    @Override
    public void onFrame() {
        if (debug) {
            drawBases();
        }
    }

    private void drawBases() {
        Game game = BroodWarClient.getGame();
        if (game == null) {
            return;
        }

        BroodWarClient.getGame().drawTextScreen(100, 100, "");

        //TODO: Refactor
        for (final OccupiedBase occupiedBase : GameManager.getInstance().getBaseList()) {
            Base base = occupiedBase.getBase();
            game.drawBoxMap(
                    base.getLocation().toPosition(),
                    base.getLocation().toPosition().add(new Position(128, 96)),
                    Color.Blue);
            game.drawTextMap(
                    base.getCenter().getX() - 40,
                    base.getCenter().getY() - ONE_TILE * 2,
                    ("SCV Count: " + occupiedBase.getWorkerCount() + "/16"),
                    Text.White);
        }
    }
}

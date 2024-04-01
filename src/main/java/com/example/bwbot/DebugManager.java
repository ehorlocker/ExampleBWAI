package com.example.bwbot;

import bwapi.*;
import bwem.Base;

//TODO: Switch to log4j
public class DebugManager extends DefaultBWListener {
    int ONE_TILE = TilePosition.SIZE_IN_PIXELS;
    private static final boolean debug = true;

    public static void print(String out) {
        if(debug) {
            System.out.println(out);
        }
    }

    @Override
    public void onFrame() {
        drawBases();
    }

    private void drawBases() {
        Game game = BroodWarClient.getGame();
        if (game == null) {
            return;
        }

        BroodWarClient.getGame().drawTextScreen(100, 100, "");

        GameManager.getInstance().update();

        //TODO: Refactor
        for (final BaseInfo baseInfo : GameManager.getInstance().getBaseList()) {
            Base base = baseInfo.getBase();
            game.drawBoxMap(
                    base.getLocation().toPosition(),
                    base.getLocation().toPosition().add(new Position(128, 96)),
                    Color.Blue);
            game.drawTextMap(
                    base.getCenter().getX() - 40,
                    base.getCenter().getY() - ONE_TILE * 2,
                    ("SCV Count: " + baseInfo.getWorkerCount() + "/16"),
                    Text.White);
        }
    }
}

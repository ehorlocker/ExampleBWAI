package com.example.bwbot;

import bwapi.BWClient;
import bwapi.Game;
import bwapi.Unit;
import bwem.Base;

import java.util.List;

public class ExampleUtils {
    public static Base getClosestBase(Unit unit) {
        Base closestBase = null;
        int closestDistance = Integer.MAX_VALUE;
        for (Base base : ExampleBot.bwem.getMap().getBases()) {
            int distance = base.getCenter().getApproxDistance(unit.getPosition());
            if (distance < closestDistance) {
                closestBase = base;
                closestDistance = distance;
            }
        }
        return closestBase;
    }

}

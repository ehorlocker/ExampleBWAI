package com.example.bwbot;

import bwapi.Unit;
import bwem.BWEM;
import bwem.Base;

public class ExampleUtils {
    public static Base getClosestBase(Unit unit) {
        Base closestBase = null;
        int closestDistance = Integer.MAX_VALUE;
        BWEM bwem = BroodWarClient.getBwem();
        if (bwem != null) {
            for (Base base : bwem.getMap().getBases()) {
                int distance = base.getCenter().getApproxDistance(unit.getPosition());
                if (distance < closestDistance) {
                    closestBase = base;
                    closestDistance = distance;
                }
            }
        }
        return closestBase;
    }

}

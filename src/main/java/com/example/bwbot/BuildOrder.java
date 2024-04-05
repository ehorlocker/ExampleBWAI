package com.example.bwbot;

import bwapi.UnitType;

public class BuildOrder {
    private UnitType unitToBuild;
    private int supplyNeeded;

    BuildOrder() {

    }

    BuildOrder(int supplyNeeded, String unitToBuild) {
        this.supplyNeeded = supplyNeeded;
        this.unitToBuild = UnitType.valueOf(unitToBuild);
    }

    public boolean isBuildOrderStep(int supplyUsed) {
        return (supplyUsed >= (supplyNeeded * 2));
    }

    public UnitType getUnitToBuild() {
        return unitToBuild;
    }

    public int getSupplyNeeded() {
        return supplyNeeded;
    }

    public String toString() {
        String result =
                "\n-----------------" +
                "\nSupply Needed: " + supplyNeeded +
                "\nUnit to Build: " + unitToBuild +
                "\n-----------------";
        return result;
    }
}

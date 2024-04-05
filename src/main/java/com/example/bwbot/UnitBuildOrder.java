package com.example.bwbot;

import bwapi.UnitType;

public class UnitBuildOrder {
    private UnitType unitToBuild;
    private int supplyNeeded;
    private int numberToBuild;

    UnitBuildOrder() {

    }

    UnitBuildOrder(int supplyNeeded, String unitToBuild, int numberToBuild) {
        this.supplyNeeded = supplyNeeded;
        this.unitToBuild = UnitType.valueOf(unitToBuild);
        this.numberToBuild = numberToBuild;
    }

    public boolean isBuildOrderStep(int supplyUsed) {
        return (supplyUsed == (supplyNeeded * 2));
    }

    public UnitType getUnitToBuild() {
        return unitToBuild;
    }

    public int getSupplyNeeded() {
        return supplyNeeded;
    }

    public int getNumberToBuild() {
        return numberToBuild;
    }

    public String toString() {
        String result =
                "-----------------" +
                "\nSupply Needed: " + supplyNeeded +
                "\nUnit to Build: " + unitToBuild +
                "\n-----------------";
        return result;
    }
}

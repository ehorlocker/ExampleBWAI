package com.example.bwbot;

import bwapi.UnitType;

public class BuildOrder {
    BuildOrder() {

    }
    BuildOrder(int supplyNeeded, String unitToBuild) {
        this.supplyNeeded = supplyNeeded;
        this.unitToBuild = UnitType.valueOf(unitToBuild);
    }
    private int supplyNeeded;
    private UnitType unitToBuild;
    public boolean isBuildOrderStep(int supplyUsed) { return (supplyUsed == (supplyNeeded * 2));}

    public void setSupplyNeeded(int supplyNeeded) {
        this.supplyNeeded = supplyNeeded;
    }
    public void setUnitToBuild(UnitType unitToBuild) {
        this.unitToBuild = unitToBuild;
    }

    public UnitType getUnitToBuild() {
        return unitToBuild;
    }
    public int getSupplyNeeded() {
        return supplyNeeded;
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

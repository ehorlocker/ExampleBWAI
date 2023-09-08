package com.example.bwbot;

import bwapi.Unit;

import java.util.ArrayList;

public class ResourceCenter {
    Unit resourceCenter;
    ArrayList<Unit> scvArray = new ArrayList<Unit>();

    public ResourceCenter(Unit unit) {
        resourceCenter = unit;
    }
    public Unit getUnit() {
        return resourceCenter;
    }

    public ArrayList<Unit> getScvArray() {
        return scvArray;
    }

    public void addScv (Unit scv) {
        scvArray.add(scv);
    }
}

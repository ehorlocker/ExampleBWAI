package com.example.bwbot;

import bwapi.Unit;
import bwapi.UnitCommand;
import bwem.Base;

import java.util.LinkedList;
import java.util.Queue;

public class Worker extends ExampleUnit {

    Unit currentBuilding = null;

    private Base currentBase;

    //commandQueue is now handled by commanandManager
    public Worker(Unit _worker) {
        super(_worker);
        currentBase = ExampleUtils.getClosestBase(_worker);
    }

    public Base getCurrentBase() {
        return currentBase;
    }

    public void setCurrentBase(Base _base) {
        currentBase = _base;
    }

    //maybe make a mining function

}

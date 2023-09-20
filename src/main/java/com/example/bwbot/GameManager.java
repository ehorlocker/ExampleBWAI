package com.example.bwbot;

import bwapi.*;
import bwem.BWEM;
import bwem.BWMap;
import bwem.Base;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.runtime.Debug;

import java.io.File;
import java.io.IOException;
import java.sql.Array;
import java.util.*;

public class GameManager {
    private static GameManager instance;
    private static List<ExampleUnit> unitList;
    private static List<BaseInfo> playerBaseList;
    private static List<Worker> workerList;
    private static List<ExampleUnit> raxList;
    private static StrategyManager strategyManager;


    private static Queue<BuildOrder> buildOrderQueue = new LinkedList<BuildOrder>();

    private static List<BaseInfo> expansionBaseList = new LinkedList<BaseInfo>();

    private static Race race;

    public static final int PRIORITY_ONE = 1;
    public static final int PRIORITY_TWO = 2;
    public static final int PRIORITY_THREE = 3;
    public static final int PRIORITY_FOUR = 4;
    public static final int PRIORITY_FIVE = 5;

    private GameManager() {
        //ame = _game;
        //self = game.self();

        unitList = new ArrayList<ExampleUnit>();
        playerBaseList = new ArrayList<BaseInfo>();
        workerList = new ArrayList<Worker>();
        raxList = new ArrayList<ExampleUnit>();
        race = ExampleBot.game.self().getRace();
        strategyManager = new StrategyManager();

        // we do this separate so the command center is correctly added
        // to the unit list in order
        for(Unit startingUnit : ExampleBot.game.self().getUnits()) {
            if(startingUnit.getType().isBuilding()) {
                addUnitToUnitList(startingUnit);
                DebugManager.print("Adding " + startingUnit.getType() + " to UnitList...");
            }
        }
        for(Unit startingUnit : ExampleBot.game.self().getUnits()) {
            if(!startingUnit.getType().isBuilding()) {
                addUnitToUnitList(startingUnit);
                DebugManager.print("Adding " + startingUnit.getType() + " to UnitList...");
            }
        }
        // we need to make a list of expansions to move to so we begin by adding
        // all the bases on the map to a list, so we can order it.
        for(Base base : ExampleBot.bwem.getMap().getBases()) {
            // check to see if the base is already in the list (we dont want to add the first one)
            if(!expansionBaseList.contains(new BaseInfo(base, null))) {
                expansionBaseList.add(new BaseInfo(base, null));
            }
        }

        try {
            readBuildOrder();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /*public static void initialize() {
        if(instance == null)
            instance = new GameManager();
    }*/

    public void update() {
        for(ExampleUnit exampleUnit : unitList) {
            exampleUnit.update();
        }
        for(BaseInfo baseInfo: playerBaseList) {
            baseInfo.getResourceDepot().update();
        }
        for(ExampleUnit rax: raxList) {
            rax.addCommand(new ExampleUnitCommand(UnitCommand.train(rax.getUnit(), UnitType.Terran_Marine), PRIORITY_ONE));
        }
        strategyManager.update();
    }

    public void addUnitToUnitList(Unit unit) {
        // we have resource depot check first b/c starting workers need a base to attach to
        if(unit.getType() == ExampleBot.game.self().getRace().getResourceDepot()) {
            // Setup a BaseInfo for baseList. Gets Base information from
            // location of the resource depot.
            // Problem: Flying in command center to new base b/c getClosestBase
            playerBaseList.add(new BaseInfo(ExampleUtils.getClosestBase(unit), new ExampleUnit(unit)));
        }
        else if(unit.getType().isWorker()) {
            // this will get removed later, but we send workers straight to a mineral patch
            Worker worker = new Worker(unit);

            // adding command for gathering with nearest mineral can be optimized.
            // we could assign it a mineral and not allow more than 2 workers per mineral
            ExampleUnitCommand commandToAdd = new ExampleUnitCommand(UnitCommand.gather(worker.getUnit(),
                    ExampleBot.game.getClosestUnit(unit.getPosition(), UnitFilter.IsMineralField)), PRIORITY_ONE);
            worker.addCommand(commandToAdd);

            // add the new worker with the command to unitList
            unitList.add(worker);

            //add them to the worker list too
            workerList.add(worker);

            // in order to add the worker to the list of workers at a base,
            // we use a findClosestBase function to find the base it's in
            // and add it to the BaseInfo object that has that base
            Base closestBase = ExampleUtils.getClosestBase(worker.getUnit());
            if(playerBaseList.isEmpty()) {
                DebugManager.print("baseList was empty when trying to add " + worker.getUnit());
            }
            for(BaseInfo baseInfo: playerBaseList) {
                //DebugManager.print("base in baseList: " + baseInfo.getBase().toString());
                if(baseInfo.getBase() == closestBase) {
                    //DebugManager.print("adding " + worker.getUnit() + " to baseList");
                    baseInfo.addWorker(worker);
                }

            }
        }
        else if (unit.getType() == UnitType.Terran_Barracks) {
            ExampleUnit raxExampleUnit = new ExampleUnit(unit);
            raxList.add(raxExampleUnit);
            unitList.add(raxExampleUnit);
        }

    }

    public void readBuildOrder()
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        buildOrderQueue = new LinkedList<BuildOrder>(Arrays.asList(mapper.readValue(new File("bwapi-data/read/test1.json"), BuildOrder[].class)));
        System.out.println(buildOrderQueue);
    }

    public static Queue<BuildOrder> getBuildOrderQueue() {
        return buildOrderQueue;
    }

    public static List<BaseInfo> getBaseList() {
        return playerBaseList;
    }

    public static Race getRace() {
        return race;
    }

    public static List<ExampleUnit> getUnitList() {
        return unitList;
    }

    public static List<Worker> getWorkerList() {
        return workerList;
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }
}


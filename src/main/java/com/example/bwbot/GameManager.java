package com.example.bwbot;

import bwapi.*;
import bwem.BWEM;
import bwem.Base;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GameManager extends BroodWarEventListener {
    private static GameManager instance;
    private Game game;
    private StrategyManager strategyManager;
    private Player player;
    private static Race race;
    private static List<ExampleUnit> unitList;
    private static List<OccupiedBase> playerBaseList;
    private static List<Worker> workerList;
    private static List<ExampleUnit> raxList;
    private static List<OccupiedBase> expansionBaseList = new LinkedList<OccupiedBase>();
    private boolean addedInitialWorkers = false;

    private static Queue<BuildOrder> buildOrderQueue = new LinkedList<BuildOrder>();
    private static Queue<UnitBuildOrder> unitBuildOrderQueue = new LinkedList<UnitBuildOrder>();

    public static final int PRIORITY_ONE = 1;
    public static final int PRIORITY_TWO = 2;
    public static final int PRIORITY_THREE = 3;
    public static final int PRIORITY_FOUR = 4;
    public static final int PRIORITY_FIVE = 5;

    private GameManager() {
        unitList = new ArrayList<ExampleUnit>();
        playerBaseList = new ArrayList<OccupiedBase>();
        workerList = new ArrayList<Worker>();
        raxList = new ArrayList<ExampleUnit>();
        game = BroodWarClient.getGame();
        race = BroodWarClient.getPlayer().getRace();
        player = game.self();
        strategyManager = new StrategyManager();
        BroodWarClient.getInstance().addListener(this);

        // we need to make a list of expansions to move to so we begin by adding
        // all the bases on the map to a list, so we can order it.
        BWEM bwem =  BroodWarClient.getBwem();
        if (bwem != null) {
            for (Base base : bwem.getMap().getBases()) {
                // check to see if the base is already in the list (we dont want to add the first one)
                if(!expansionBaseList.contains(new OccupiedBase(base, null))) {
                    expansionBaseList.add(new OccupiedBase(base, null));
                }
            }
        }


        try {
            readBuildOrder();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            readUnitBuildOrder();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void init() {
        if(instance == null) {
            instance = new GameManager();
        }
    }

    @Override
    public void onFrame() {
        for(ExampleUnit exampleUnit : unitList) {
            exampleUnit.update();
        }
        for(OccupiedBase occupiedBase : playerBaseList) {
            occupiedBase.getResourceDepot().update();
        }
        strategyManager.update();
    }

    public void addUnitToUnitList(Unit unit) {
        // we have resource depot check first b/c starting workers need a base to attach to
        if (unit.getType() == race.getResourceDepot()) {
            // Setup a BaseInfo for baseList. Gets Base information from
            // location of the resource depot.
            // Problem: Flying in command center to new base b/c getClosestBase
            playerBaseList.add(new OccupiedBase(ExampleUtils.getClosestBase(unit), new ExampleUnit(unit)));
            Debug.print("adding base");
            if (!addedInitialWorkers) {
                addInitialWorkers();
            }
        }
        else if (unit.getType().isWorker()) {
            Debug.print("adding worker");
            Worker worker = new Worker(unit);

            strategyManager.assignWorkerToMineClosestMineral(worker);

            unitList.add(worker);
            workerList.add(worker);

            // in order to add the worker to the list of workers at a base,
            // we use a findClosestBase function to find the base it's in
            // and add it to the BaseInfo object that has that base
            Base closestBase = ExampleUtils.getClosestBase(worker.getUnit());
            if (playerBaseList.isEmpty()) {
                Debug.print("baseList was empty when trying to add " + worker.getUnit());
            }
            for(OccupiedBase occupiedBase : playerBaseList) {
                //DebugManager.print("base in baseList: " + baseInfo.getBase().toString());
                if(occupiedBase.getBase() == closestBase) {
                    //DebugManager.print("adding " + worker.getUnit() + " to baseList");
                    occupiedBase.addWorker(worker);
                }
            }
        }
        else if (unit.getType() == UnitType.Terran_Barracks) {
            Debug.print("adding rax");
            ExampleUnit raxExampleUnit = new ExampleUnit(unit);
            raxList.add(raxExampleUnit);
            unitList.add(raxExampleUnit);
        }
    }

    private void addInitialWorkers() {
            for (int i = 0; i < workerList.size(); i++) {
                if(playerBaseList.isEmpty()) {
                    Debug.print("baseList was empty");
                    return;
                }
                Worker worker = workerList.get(i);
                Base closestBase = ExampleUtils.getClosestBase(worker.getUnit());
                for(OccupiedBase occupiedBase : playerBaseList) {
                    if(occupiedBase.getBase() == closestBase) {
                        occupiedBase.addWorker(worker);
                    }
                }
            }
            addedInitialWorkers = true;
    }

    public void readBuildOrder()
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        buildOrderQueue = new LinkedList<BuildOrder>(Arrays.asList(mapper.readValue(
                new File("bwapi-data/read/building.json"), BuildOrder[].class)));
        Debug.print(buildOrderQueue.toString());
    }

    public void readUnitBuildOrder()
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        unitBuildOrderQueue = new LinkedList<UnitBuildOrder>(Arrays.asList(mapper.readValue(
                new File("bwapi-data/read/unit.json"), UnitBuildOrder[].class)));
        Debug.print(unitBuildOrderQueue.toString());
    }

    public static Queue<BuildOrder> getBuildOrderQueue() {
        return buildOrderQueue;
    }

    public static Queue<UnitBuildOrder> getUnitBuildOrderQueue() {
        return unitBuildOrderQueue;
    }

    public static List<OccupiedBase> getBaseList() {
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

    public static List<ExampleUnit> getRaxList() {
        return raxList;
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    @Override
    public void onUnitComplete(Unit unit) {
        addUnitToUnitList(unit);
    }
}


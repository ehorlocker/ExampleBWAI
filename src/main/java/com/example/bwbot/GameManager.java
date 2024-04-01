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
    private static List<ExampleUnit> unitList;
    private static List<OccupiedBase> playerBaseList;
    private static List<Worker> workerList;
    private static List<ExampleUnit> raxList;
    private static StrategyManager strategyManager;


    private static Queue<BuildOrder> buildOrderQueue = new LinkedList<BuildOrder>();

    private static List<OccupiedBase> expansionBaseList = new LinkedList<OccupiedBase>();

    private Game game;

    private static Race race;

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
        strategyManager = new StrategyManager();
        BroodWarClient.getInstance().addListener(this);

        // we do this separate so the command center is correctly added
        // to the unit list in order
        for (Unit startingUnit : game.self().getUnits()) {
            if (startingUnit.getType().isBuilding()) {
                addUnitToUnitList(startingUnit);
                Debug.print("Adding " + startingUnit.getType() + " to UnitList...");
            }
        }
        for (Unit startingUnit : game.self().getUnits()) {
            if (!startingUnit.getType().isBuilding()) {
                addUnitToUnitList(startingUnit);
                Debug.print("Adding " + startingUnit.getType() + " to UnitList...");
            }
        }
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
        for(ExampleUnit rax: raxList) {
            Game game = BroodWarClient.getGame();
            if(!rax.getUnit().isTraining() &&
                    game != null &&
                    game.canMake(UnitType.Terran_Marine)) {
                rax.addCommand(new ExampleUnitCommand(
                        UnitCommand.train(rax.getUnit(), UnitType.Terran_Marine), PRIORITY_ONE));
            }
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
        }
        else if (unit.getType().isWorker()) {
            // this will get removed later, but we send workers straight to a mineral patch
            Worker worker = new Worker(unit);

            // adding command for gathering with nearest mineral can be optimized.
            // we could assign it a mineral and not allow more than 2 workers per mineral
            ExampleUnitCommand commandToAdd = new ExampleUnitCommand(UnitCommand.gather(worker.getUnit(),
                    game.getClosestUnit(unit.getPosition(), UnitFilter.IsMineralField)), PRIORITY_ONE);
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
            ExampleUnit raxExampleUnit = new ExampleUnit(unit);
            raxList.add(raxExampleUnit);
            unitList.add(raxExampleUnit);
        }

    }

    public void readBuildOrder()
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        buildOrderQueue = new LinkedList<BuildOrder>(Arrays.asList(mapper.readValue(
                new File("bwapi-data/read/building.json"), BuildOrder[].class)));
        System.out.println(buildOrderQueue);
    }

    public static Queue<BuildOrder> getBuildOrderQueue() {
        return buildOrderQueue;
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

    public static GameManager getInstance() {
        if (instance == null) {
            Debug.print("making gamemanager");
            instance = new GameManager();
        }
        return instance;
    }

    @Override
    public void onUnitComplete(Unit unit) {
        // janky fix for units not always getting added to the unit list
        if (game.elapsedTime() > 4)
            addUnitToUnitList(unit);

        // TODO: a better way to filter buildings for GameManager
        if (unit.getType() == UnitType.Terran_Barracks) {
            addUnitToUnitList(unit);
        }
    }
}


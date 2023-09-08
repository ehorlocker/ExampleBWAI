package com.example.bwbot;

import bwapi.*;
import bwem.BWEM;
import bwem.Base;

import bwta.BWTA;
import com.fasterxml.jackson.databind.ObjectMapper;
import jbweb.Blocks;
import jbweb.JBWEB;
import sun.awt.image.ImageWatched;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

/********************************************
 *  TODO: Add toBeBuilt to StrategyManager  *
 *        It might have custom objects idk  *
 *                                          *
 *  this may get recreated at some point    *
 *  once we're at spaghetti                 *
 ********************************************/

class ExampleBot extends DefaultBWListener {


    int ONE_TILE = TilePosition.SIZE_IN_PIXELS;
    BWClient bwClient;
    static Game game;
    static BWEM bwem;
    BWTA bwta;
    static Player self;
    Race race;
    int buildOrderStep = 0;
    boolean waitForBuildOrder = false;



    //This will need to change to a priority queue
    Queue<UnitType> toBeBuilt = new LinkedList<UnitType>();
    ArrayList<Pair<UnitType, Unit>> beingBuilt = new ArrayList<Pair<UnitType, Unit>>();
    ArrayList<Unit> workersBuilding = new ArrayList<Unit>();
    ArrayList<Unit> workersExploring = new ArrayList<Unit>();
    ArrayList<Unit> raxArray = new ArrayList<Unit>();
    ArrayList<ResourceCenter> ccArray = new ArrayList<ResourceCenter>();
    ArrayList<Unit> scvArray = new ArrayList<Unit>();
    ArrayList<Unit> marineArray = new ArrayList<Unit>();


    boolean firstCC = true;

    Base latestBase;


    @Override
    public void onStart() {
        System.out.println("STARTING BOT...");
        game = bwClient.getGame();

        bwem = new BWEM(game);
        bwem.initialize();
        self = game.self();
        race = self.getRace();


        JBWEB.onStart(game, bwem);
        Blocks.findBlocks();
        GameManager.getInstance();

        /*for(Base base : bwem.getMap().getBases()) {
            System.out.println(base.getLocation() + " VS " + self.getStartLocation());
            if(base.getLocation().getApproxDistance(self.getStartLocation()) == 0) {
                System.out.println("SETTING STARTING LOCATION...");
                latestBase = base;
            }
        }

        for(Unit unit: game.getAllUnits()) {
            if(unit.getType() == UnitType.Terran_Command_Center) {
                System.out.println("ADDING CC...");
                ccArray.add(new ResourceCenter(unit));
            }
        }*/
    }

    @Override
    public void onFrame() {
        game.drawTextScreen(100, 100, "");
        game.drawTextScreen(20, 20, "SCV Count: " + scvArray.size());
        game.drawTextScreen(20, 30, "Barracks Count: " + raxArray.size());

        GameManager.getInstance().update();

        for (final BaseInfo baseInfo : GameManager.getInstance().getBaseList()) {
            Base base = baseInfo.getBase();
            game.drawBoxMap(
                    base.getLocation().toPosition(),
                    base.getLocation().toPosition().add(new Position(128, 96)),
                    Color.Blue);
            game.drawTextMap(
                    base.getCenter().getX() - 40,
                    base.getCenter().getY() - ONE_TILE * 2,
                    ("SCV Count: " + baseInfo.getWorkerCount() + "/16"),
                    Text.White);
        }


        //Blocks.draw();

        // Make SCVs
        /*for (Unit trainer : self.getUnits()) {
            if (trainer.getType() == UnitType.Terran_Command_Center)
            {
                if (game.canMake(UnitType.Terran_SCV, trainer) &&
                        !trainer.isTraining() &&
                        toBeBuilt.isEmpty() &&
                        .size() < 16) {
                    System.out.println("TRAINING SCV");
                    trainer.train(UnitType.Terran_SCV);
                }
            }
        }*/

        /*for(ResourceCenter commandCenter : ccArray) {
            if(game.canMake(UnitType.Terran_SCV, commandCenter.getUnit()) &&
                commandCenter.getScvArray().size() < 16 &&
                !commandCenter.getUnit().isTraining() &&
                toBeBuilt.isEmpty()) {
                System.out.println("TRAINING SCV");
                commandCenter.getUnit().train(UnitType.Terran_SCV);
                commandCenter.addScv(commandCenter.getUnit().getBuildUnit());
            }
        }

        if (self.supplyTotal() - self.supplyUsed() <= 2 &&
                self.supplyTotal() <= 400 &&
                !beingBuilt.contains(race.getSupplyProvider()) &&
                !toBeBuilt.contains(race.getSupplyProvider()) &&
                buildOrderStep >= 5) {
            System.out.println("ADDING SUPPLY DEPOT TO TOBEBUILT");
            toBeBuilt.add(race.getSupplyProvider());
        }

        if(self.supplyUsed() < 48)
        {
            earlyGameBuildOrderChecks();
        }

        if(!raxArray.isEmpty()) {
            for(Unit rax: raxArray) {
                if(game.canMake(UnitType.Terran_Marine, rax) && !rax.isTraining())
                    rax.train(UnitType.Terran_Marine);
            }
        }


        if(!toBeBuilt.isEmpty() && game.canMake(toBeBuilt.peek())) {
            for (ResourceCenter commandCenter : ccArray) {
                for(Unit unit : commandCenter.getScvArray()) {
                    if ((unit.isIdle() || unit.isGatheringMinerals()))
                    {
                        System.out.println("SENDING " + unit + " TO BUILD " + toBeBuilt.peek() + "...");
                        TilePosition buildLocation;
                        // set position for building
                        if(toBeBuilt.peek() == UnitType.Terran_Command_Center)  {
                            //get buildLocation of next base
                            buildLocation = getClosestBase(latestBase).getLocation();
                            //check if unexplored, then order to move
                            unit.move(buildLocation.toPosition());
                        }
                        else {
                            buildLocation = game.getBuildLocation(race.getSupplyProvider(), self.getStartLocation());
                            unit.build(toBeBuilt.peek(), buildLocation);
                            unit.gather(getClosestMineral(unit), true);
                        }

                        System.out.println("ADDING BUILDING TO BEINGBUILT");
                        beingBuilt.add(new Pair(toBeBuilt.remove(), unit));
                        break;
                    }
                }
            }
        }
        //if we have to move a unit before building, we check to see if the builder
        //is idle before issuing build command.
        if(!beingBuilt.isEmpty()) {
            for(Pair<UnitType, Unit> beingBuiltPair : beingBuilt) {
                if(beingBuiltPair.getRight().isIdle() &&
                        beingBuiltPair.getLeft() == UnitType.Terran_Command_Center) {
                    beingBuiltPair.getRight().build(beingBuiltPair.getLeft(), getClosestBase(latestBase).getLocation());
                }
            }
        }*/

    }

    @Override
    public void onUnitComplete(Unit unit) {
        if(game.elapsedTime() > 4)
            GameManager.getInstance().addUnitToUnitList(unit);
        /*Unit closestMineral = getClosestMineral(unit);

        if(unit.getType() == UnitType.Terran_Command_Center) {
            if(firstCC)
                firstCC = false;
            else
                ccArray.add(new ResourceCenter(unit));
        }
        else if(unit.getType().isWorker() && !ccArray.isEmpty()) {
            unit.gather(closestMineral);
            ccArray.get(0).addScv(unit);
        }
        else if(unit.getType().isBuilding() &&
                    unit.getType().getRace() == race &&
                    !beingBuilt.isEmpty()) {
            beingBuilt.remove(new Pair(unit.getType(), UnitType.Terran_SCV));
            if(unit.getType() == UnitType.Terran_Barracks) {
                raxArray.add(unit);
            }
        }*/

    }

    void run() {
        bwClient = new BWClient(this);
        bwClient.startGame();
    }

    public static void main(String[] args) {
        new ExampleBot().run();
    }

    public boolean isTrainingQueueFull(Unit unit) {
        return (unit.getTrainingQueueCount() >= 5);
    }

    public Unit getClosestMineral(Unit unit)
    {
        Unit closestMineral = null;
        int closestDistance = Integer.MAX_VALUE;
        for (Unit mineral : game.getMinerals()) {
            int distance = unit.getDistance(mineral);
            if (distance < closestDistance) {
                closestMineral = mineral;
                closestDistance = distance;
            }
        }
        return closestMineral;
    }

    //this might be able to get removed cause bwta has a function for this


    public Unit getClosestCommandCenter(Unit unit) {
        Unit closestCommandCenter = null;
        int closestDistance = Integer.MAX_VALUE;
        for (ResourceCenter commandCenter : ccArray) {
            int distance = unit.getDistance(commandCenter.getUnit());
            if (distance < closestDistance) {
                closestCommandCenter = commandCenter.getUnit();
                closestDistance = distance;
            }
        }
        return closestCommandCenter;
    }

    /*public void earlyGameBuildOrderChecks() {
        if(!buildOrderQueue.isEmpty() && buildOrderQueue.peek().isBuildOrderStep(self.supplyUsed())) {
            System.out.println("Build order calls for: " + buildOrderQueue.peek().getUnitToBuild());
            toBeBuilt.add(buildOrderQueue.remove().getUnitToBuild());
        }
    }*/
}
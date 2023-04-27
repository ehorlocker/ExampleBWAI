package com.example.bwbot;

import bwapi.*;
import bwem.BWEM;
import bwem.Base;

import com.fasterxml.jackson.databind.ObjectMapper;
import jbweb.Blocks;
import jbweb.JBWEB;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/********************************************
 *  TODO: CC functionality at 18 supply     *
 *      - Identify where the closest        *
 *      base is                             *
 *      - Make sure it is secure with       *
 *      existing units                      *
 *  TODO: Build order JSON creator!         *
 *  TODO: Refinery/Gas functionality        *
 *  TODO: toBeBuilt into priority queue     *
 *                                          *
 *  then we move on to combat and moving    *
 *  units around the map                    *
 *                                          *
 *  this may get recreated at some point    *
 *  once we're at spaghetti                 *
 ********************************************/

class ExampleBot extends DefaultBWListener {
    int ONE_TILE = TilePosition.SIZE_IN_PIXELS;
    BWClient bwClient;
    Game game;
    BWEM bwem;
    Player self;
    Race race;
    int buildOrderStep = 0;
    boolean waitForBuildOrder = false;



    //This will need to change to a priority queue
    Queue<UnitType> toBeBuilt = new LinkedList<UnitType>();
    ArrayList<UnitType> beingBuilt = new ArrayList<UnitType>();
    ArrayList<Unit> workersBuilding = new ArrayList<Unit>();
    ArrayList<Unit> raxArray = new ArrayList<Unit>();
    ArrayList<Unit> scvArray = new ArrayList<Unit>();
    ArrayList<Unit> marineArray = new ArrayList<Unit>();

    Queue<BuildOrder> buildOrderQueue = new LinkedList<BuildOrder>();

    public void readBuildOrder()
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        buildOrderQueue = new LinkedList<BuildOrder>(Arrays.asList(mapper.readValue(new File("bwapi-data/read/test1.json"), BuildOrder[].class)));
        //System.out.println(buildOrderQueue);
    }

    @Override
    public void onStart() {
        System.out.println("STARTING BOT...");
        game = bwClient.getGame();

        bwem = new BWEM(game);
        bwem.initialize();
        self = game.self();
        race = self.getRace();
        try {
           readBuildOrder();
        }
        catch (Exception exception) {
           exception.printStackTrace();
        }

        game = bwClient.getGame();
        bwem = new BWEM(game);
        bwem.initialize();
        bwem.getMap().assignStartingLocationsToSuitableBases();

        JBWEB.onStart(game, bwem);
        Blocks.findBlocks();
    }

    @Override
    public void onFrame() {
        game.drawTextScreen(100, 100, "");
        game.drawTextScreen(20, 20, "SCV Count: " + scvArray.size());
        game.drawTextScreen(20, 30, "Barracks Count: " + raxArray.size());



        for (final Base base : bwem.getMap().getBases()) {
            game.drawBoxMap(
                    base.getLocation().toPosition(),
                    base.getLocation().toPosition().add(new Position(128, 96)),
                    Color.Blue);
            if(base.isStartingLocation()) {
                game.drawTextMap(
                        base.getCenter().getX() - 40,
                        base.getCenter().getY() - ONE_TILE * 2,
                        ("SCV Count: " + scvArray.stream().count() + "/16"),
                        Text.White);
            }
        }

        //Blocks.draw();

        // Make SCVs
        for (Unit trainer : self.getUnits()) {
            if (trainer.getType() == UnitType.Terran_Command_Center)
            {
                if (game.canMake(UnitType.Terran_SCV, trainer) &&
                        !trainer.isTraining() &&
                        toBeBuilt.isEmpty() &&
                        scvArray.size() < 16) {
                    System.out.println("TRAINING SCV");
                    trainer.train(UnitType.Terran_SCV);
                }
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
            for (Unit unit: self.getUnits()) {
                if (unit.getType().isWorker() && (unit.isIdle() || unit.isGatheringMinerals()))
                {
                    Unit builder = unit;
                    System.out.println("SETTING " + builder + " TO BUILD " + toBeBuilt.peek() + "...");
                    // build supply depot
                    TilePosition buildLocation = game.getBuildLocation(race.getSupplyProvider(), self.getStartLocation());
                    builder.build(toBeBuilt.peek(), buildLocation);
                    builder.gather(getClosestMineral(builder), true);
                    workersBuilding.add(builder);

                    System.out.println("ADDING BUILDING TO BEINGBUILT");
                    beingBuilt.add(toBeBuilt.remove());
                    break;
                }
            }
        }

    }

    @Override
    public void onUnitComplete(Unit unit) {
        Unit closestMineral = getClosestMineral(unit);

        if(unit.getType().isWorker()) {
            unit.gather(closestMineral);
            scvArray.add(unit);
        }
        else if(unit.getType().isBuilding() &&
                    unit.getType().getRace() == race &&
                    !workersBuilding.isEmpty()) {
            Unit builder = workersBuilding.get(beingBuilt.indexOf(unit.getType()));
            workersBuilding.remove(builder);
            beingBuilt.remove(unit.getType());
            if(unit.getType() == UnitType.Terran_Barracks) {
                raxArray.add(unit);
            }
        }
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

    /*public Base getClosestBase(Base latestBase) {
        return new Base();
    }*/

    public void earlyGameBuildOrderChecks() {
        if(!buildOrderQueue.isEmpty() && buildOrderQueue.peek().isBuildOrderStep(self.supplyUsed())) {
            System.out.println("Build order calls for: " + buildOrderQueue.peek().getUnitToBuild());
            toBeBuilt.add(buildOrderQueue.remove().getUnitToBuild());
        }
    }
}
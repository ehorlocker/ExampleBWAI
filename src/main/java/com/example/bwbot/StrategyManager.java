package com.example.bwbot;

import bwapi.*;

import java.util.*;

//TODO: Remove building from beingBuilt as soon as construction is started.
public class StrategyManager extends BroodWarEventListener {

    private PriorityQueue<UnitType> toBeBuilt = new PriorityQueue<UnitType>();
    private List<UnitType> beingBuilt = new LinkedList<UnitType>();

    private Game game = BroodWarClient.getGame();
    private int mineralsAfterPlanning;

    public StrategyManager() {
        BroodWarClient.getInstance().addListener(this);
    }

    public void update() {
        if (GameManager.getBaseList().isEmpty()) {
            Debug.print("StrategyManager tried getting baseList but it is empty!");
        }
        for (OccupiedBase occupiedBase : GameManager.getBaseList()) {
            checkAndAdjustBaseSaturation(occupiedBase);
        }
        Player player = BroodWarClient.getPlayer();
        if (player != null) {
            mineralsAfterPlanning = player.minerals();
            for (UnitType building : beingBuilt) {
                mineralsAfterPlanning -= building.mineralPrice();
            }
        }
        checkBuildOrder();
        updateToBeBuilt();
    }

    public boolean checkAndAdjustBaseSaturation(OccupiedBase occupiedBase) {
        Game game = BroodWarClient.getGame();
        if (!occupiedBase.isSaturated() &&
                !occupiedBase.isTraining() &&
                game != null &&
                BroodWarClient.getGame().canMake(UnitType.Terran_SCV)) {
            ExampleUnitCommand commandToAdd = new ExampleUnitCommand(UnitCommand.train(occupiedBase.getResourceDepot().getUnit(), GameManager.getRace().getWorker()), GameManager.PRIORITY_ONE);
            occupiedBase.getResourceDepot().addCommand(commandToAdd);
            return false;
        }
        else {
            return true;
        }
    }

    public void checkBuildOrder() {
        Queue<BuildOrder> buildOrder = GameManager.getBuildOrderQueue();
        if (!buildOrder.isEmpty() &&
                buildOrder.peek().isBuildOrderStep(game.self().supplyUsed())) {
            System.out.println("Build order calls for: " + buildOrder.peek().getUnitToBuild());
            toBeBuilt.add(GameManager.getBuildOrderQueue().remove().getUnitToBuild());
        }
    }

    public void updateToBeBuilt() {
        //is there something here in toBeBuilt
        if (toBeBuilt.peek() != null &&
                mineralsAfterPlanning >= toBeBuilt.peek().mineralPrice() &&
                game.canMake(toBeBuilt.peek())) {
            //if there is, assign a worker to build it
            for (Worker worker : GameManager.getWorkerList()) {
                // if a worker is idle or gathering minerals
                if (worker.getUnit().getType() == GameManager.getRace().getWorker() &&
                        worker.getUnit().isIdle() || worker.getUnit().isGatheringMinerals()) {
                    if (toBeBuilt.peek() == null) {
                        return;
                    }
                    UnitType buildingToBeBuilt = toBeBuilt.poll();
                    Debug.print("Sending " + worker.getUnit() + " to build " + buildingToBeBuilt + "...");
                    //assign a worker to make it
                    TilePosition buildingLocation = new TilePosition(
                            game.getBuildLocation(GameManager.getRace().getSupplyProvider(),
                                    BroodWarClient.getPlayer().getStartLocation()));
                    assignWorkerToBuild(worker, buildingToBeBuilt, buildingLocation, GameManager.PRIORITY_ONE);
                    break;
                }
            }
        }
    }

    private void assignWorkerToBuild(Worker worker, UnitType buildingToBeBuilt, TilePosition buildingLocation, int priority) {
        worker.addCommand(new ExampleUnitCommand(UnitCommand.build(worker.getUnit(),
                                                            buildingLocation,
                                                            buildingToBeBuilt),
                                                            priority));
        // we need to keep track of the unit we build i.e. barracks
        beingBuilt.add(buildingToBeBuilt);
    }

    public void assignWorkerToMineClosestMineral(Worker worker) {
        // adding command for gathering with nearest mineral can be optimized.
        // we could assign it a mineral and not allow more than 2 workers per mineral
        ExampleUnitCommand commandToAdd = new ExampleUnitCommand(UnitCommand.gather(worker.getUnit(),
                game.getClosestUnit(worker.getUnit().getPosition(), UnitFilter.IsMineralField)), GameManager.PRIORITY_ONE);
        worker.addCommand(commandToAdd);
    }

    @Override
    public void onUnitComplete(Unit unit) {
        if (unit.getType().isBuilding()) {
            beingBuilt.remove(unit.getType());
        }
    }
}

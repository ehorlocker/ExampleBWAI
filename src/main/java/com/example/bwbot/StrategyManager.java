package com.example.bwbot;

import bwapi.*;

import java.util.*;

//TODO: Remove building from beingBuilt as soon as construction is started.
public class StrategyManager extends BroodWarEventListener {

    private PriorityQueue<UnitType> toBeBuilt = new PriorityQueue<UnitType>();
    private List<UnitType> beingBuilt = new LinkedList<UnitType>();

    private Queue<UnitType> raxUnitsToBuild = new LinkedList<UnitType>();
    private Queue<UnitType> workersToBuild = new LinkedList<UnitType>();

    private Game game;
    private Player player;
    private int mineralsAfterPlanning;

    public StrategyManager() {
        BroodWarClient.getInstance().addListener(this);
        game = BroodWarClient.getGame();
        player = game.self();
    }

    public void update() {
        if (GameManager.getBaseList().isEmpty()) {
            Debug.print("StrategyManager tried getting baseList but it is empty!");
        }
        Player player = BroodWarClient.getPlayer();
        if (player != null) {
            mineralsAfterPlanning = player.minerals();
            for (UnitType building : beingBuilt) {
                mineralsAfterPlanning -= building.mineralPrice();
            }
        }
        checkBuildOrders();
        updateToBeBuilt();
        updateUnitsToBeBuilt();
    }

    public void checkBuildOrders() {
        Queue<BuildOrder> buildOrder = GameManager.getBuildOrderQueue();
        if (!buildOrder.isEmpty() &&
                buildOrder.peek().isBuildOrderStep(player.supplyUsed())) {
            Debug.print("Build order calls for: " + buildOrder.peek().getUnitToBuild());
            toBeBuilt.add(buildOrder.remove().getUnitToBuild());
        }
        Queue<UnitBuildOrder> unitBuildOrder = GameManager.getUnitBuildOrderQueue();
        UnitBuildOrder unitBuildOrderToBuild = unitBuildOrder.peek();
        if (unitBuildOrderToBuild != null &&
                unitBuildOrderToBuild.isBuildOrderStep(player.supplyUsed())) {
            //make a queue for the units based on prod facility
            if (unitBuildOrderToBuild.getUnitToBuild() == UnitType.Terran_SCV) {
                for (int i = 0; i < unitBuildOrderToBuild.getNumberToBuild(); i++) {
                    workersToBuild.add(unitBuildOrderToBuild.getUnitToBuild());
                }
                unitBuildOrder.remove();
            } else if (unitBuildOrderToBuild.getUnitToBuild() == UnitType.Terran_Marine ||
                    unitBuildOrderToBuild.getUnitToBuild() == UnitType.Terran_Firebat ||
                    unitBuildOrderToBuild.getUnitToBuild() == UnitType.Terran_Medic) {
                Debug.print("Adding " + unitBuildOrderToBuild.getNumberToBuild() + "x "
                        + unitBuildOrderToBuild.getUnitToBuild() + " to raxUnitsToBuild");
                for (int i = 0; i < unitBuildOrderToBuild.getNumberToBuild(); i++) {
                    raxUnitsToBuild.add(unitBuildOrderToBuild.getUnitToBuild());
                }
                unitBuildOrder.remove();
            }
        }
    }

    public void updateToBeBuilt() {
        //is there something here in toBeBuilt and we can afford it after the next building
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

    private void updateUnitsToBeBuilt() {
        if (!raxUnitsToBuild.isEmpty()) {
            List<ExampleUnit> raxList = GameManager.getRaxList();
            if (raxList != null) {
                for (ExampleUnit rax : raxList) {
                    if(!rax.getUnit().isTraining() &&
                            game != null &&
                            raxUnitsToBuild.peek() != null &&
                            game.canMake(raxUnitsToBuild.peek())) {
                        UnitType unit = raxUnitsToBuild.poll();
                        Debug.print("Adding " + unit + " to " + rax);
                        if (unit != null) {
                            rax.addCommand(new ExampleUnitCommand(
                                    UnitCommand.train(rax.getUnit(), unit),
                                    GameManager.PRIORITY_ONE));
                        }
                    }
                }
            }
        }
        if (!workersToBuild.isEmpty()) {
            for (OccupiedBase occupiedBase : GameManager.getBaseList()) {
                if (!occupiedBase.isSaturated() &&
                        !occupiedBase.isTraining() &&
                        game != null &&
                        game.canMake(UnitType.Terran_SCV)) {
                    ExampleUnitCommand commandToAdd = new ExampleUnitCommand(
                            UnitCommand.train(occupiedBase.getResourceDepot().getUnit(),
                            GameManager.getRace().getWorker()),
                            GameManager.PRIORITY_ONE);
                    occupiedBase.getResourceDepot().addCommand(commandToAdd);
                    workersToBuild.poll();
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

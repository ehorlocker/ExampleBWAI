package com.example.bwbot;

import bwapi.*;
import bwem.Base;
import sun.security.jca.GetInstance;

import java.util.*;

public class StrategyManager {

        private PriorityQueue<UnitType> toBeBuilt = new PriorityQueue<UnitType>();
        private List<UnitType> beingBuilt = new LinkedList<UnitType>();

        public void update() {
            if(GameManager.getBaseList().isEmpty()) {
                DebugManager.print("StrategyManager tried getting baseList but it is empty!");
            }
            for (BaseInfo baseInfo : GameManager.getBaseList()) {
                checkAndAdjustBaseSaturation(baseInfo);
            }
            checkBuildOrder();
            updateToBeBuilt();
        }

        public boolean checkAndAdjustBaseSaturation(BaseInfo baseInfo) {
            if(!baseInfo.isSaturated() && !baseInfo.isTraining()) {
                ExampleUnitCommand commandToAdd = new ExampleUnitCommand(UnitCommand.train(baseInfo.getResourceDepot().getUnit(), GameManager.getRace().getWorker()), GameManager.PRIORITY_ONE);
                baseInfo.getResourceDepot().addCommand(commandToAdd);
                return false;
            }
            else {
                return true;
            }
        }

        public void checkBuildOrder() {
            Queue<BuildOrder> buildOrder = GameManager.getBuildOrderQueue();
            if(!buildOrder.isEmpty() &&
                    buildOrder.peek().isBuildOrderStep(ExampleBot.game.self().supplyUsed())) {
                System.out.println("Build order calls for: " + buildOrder.peek().getUnitToBuild());
                toBeBuilt.add(GameManager.getBuildOrderQueue().remove().getUnitToBuild());
            }
        }

        public void updateToBeBuilt() {
            //is there something here in toBeBuilt
            if(toBeBuilt.peek() != null && ExampleBot.game.canMake(toBeBuilt.peek())) {
                //if there is, assign a worker to build it
                for (Worker worker : GameManager.getWorkerList()) {
                    // if a worker is idle or gathering minerals
                    if (worker.getUnit().getType() == GameManager.getRace().getWorker() &&
                            worker.getUnit().isIdle() || worker.getUnit().isGatheringMinerals()) {
                        UnitType buildingToBeBuilt = toBeBuilt.poll();
                        // if we're building a command center
                        if(buildingToBeBuilt != null && buildingToBeBuilt == GameManager.getRace().getResourceDepot()) {
                            /*BaseInfo closestBase = null;
                            for(Base base : ExampleBot.bwem.getMap().getBases()) {
                                for(BaseInfo baseInfo : GameManager.getBaseList()) {
                                    if(!baseInfo.getBaseInfoWithBase(base) &&
                                            !baseInfo.hasResourceDepot() &&
                                            baseInfo.getBase().getCenter().getApproxDistance(worker.getCurrentBase().getCenter()) <= closestBase.getBase().getCenter().getApproxDistance(worker.getCurrentBase().getCenter())
                                    ) {
                                        closestBase = baseInfo;
                                    }
                                }

                            }
                            assignWorkerToBuild(worker, buildingToBeBuilt, closestBase.getBase().getLocation(), GameManager.PRIORITY_ONE);*/
                        }
                        else if(buildingToBeBuilt != null){
                            DebugManager.print("SENDING " + worker.getUnit() + " TO BUILD " + buildingToBeBuilt + "...");
                            //assign a worker to make it
                            TilePosition buildingLocation = new TilePosition(ExampleBot.game.getBuildLocation(GameManager.getRace().getSupplyProvider(), ExampleBot.self.getStartLocation()));
                            assignWorkerToBuild(worker, buildingToBeBuilt, buildingLocation, GameManager.PRIORITY_ONE);
                        }
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

}

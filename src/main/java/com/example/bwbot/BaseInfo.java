package com.example.bwbot;

import bwapi.Unit;
import bwem.Base;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class BaseInfo{
    Base base;
    ExampleUnit resourceDepot; //needs to be building object eventually
    ArrayList<Worker> workersAtBase = new ArrayList<Worker>();

    BaseInfo(Base base, ExampleUnit resourceDepot) {
        this.base = base;
        this.resourceDepot = resourceDepot;
    }

    public void addWorker(Worker worker) {
        workersAtBase.add(worker);
    }

    public boolean isSaturated() {
        if(workersAtBase.size() < 16)
            return false;
        else
            return true;
    }

    public boolean isTraining() {
        return resourceDepot.getUnit().isTraining();
    }

    public boolean hasResourceDepot() {
        if(resourceDepot != null) {
            return true;
        }
        return false;
    }

    public void setResourceDepot(ExampleUnit resourceDepot) {
        this.resourceDepot = resourceDepot;
    }

    public ExampleUnit getResourceDepot() {
        return resourceDepot;
    }

    public int getWorkerCount() {
        return workersAtBase.size();
    }

    public Base getBase() {
        return base;
    }

    public boolean getBaseInfoWithBase(Base base) {
        return getBase() == base;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;

        if(o.getClass() != this.getClass())
            return false;

        return (((BaseInfo) o).getBase() == this.base);
    }
}

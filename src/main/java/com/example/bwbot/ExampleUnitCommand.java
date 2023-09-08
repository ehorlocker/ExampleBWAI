package com.example.bwbot;

import bwapi.UnitCommand;

public class ExampleUnitCommand implements Comparable<ExampleUnitCommand> {

    private UnitCommand unitCommand;

    private Integer priority;

    ExampleUnitCommand(UnitCommand unitCommand, int priority) {
        this.unitCommand = unitCommand;
        this.priority = priority;
    }

    public UnitCommand getUnitCommand() {
        return unitCommand;
    }

    public Integer getPriority() {
        return priority;
    }

    @Override
    public int compareTo(ExampleUnitCommand exampleUnitCommand) {
        return priority.compareTo(exampleUnitCommand.getPriority());
    }
}

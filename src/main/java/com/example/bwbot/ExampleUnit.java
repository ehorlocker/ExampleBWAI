package com.example.bwbot;

import bwapi.Unit;

//cannot be an is-a relationship because we do not have access to the default constructor
public class ExampleUnit {
    private Unit unit;

    private CommandManager commandManager;

    public ExampleUnit(Unit unit) {
        this.unit = unit;
        commandManager = new CommandManager();
    }

    public void update() {
        if (commandManager.hasCommands()) {
            Debug.print(unit.getType() + " - " + unit.toString() + " is going to do things");
            unit.issueCommand(commandManager.popCommand().getUnitCommand());
        }
    }

    public void addCommand(ExampleUnitCommand exampleUnitCommand) {
        commandManager.addCommand(exampleUnitCommand);
    }

    public Unit getUnit() { return unit; }
    public CommandManager getCommandManager () { return commandManager; }

    public void setUnit(Unit _unit) {
        unit = _unit;
    }
}

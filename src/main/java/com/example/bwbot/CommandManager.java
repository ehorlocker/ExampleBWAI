package com.example.bwbot;

import bwapi.Pair;
import bwapi.UnitCommand;

import java.util.Comparator;
import java.util.PriorityQueue;


/*  we need a way to update commandManager with versatility
*   functionality we need:
*       -interrupt commands for micro and interrupting mining
*       -a queue for next commands
*       -a way to update commandManager through this class
*   we need commandManager to be able to handle micro decisions like kiting
*   as well as simple build and mine command queues. this makes it not as simple
*   as simple checking to see if the unit is idle.
*
*   A good exercise is to think of how the structure will handle the
*   following situations:
*       -A worker mining minerals is ordered to build a building
*        and expected to return to mining afterward.
*       -A vulture is moving around the map and spots a melee unit.
*        It immediately stops moving and begins kiting the unit. There
*        is an attack on the base while it is kiting, and it needs to
*        go back to base to defend.
*       -A group of marines see a unit that they decide they can kill.
*        they stim and attack it.
*
*
*
*   this opens up questions as to where we handle decision-making as well.
*   if decisionds are decided elsewhere how do we update each unit's commandManager.
*
*   a quick idea is "profiles" for micro. i.e. cancel current move command with
*   vulture and begin kiting. this could be handled by something like a micro manager.
*/

public class CommandManager {
    PriorityQueue<ExampleUnitCommand> commandQueue = new PriorityQueue<ExampleUnitCommand>();
    UnitCommand currentCommand;

    public CommandManager() { currentCommand = null; }
    //add command
    public void addCommand(ExampleUnitCommand exampleUnitCommand) {
        commandQueue.add(exampleUnitCommand);
    }

    public boolean hasCommands() {
        return !commandQueue.isEmpty();
    }

    public ExampleUnitCommand popCommand() {
        if(hasCommands()) {
            return commandQueue.poll();
        }
        return null;
    }
    //update
}

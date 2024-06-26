
# ExampleBWAI

This project is a StarCraft: Brood War bot written in Java (using [JBWAPI](https://github.com/JavaBWAPI/JBWAPI)) made to compete against other AI opponents.


## Installation

Follow the [guide on SSCAIT's website](https://www.sscaitournament.com/index.php?action=tutorial) to get the Java environment running. When setting `race` in `BWAPI.ini` set the race to `Terran`. I also recommend setting `speed_override` to `20` so the game runs much faster. This is the game speed of the tournament series that SSCAIT runs and will not interfere with the functionality of the bot.
## Features

This is the beginning of a standard bot, but ExampleBWAI supports build orders provided in the form of a `.json` in `/bwapi-data/read`. The build orders must be formatted like so:
```
[
  {
    "supplyNeeded":9,
    "unitToBuild":"Terran_Supply_Depot"
  },
  {
    "supplyNeeded":11,
    "unitToBuild":"Terran_Barracks"
  },
]
```
The first argument, `supplyNeeded` takes in an int that should match the supply taken up when the building is to be built.

The second argument, `unitToBuild` is the `UnitType` to be built. See `UnitType` in the [JBWAPI documentation](https://javabwapi.github.io/JBWAPI/) for the list of units that can be made. Note: this should only be units of the race your bot is playing.

In the example above, a `Terran_Supply_Depot` will be made when the player reaches 9 supply and a `Terran_Barracks` will be built when the player reaches 11 supply.

## Demo

The bot currently follows the build order established by building.json, and will eventually follow Flash's 2 Rax Academy Opening that can be found in [this spreadsheet](https://docs.google.com/spreadsheets/d/1m6nU6FewJBC2LGQX_DPuo4PqzxH8hF3bazp8T6QlqRs/edit#gid=0). It currently only follows ~1/3 of the build with a lot more room for optimization. Unfortunately, GitHub only allows 10MB video files, so the demo can be found [here](https://youtu.be/8ID-fIMceKA).


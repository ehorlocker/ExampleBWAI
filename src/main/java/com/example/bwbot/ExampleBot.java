package com.example.bwbot;

import bwapi.*;
import bwem.BWEM;
import bwem.Base;

import bwta.BWTA;
import jbweb.Blocks;
import jbweb.JBWEB;

import java.util.*;

/********************************************
 *  TODO: Add toBeBuilt to StrategyManager  *
 *        It might have custom objects idk  *
 *                                          *
 *  this may get recreated at some point    *
 *  once we're at spaghetti                 *
 ********************************************/

class ExampleBot extends DefaultBWListener {
    public static void main(String[] args) {
        BroodWarClient.getInstance().init();
    }
}
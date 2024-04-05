package com.example.bwbot;

/********************************************
 *  TODO: Remove entry from beingBuilt      *
 *   (maybe make seperate) when constructed *
 *  TODO: Use SCV after it's done building  *
 *  this may get recreated at some point    *
 *  once we're at spaghetti                 *
 ********************************************/

class ExampleBot {
    public static void main(String[] args) {
        BroodWarClient.getInstance().init();
    }
}
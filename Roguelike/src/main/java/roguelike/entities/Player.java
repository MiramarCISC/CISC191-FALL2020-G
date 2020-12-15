/*
	Class created by Darius Yeung for CISC191 Roguelike project.

 	Contributions from Joshua Monroe.
*/

package roguelike.entities;

import java.util.Map;
//import java.util.Random;

public class Player extends Creature {
    private int levelThreshold;
    private int playerXP;       // Player's current XP.
    private int level;			// Player's current level.

    public Player(Map<String, String> creatureData, int x, int y) {
        super(creatureData, x, y);
        this.level = 1;
        this.playerXP = 0;
        this.levelThreshold = 100;
    }

    /** @return The player's current XP. */
    public int getPlayerXP() { return playerXP; }

    /** @return The level threshold. */
    public int getLevelThreshold() { return levelThreshold; }

    /** @return The player's current level. */
    public int getLevel() { return level; }

    /** Add multiplier and randomizes whether a critical attack is made or not */
    public void addXP(Creature creature, double xpMultiplier) {
        playerXP += creature.getXpValue() * xpMultiplier;


        // if playerXP > levelThreshold += 1. ----- levelThreshold * 2.1
        if (playerXP > levelThreshold) {
            level++;
            levelThreshold *= 2.1;
        }
    }
}

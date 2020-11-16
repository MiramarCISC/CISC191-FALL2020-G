package roguelike.entities;

import java.util.Map;

public class Player extends Creature {
    private int levelThreshold;
    private int playerXP;       // Player's current XP.
    private int level;			// Player's current level.
    public Player(Map<String, String> creatureData, int x, int y) {
        super(creatureData, x, y);
        this.playerXP = 0;
        this.levelThreshold = 100;
    }

    /** @return The player's current XP. */
    public int getPlayerXP() { return playerXP; }

    /** @return The level threshold. */
    public int getLevelThreshold() { return levelThreshold; }

    /** @return The player's current level. */
    public int getLevel() { return level; }

    /** Adds XP value with a constant (maybe 20)
     *  @param creature Pass a positive number if player did something good, i.e. killing a creature.
     */
    public void addXP(Creature creature) {
        playerXP += creature.getXpValue();
        // if playerXP > levelThreshold += 1. ----- levelThreshold * 2.1
        if (playerXP > levelThreshold) {
            level++;
            levelThreshold *= 2.1;
        }
    }
//    while (xpValue > (int)(Math.pow(level, 1.5) * 15)) {
//        level++;
//        System.out.println("LEVEL UP!" + level);
//    }
}

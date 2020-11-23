package roguelike.Inventory;

import roguelike.entities.Creature;
import roguelike.entities.Player;

public class DropScreen {
    Creature player;
    public DropScreen(Creature player) {
        super(player);
    }

    protected String getVerb() {
        return "drop";
    }
}

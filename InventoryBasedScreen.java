package roguelike.Inventory;

import asciiPanel.AsciiPanel;
import com.sun.glass.ui.Screen;
import roguelike.entities.Creature;



import java.util.ArrayList;


public abstract class InventoryBasedScreen {

        protected Creature player;
        private String letters;

        protected abstract String getVerb();
        protected abstract boolean isAcceptable(Item item);
        protected abstract Screen use(Item item);

        public InventoryBasedScreen(Creature player){
            this.player = player;
            this.letters = "abcdefghijklmnopqrstuvwxyz";
        }

    public void displayOutput(AsciiPanel terminal) {
        ArrayList<String> lines = getList();

        int y = 23 - lines.size();
        int x = 4;

        if (lines.size() > 0)
            terminal.clear(' ', x, y, 20, lines.size());

        for (String line : lines){
            terminal.write(line, x, y++);
        }

        terminal.clear(' ', 0, 23, 80, 1);
        terminal.write("What would you like to " + getVerb() + "?", 2, 23);

        terminal.repaint();
    }


    private Inventory inventory = new Inventory(20);
    public Inventory inventory() { return inventory; }

    private ArrayList<String> getList() {
        ArrayList<String> lines = new ArrayList<String>();
        Item[] inventory = player.inventory().getItems();

        for (int i = 0; i < inventory.length; i++){
            Item item = inventory[i];

            if (item == null || !isAcceptable(item))
                continue;

            String line = letters.charAt(i) + " - " + item.glyph() + " " + item.name();

            lines.add(line);
        }
        return lines;
    }

    private Screen userExits(){
        for (Item item : player.inventory().getItems()){
            if (item != null && item.name().equals("teddy bear"))
                return new WinScreen();
        }
        return new LoseScreen();
    }
}



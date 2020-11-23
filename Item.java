
package roguelike.Inventory;

import asciiPanel.AsciiPanel;
import roguelike.entities.Creature;
import roguelike.entities.Tile;
import roguelike.entities.Entity;
import roguelike.world.World;

import java.awt.Color;

//update the world to track, add, and remove items
//pickup, use, and drop items with creature class
//update Playscreen to display items
// accept keystrokes for the player to pickup or drop items.

public class Item {                                             //item consists of how it look, color, and name

    private char glyph;                                         //define how it looks by character
    public char glyph() { return glyph; }

    private Color color;                                        //get color of item
    public Color color() { return color; }

    private String name;                                        //name of item
    public String name() { return name; }

    public Item(char glyph, Color color, String name){          //define contructor of item
        this.glyph = glyph;
        this.color = color;
        this.name = name;
    }

    World world = new World(); // world object to implement with world package
    //to add items, find empty tile, and space
    public Tile tile(int wx, int wy, int wz) {
        return world.tile(wx, wy, wz);
    }
    public enum Tile {
        FLOOR((char)250, AsciiPanel.yellow),
        WALL((char)177, AsciiPanel.yellow),
        BOUNDS('x', AsciiPanel.brightBlack);

        private char glyph;
        public char glyph() { return glyph; }

        private Color color;
        public Color color() { return color; }

        Tile(char glyph, Color color){
            this.glyph = glyph;
            this.color = color;
        }


        public boolean isGround() {                             //make sur the tile is not wall, or out of
        return this != WALL && this != BOUNDS;                  //world's boundary
    }

    public void addAtEmptyLocation(Item item, int depth) {      // a function to add items in
        int x;                                                  //empty location at random
        int y;

        do {
            x = (int)(Math.random() * width);                    //randomly generate width, and height of area
            y = (int)(Math.random() * height);
        }
        while (!tile(x,y,depth).isGround() || item(x,y,depth) != null); /* conditions are... the area is ground
                                                                           and not occupied
                                                                        */
        items[x][y][depth] = item;
    }


    public Item newRock(int depth){         // create rocks for world
        Item rock = new Item(',', AsciiPanel.yellow, "rock");
        world.addAtEmptyLocation(rock, depth);
        return rock;
    }


    int width, height, depth;
    private Item[][][] items = new Item[width][height][depth];   // declare items' dimension

    public Item item(int x, int y, int z){
        return items[x][y][z];
    }


    ///// for the world item characteristics
    public char glyph(int x, int y, int z){
        Creature creature = creature(x, y, z);
        if (creature != null)
            return creature.glyph();

        if (item(x,y,z) != null)
            return item(x,y,z).glyph();

        return tile(x, y, z).glyph();
    }

    public Color color(int x, int y, int z){
        Creature creature = creature(x, y, z);
        if (creature != null)
            return creature.color();

        if (item(x,y,z) != null)
            return item(x,y,z).color();

        return tile(x, y, z).color();
    }

}




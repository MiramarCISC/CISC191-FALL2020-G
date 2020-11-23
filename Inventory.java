package roguelike.Inventory;

import com.sun.glass.ui.Screen;
import roguelike.entities.Creature;
import roguelike.entities.Player;
import roguelike.world.World;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Inventory {
    String letters;
    Creature creature;
    World world;

    private Item[] items;
    public Item[] getItems() { return items; }
    public Item get(int i) { return items[i]; }



    public Inventory(int max){
        items = new Item[max];
    }



    public void add(Item item){
        for (int i = 0; i < items.length; i++){
            if (items[i] == null){
                items[i] = item;
                break;
            }
        }
    }


    public void remove(Item item){
        for (int i = 0; i < items.length; i++){
            if (items[i] == item){
                items[i] = null;
                return;
            }
        }
    }


    public boolean isFull(){
        int size = 0;
        for (int i = 0; i < items.length; i++){
            if (items[i] != null)
                size++;
        }
        return size == items.length;
    }




    private Inventory inventory = new Inventory(20);;
    public Inventory inventory() { return inventory; }

    public void pickup(){
        Item item = world.item(int x, int y, int z);

        if (inventory.isFull() || item == null){
            doAction("grab at the ground");
        } else {
            doAction("pickup a %s", item.name());
            world.remove(x, y, z);
            inventory.add(item);
        }
    }

    public void drop(Item item){
        doAction("drop a " + item.name());
        inventory.remove(item);
        world.addAtEmptySpace(item, x, y, z);
    }


    public void addAtEmptySpace(Item item, int x, int y, int z){
        if (item == null)
            return;

        List<Point> points = new ArrayList<Point>();
        List<Point> checked = new ArrayList<Point>();

        points.add(new Point(x, y, z));

        while (!points.isEmpty()){
            Point p = points.remove(0);
            checked.add(p);

            if (!tile(p.x, p.y, p.z).isGround())
                continue;

            if (items[p.x][p.y][p.z] == null){
                items[p.x][p.y][p.z] = item;
                Creature c = this.creature(p.x, p.y, p.z);
                if (c != null)
                    c.notify("A %s lands between your feet.", item.name());
                return;
            } else {
                List<Point> neighbors = p.neighbors8();
                neighbors.removeAll(checked);
                points.addAll(neighbors);
            }
        }
    }


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

    protected boolean isAcceptable(Item item) {
        return true;
    }



}

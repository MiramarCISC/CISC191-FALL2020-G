/*
	Class created by Jelle Pelgrims.
*/

package roguelike.entities;

import roguelike.Roguelike;

import java.awt.*;
import java.util.Map;

public class Tile extends Entity {
	
    private Color backgroundColor;
    private boolean blocked;
    
    public boolean isBlocked() {return this.blocked;}
    public Color getBackgroundColor() {return this.backgroundColor;}
    
    public Tile(Map<String, String> tileData, int xpos, int ypos)
    {
    	super(tileData, xpos, ypos);
        backgroundColor = Roguelike.stringToColor(tileData.get("backgroundColor"));
        blocked = Boolean.parseBoolean(tileData.get("blocked"));
    }
}
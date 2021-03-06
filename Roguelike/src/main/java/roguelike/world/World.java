/*
	Original class created by Jelle Pelgrims.

 	Contributions for CISC191 Roguelike project
  	from Joshua Monroe.
*/

package roguelike.world;

import roguelike.entities.Creature;
import roguelike.entities.Entity;
import roguelike.entities.Player;
import roguelike.entities.Tile;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class World {
	
	private Tile[][] tiles;
	private int width;
	private int height;
	public Player player;
	public Set<Creature> creatures;
	
	public int width() { return width; }
	public int height() { return height; }
	
	public World(Tile[][] tiles) {
		this.tiles = tiles;
		this.width = tiles.length;
		this.height = tiles[0].length;
	}
	
	public World(Tile[][] tiles, Set<Creature> creatures) {
		this.creatures = new HashSet<>();
		this.creatures.addAll(creatures);
		this.tiles = tiles;
		this.width = tiles.length;
		this.height = tiles[0].length;
	}
	
	public void addEntity(Creature creature) {
		this.creatures.add(creature);
	}
	
	public Tile tile(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return null;
		else
			return tiles[x][y];
	}
	
	public char glyph(int x, int y){
		return tile(x, y).getGlyph();
	}
	
	public Color glyphColor(int x, int y){
		return tile(x, y).getColor();
	}	
	
	public Color backgroundColor(int x, int y){
		return tile(x, y).getBackgroundColor();
	}
	
	public Entity getEntityAt(int x, int y) {
		return creatures.stream()
				.filter(entity -> entity.getX() == x && entity.getY() == y)
				.findFirst()
				.orElse(null);
	}
	
	public boolean isBlocked(int x, int y) {
		return (tiles[x][y].isBlocked() || getEntityAt(x, y) != null);
	}
	
	public void update() {
		creatures.stream().filter(creature -> creature.killedBy() == player).forEach(creature -> player.addXP(creature, 1.0));
		// Remove from the game any creatures that are dead.
		creatures.removeIf(creature -> creature.killedBy() != null);
		// Call the update method for all creatures, with the exception of the player.
		creatures.stream()
			.filter(creature -> !creature.equals(player))
			.forEach(creature -> creature.update(this));
	}

	public Set<String> getTileTypesInArea(Rectangle rectangle) {
		Set<String> tileTypes = new HashSet<>();
		Tile tile;

		for (int y = (int)rectangle.getY(); y < rectangle.getMaxY(); y++) {
			for (int x = (int)rectangle.getX(); x < rectangle.getMaxX(); x++) {
				tile = this.tiles[x][y];
				if (tile != null) {
					tileTypes.add(tile.getType());
				}
			}
		}

		return tileTypes;
	}

	public Creature getCreatureClosestTo(Creature creature) throws NoSuchElementException {
		return creatures.stream().parallel()
				.filter(c -> !c.equals(creature))
				.min(Comparator.comparing(c ->
						Math.sqrt(Math.pow(c.getX() - creature.getX(), 2) + Math.pow(c.getY() - creature.getY(), 2))))
				.orElseThrow();
	}

	public Set<String> getCreatureTypesInArea(Rectangle rectangle) {
		Set<String> creatureTypes = new HashSet<>();
		creatureTypes.add(player.getType());

		for (Creature creature : this.creatures) {
			if (creature.getX() > (player.getX() - (rectangle.getWidth() / 2)) &&
				creature.getX() < (player.getX() + (rectangle.getWidth() / 2)) &&
				creature.getY() > (player.getY() - (rectangle.getHeight() / 2)) &&
				creature.getY() < (player.getY() + (rectangle.getHeight() / 2)))
			{
				creatureTypes.add(creature.getType());
			}
		}

//		OLD CODE: Replaced with the above because it didn't work correctly.
//
//		for (Creature creature : this.creatures) {
//			if (creature.getX() > rectangle.getX() && creature.getX() < rectangle.getMaxX() &&
//					creature.getY() > rectangle.getY() && creature.getY() < rectangle.getMaxY()) {
//				creatureTypes.add(creature.getType());
//				System.out.println(creature.getType());
//			}
//		}
		
		return creatureTypes;
	}

	/** Search a specified area for creatures.
	 * 	@param      x X-coordinate of search center.
	 * 	@param      y Y-coordinate of search center.
	 * 	@param  width Width of search area.
	 * 	@param height Height of search area.
	 *
	 * @return A list of creatures in the specified search area.
	 */
	public List<Creature> getCreaturesInArea(int x, int y, int width, int height) {
		return creatures.stream()
				.filter(creature -> creature.getX() > x - (width / 2) &&
									creature.getX() < x + (width / 2) &&
									creature.getY() > y - (height / 2) &&
									creature.getY() < y + (height / 2))
				.collect(Collectors.toList());
	}
}

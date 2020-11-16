package roguelike.entities;

import roguelike.world.World;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Creature extends Entity {

	private int hitPoints;      // Creature's hit points.
	private int attackDmg;		// Creature's attack damage.
	private int xpValue;		// Creature's xp value when killed.
	private String behaviour;	// Creatures behaviour type.
	private boolean isDead;		// Tells the game that the creature can be removed once it has been killed.

	public Creature(Map<String, String> creatureData, int x, int y) {
		super(creatureData, x, y);
		this.hitPoints = Integer.parseInt(creatureData.get("hitPoints")); // Initialize starting hit points.
		this.attackDmg = Integer.parseInt(creatureData.get("attackDmg")); // Initialize attack damage.
		this.xpValue = Integer.parseInt(creatureData.get("xpValue"));     // Initialize xp value when killed.
		this.behaviour = creatureData.get("behaviour");					  // Initialize behaviour type from creatureData.
		this.isDead = false;											  // Initialize isDead to false.
	}

	/** @return True if creature is has been killed. */
	public boolean isDead() {
		return isDead;
	}

	/** @return The creature's current hitPoints. */
	public int getHitPoints() {
		return hitPoints;
	}

	/** @return The player's current XP value. */
	public int getXpValue() { return xpValue; }

	/** Modifies the creature's hitPoints by adding a specified amount.
	 * 	@param amount Pass a positive number to heal or negative number to damage.
	 */
	private void modifyHitPoints(int amount) {
		if (hitPoints + amount > 0) {
			hitPoints += amount;
		} else {
			hitPoints = 0;
			isDead = true;
		}
	}

	/** Move the creature to a new tile, else if the tile is blocked perform an action based on the blocking entity.
	 * 	@param world The game's world object.
	 * 	@param    dx Movement distance in the horizontal plane.
	 * 	@param	  dy Movement distance in the vertical plane.
	 */
	public void move(World world, int dx, int dy) {
		// Check if the prospective tile is blocked, if not move there.
		if (!world.isBlocked(x + dx, y + dy))
		{
			x += dx;
			y += dy;
		} else {
			// Else, if the tile is blocked, retrieve the blocking entity.
			Entity entity = world.getEntityAt(x + dx, y + dy);

			// If the entity is an item, use it.
			if (entity instanceof Item) {
				useItem((Item) entity);
				// Else, if the entity is a creature, and the calling entity is not docile, attack the creature.
			} else if (entity instanceof Creature && !behaviour.equals("docile")) {
				attackCreature((Creature)entity);
			}
		}
	}

	public void useItem(Item item) {
		if (item.getEffect().equals("health") && hitPoints <= 90) {
			hitPoints += 10;
		}
	}

	/** Attacks a creature.
	 * 	@param creature The creature being attacked.
	 */
	public void attackCreature(Creature creature) {
		// Modify the hit points of the creature being attacked.
		creature.modifyHitPoints(-attackDmg);

		// Log the attack to the console for debugging purposes.
		System.out.println(this.getType() + " attacked " + creature.getType() + " " + creature.getHitPoints() + "/100.");
	}

	public void update(World world) {
		Random rnd = new Random();
		int performAction = rnd.nextInt(100);
		if (behaviour.equals("docile") && performAction > 94) {
			// walk around and flee if attacked

			int rndNr = rnd.nextInt(3);

			switch (rndNr) {
				case 0:
					move(world, 1, 0);
					break;
				case 1:
					move(world, -1, 0);
					break;
				case 2:
					move(world, 0, 1);
					break;
				case 3:
					move(world, 0, -1);
					break;
			}

		} else if (behaviour.equals("aggressive") && performAction > 94) {
			// look for other npcs and hunt them
			List<Creature> creatures = world.getCreaturesInArea(this.x, this.y, 10, 10);
			creatures.remove(this);

			if (creatures.size() > 0) {
				Creature creature = creatures.get(0);

				if (this.x > creature.getX()) {
					this.move (world, -1, 0);
				} else if (this.x < creature.getX()) {
					this.move (world, 1, 0);
				} else if (this.y > creature.getY()) {
					this.move (world, 0, -1);
				} else if (this.y < creature.getY()) {
					this.move (world, 0, 1);
				}
			}
		}

	}
}

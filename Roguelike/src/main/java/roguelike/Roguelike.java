package roguelike;

import roguelike.entities.Creature;
import roguelike.entities.Player;
import roguelike.ui.Interface;
import roguelike.world.World;
import roguelike.world.WorldBuilder;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Roguelike {

	private boolean isRunning;
	private int framesPerSecond = 60;
	private int timePerLoop = 1000000000 / framesPerSecond;
	
	private World world;
	private Player player;
	
	private Map<String,Map<String, String>> creatureData;
	private Map<String,Map<String, String>> tileData;
	private Map<String,Map<String, String>> itemData;
	
	private int screenWidth;
	private int screenHeight;
	
	private Rectangle gameViewArea;
	
	private static final int mapWidth = 1000;
	private static final int mapHeight = 1000;
	
	private Interface ui;
	
	public Roguelike(int screenWidth, int screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		gameViewArea = new Rectangle(screenWidth, screenHeight-5);
		
		ui = new Interface(screenWidth, screenHeight, new Rectangle(mapWidth, mapHeight));
		
		creatureData = loadData(getClass().getClassLoader().getResourceAsStream("creatures.txt"));
		tileData = loadData(getClass().getClassLoader().getResourceAsStream("tiles.txt"));
		itemData = loadData(getClass().getClassLoader().getResourceAsStream("items.txt"));
		
		createWorld();
	}
	
	public Map<String, Map<String, String>> loadData(InputStream inputStream) {
		Map<String, Map<String, String>> entityMap = new HashMap<>();
		String line;
		String[] attributeNames = new String[10];
		
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

        	line = br.readLine();
        	
        	if (line != null) {
        		attributeNames = line.split(", ");
        	}
        	
            while ((line = br.readLine()) != null) {
                String[] data = line.split(", ");
                Map<String, String> entityData = new HashMap<>();
                
                for (int i=0; i<attributeNames.length; i++) {
                	entityData.put(attributeNames[i], data[i]);
                }
                
                String name = data[1];
                entityMap.put(name, entityData);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entityMap;
	}
	
	private void createWorld() {
		player = new Player(creatureData.get("player"), 30, 30);
		world = new WorldBuilder(tileData, creatureData, mapWidth, mapHeight)
				    .fill("wall")
				    .createRandomWalkCave(12232, 30, 30, 6000)
				    .addBorders()
				    .populateWorld(20)
					.build();
		world.player = player;
		world.addEntity(player);
	}
	
	public void processInput() {
	    InputEvent event = ui.getNextInput();
	    if (event instanceof KeyEvent) {
	    	KeyEvent keypress = (KeyEvent)event;
	    	switch (keypress.getKeyCode()){
				case KeyEvent.VK_A:
					player.move(world, -1, 0); 
					break;
				case KeyEvent.VK_D:
					player.move(world, 1, 0);
					break;
				case KeyEvent.VK_W:
					player.move(world, 0, -1); 
					break;
				case KeyEvent.VK_S:
					player.move(world, 0, 1); 
					break;
				case KeyEvent.VK_SPACE: // Primitive ranged attack implementation.
					try {
						player.attackCreature(world.getCreatureClosestTo(player));
					} catch (NoSuchElementException e) {
						System.out.println(e.toString());
					}
					break;
			}
	    } else if (event instanceof MouseEvent) {
	    	//
	    }
	}
	
	public void render() {
		// Clears the terminal.
		ui.getTerminal().clear();
		// Writes the game screen to the terminal.
		ui.pointCameraAt(world, player.getX(), player.getY());
		// Writes the player's HP to the terminal.
		ui.drawPlayerStats(gameViewArea, world, player);
		// Writes the dynamic legend to the terminal.
		ui.drawDynamicLegend(gameViewArea, world, tileData, creatureData);
		// Draws the terminal on the screen.
		ui.refresh();
	}
	
	public void update() {
		world.update();
	}
	
	public void run() {
		isRunning = true;

		// Run the game until it is stopped or until the player dies.
		while(isRunning && player.killedBy() == null) {
			long startTime = System.nanoTime();
			
			processInput();
			update();
			render();
			
			long endTime = System.nanoTime();
			
			long sleepTime = timePerLoop - (endTime-startTime);
			
			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime/1000000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		Roguelike game = new Roguelike(80, 24);
		game.run();
	}
	
	public static Color stringToColor(String colorString) {
		Color color;
		try {
		    Field field = Color.class.getField(colorString);
		    color = (Color)field.get(null);
		} catch (Exception e) {
		    color = null; // Not defined
		}
		return color;
	}

}

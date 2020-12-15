/*
	Original class created by Jelle Pelgrims.

 	Contributions for CISC191 Roguelike project
  	from Joshua Monroe and Christopher Lee.
*/

package roguelike.world;

import roguelike.entities.Creature;
import roguelike.entities.Tile;

import java.util.*;

public class WorldBuilder {
	private int width;
	private int height;
	private Tile[][] tiles;
	private Map<String, Map<String, String>> tileData;
	private Map<String, Map<String, String>> creatureData;
	private Set<Creature> creatures;

	public WorldBuilder(Map<String, Map<String, String>> tileData, Map<String, Map<String, String>> creatureData, int width, int height) {
		this.width = width;
		this.height = height;
		this.tiles = new Tile[width][height];
		this.tileData = tileData;
		this.creatureData = creatureData;
		this.creatures = new HashSet<>();
	}

	public WorldBuilder load(String file) {
		// Loads map from file
		return this;
	}
	
	public Tile createTile(String type, int x, int y) {
		return new Tile(tileData.get(type), x, y);
	}
	
	public Creature createCreature(String type, int x, int y) {
		return new Creature(creatureData.get(type), x, y);
	}
	
	public WorldBuilder fill(String tileType) {
		for (int x=0; x < width; x++) {
			for (int y=0; y < height; y++) {
				tiles[x][y] = new Tile(tileData.get(tileType), x, y);
			}
		}
		return this;
	}
	
	public WorldBuilder addBorders() {
		for (int x=0; x<width; x++) {
			tiles[x][0] = createTile("wall", x, 0);
			tiles[x][height-1] = createTile("wall", x, height-1);
		}
		
		for (int y=0; y<height; y++) {
			tiles[0][y] = createTile("wall", 0, y);
			tiles[width-1][y] = createTile("wall", width-1, y);
		}
		return this;
	}
	
	public WorldBuilder carveOutRoom(int topX, int topY, int width, int height) {
		for (int x=topX; x < topX+width; x++) {
			for (int y=topY; y < topY+height; y++) {
				tiles[x][y] = createTile("ground", x, y);
			}
		}
		return this;
	}
	
	public WorldBuilder populateWorld(int nrOfCreatures) {
		Random rnd = new Random();
		int rndX;
		int rndY;
		
		for (int i=0; i < nrOfCreatures; i++) {
			
			do {
				rndX = rnd.nextInt(width);
				rndY = rnd.nextInt(height);
			} while (tiles[rndX][rndY].isBlocked());
			
			List<String> creatureTypes = new ArrayList<>(creatureData.keySet());
			creatureTypes.remove("player");
			String creatureType = creatureTypes.get(rnd.nextInt(creatureTypes.size()));
			
			creatures.add(createCreature(creatureType, rndX, rndY));
			
		}
		
		return this;
	}

	// Method by Christopher.
	public WorldBuilder createRandomWalkCave(int seed, int x, int y, int length) {
		int probDir=(int)(Math.random()*4);
		int failedInRow=0;
		int steps=0;
		int totalsteps=0;
		int changeDir=50;
		double probUp=0.25;
		double probDown=0.25;
		double probRight=0.25;
		double probLeft=0.25;
		int thickness=5;
		while(true) {
			if(steps>changeDir||failedInRow>20) {
				probDir=(int)Math.random()*4;
				totalsteps+=steps;
				steps=0;
			}
			probUp=0.05;
			probDown=0.05;
			probRight=0.05;
			probLeft=0.05;
			if(steps>=changeDir)
			switch(probDir) {
			case 0: 
			probUp=0.9;
			probDown=0;
			break;
			case 1:
			probDown=0.9;
			probUp=0;
			break;
			case 2:
			probRight=0.9;
			probLeft=0;
			break;
			case 3:
			probLeft=0.9;
			probRight=0;
			}
		
			double dir = Math.random();
			
			if(dir<probDown) {
				//go down
				if(rectInBounds(x-(thickness/2),y-1, thickness, 1)) {
					for(int i = x-thickness/2; i<x-thickness/2+thickness+1; i++) {
						tiles[i][y-1]=createTile("ground",i,y-1);
					}
					y--;
					steps++;
					failedInRow=0;
				}else {
					failedInRow++;
				}
				
				
			}else if(dir>probDown&&dir<probDown+probUp) {
				//go up
				if(rectInBounds(x-(thickness/2),y+1, thickness, 1)) {
					for(int i = x-thickness/2; i<x-thickness/2+thickness+1; i++) {
						tiles[i][y+1]=createTile("ground",i,y+1);
					}
					y++;
					steps++;
					failedInRow=0;
				}else {
					failedInRow++;
				}
			}else if(dir>probDown+probUp&&dir<probDown+probUp+probLeft) {
				//go left
				if(rectInBounds(x-1,y-(thickness/2), 1, thickness)) {
					for(int i = y-thickness/2; i<y-thickness/2+thickness+1; i++) {
						tiles[x-1][i]=createTile("ground",x-1,i);
					}
					x--;
					steps++;
					failedInRow=0;
				}else {
					failedInRow++;
				}
			}else if(dir>probDown+probUp+probLeft&&dir<probDown+probUp+probLeft+probRight) {
				//go right
				if(rectInBounds(x+1,y-(thickness/2), 1, thickness)) {
					for(int i = y-thickness/2; i<y-thickness/2+thickness+1; i++) {
						tiles[x+1][i]=createTile("ground",x+1,i);
					}
					x++;
					steps++;
					failedInRow=0;
				}else {
					failedInRow++;
				}
			}
			if(totalsteps>length) {
				break;
			}
			
		}

		return this;
	}

	// Method by Christopher.
	public boolean rectInBounds(int i, int j, int w, int h) {
		if(pointInBounds(i,j)&&pointInBounds(i+w,j+h))
			return true;
		return false;
	}

	// Method by Christopher.
	public boolean pointInBounds(int i, int j) {
		if(i<tiles.length&&i>=0&&j<tiles[i].length&&j>=0)
			return true;
		return false;
	}
	
	
	public World build() {
		return new World(tiles, creatures);
	}

}
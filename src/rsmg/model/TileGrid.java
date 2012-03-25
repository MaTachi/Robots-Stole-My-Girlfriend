package rsmg.model;

import java.awt.Point;

import rsmg.io.IO;

/**
 * Contains information about the Tile[][]
 */
public class TileGrid {

	private Tile[][] grid;

	/**
	 * Constructor. Gets the level specified from IO
	 * 
	 * @param levelReached
	 *            Level that are going to run
	 */
	public TileGrid(Tile[][] grid) {
		this.grid = grid;
		showGrid();
	}

	/**
	 * Set specified Tile to specified place
	 * 
	 * @param x
	 *            X coordinate in matrix
	 * @param y
	 *            y coordinate in matrix
	 * @param tile
	 *            The tile to be set
	 */
	public void set(int x, int y, Tile tile) {
		grid[y][x] = tile;
	}

	/**
	 * Get specified Tile in matrix
	 * 
	 * @param x
	 *            x coordinate in matrix
	 * @param y
	 *            y coordinate in matrix
	 * @return The Tile in the matrix
	 */
	public Tile get(int x, int y) {
		return grid[y][x];
	}
	
	/**
	 * Returns the number of tiles horizontally in the tile grid.
	 * @return Number of tiles horizontally.
	 */
	public int getWidth() {
		return grid[0].length;
	}
	
	/**
	 * Returns the number of tiles vertically in the tile grid.
	 * @return Number of tiles vertically.
	 */
	public int getHeight() {
		return grid.length;
	}
	
	/**
	 * Get the spawn position as a real position in the model.
	 * @return The position where the spawn position is located.
	 * @throws Exception If a spawn position can't be found.
	 */
	public Point getSpawnPoint() throws Exception {
		for(int x = 0; x < getWidth(); x++) {
			for(int y = 0; y < getHeight(); y++) {
				if (get(x,y) instanceof SpawnTile) {
					return new Point(x*Constants.TILESIZE, y*Constants.TILESIZE);
				}
			}
		}
		throw new Exception();
		//TODO change to a specifed exception
	}
	
	/**
	 * Get tile number from a coordinate in the model.
	 * For an example, if you give the method the argument 50 and a tile is
	 * 32 units large, the method will return 1 since the real position is in
	 * tile number 1.
	 * @param realPos The position in the game.
	 * @return The position in the matrix.
	 */
	public int getTilePosFromRealPos(double realPos) {
		return (int)(realPos / Constants.TILESIZE);
	}
	
	/**
	 * Check if an interactive object intersects with any solid tiles.
	 * @param object The interactive object.
	 * @return If the object intersects with any solid tiles.
	 */
	public boolean intersectsWith(InteractiveObject object) {
		
		// Get the object's boundaries in the tile grid.
		int leftX = getTilePosFromRealPos(object.getX());
		int rightX = getTilePosFromRealPos(object.getX()+object.getWidth());
		int topY = getTilePosFromRealPos(object.getY());
		int bottomY = getTilePosFromRealPos(object.getY()+object.getHeight());
		
		// Walk through all tiles that the object is lying over and check if any
		// of those are solid.
		for (int x = leftX; x <= rightX; x++) {
			for (int y = topY; y <= bottomY; y++) {
				if (get(x,y).isSolid() == true)
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns how much the object is inside a tile to his left;
	 * @param object The interactive object.
	 * @return The distance he is inside a tile to his left;
	 */
	public double leftSideIntersection(InteractiveObject object) {
		
		int leftX = getTilePosFromRealPos(object.getX());
		int topY = getTilePosFromRealPos(object.getY());
		int bottomY = getTilePosFromRealPos(object.getY()+object.getHeight());
		
		for (int y = topY; y <= bottomY; y++) {
			if (get(leftX, y).isSolid() == true)
				return (leftX * Constants.TILESIZE - object.getX());
		}
		return 0;
	}
	
	/**
	 * Returns how much the object is inside a tile to his right;
	 * @param object The interactive object.
	 * @return The distance he is inside a tile to his right;
	 */
	public double rightSideIntersection(InteractiveObject object) {
		
		int rightX = getTilePosFromRealPos(object.getX()+object.getWidth());
		int topY = getTilePosFromRealPos(object.getY());
		int bottomY = getTilePosFromRealPos(object.getY()+object.getHeight());
		
		for (int y = topY; y <= bottomY; y++) {
			if (get(rightX, y).isSolid() == true)
				return (object.getX() + object.getWidth() - rightX * Constants.TILESIZE);
		}
		return 0;
	}
	
	/**
	 * Returns how much the object is inside a tile under him;
	 * @param object The interactive object.
	 * @return The distance he is inside a tile under him;
	 */
	public double bottomSideIntersection(InteractiveObject object) {

		int leftX = getTilePosFromRealPos(object.getX());
		int rightX = getTilePosFromRealPos(object.getX()+object.getWidth());
		int bottomY = getTilePosFromRealPos(object.getY()+object.getHeight());
		
		for (int x = leftX; x <= rightX; x++) {
			if (get(x, bottomY).isSolid() == true)
				return (object.getY() + object.getHeight() - bottomY * Constants.TILESIZE);
		}
		return 0;
	}
	
	/**
	 * Returns how much the object is inside a tile over him;
	 * @param object The interactive object.
	 * @return The distance he is inside a tile over him;
	 */
	public double topSideIntersection(InteractiveObject object) {

		int leftX = getTilePosFromRealPos(object.getX());
		int rightX = getTilePosFromRealPos(object.getX()+object.getWidth());
		int topY = getTilePosFromRealPos(object.getY()+object.getHeight());
		
		for (int x = leftX; x <= rightX; x++) {
			if (get(x, topY).isSolid() == true)
				return (topY * Constants.TILESIZE - object.getY());
		}
		return 0;
	}

	/**
	 * Test to display grid in console
	 */
	public void showGrid() {
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				System.out.print(get(x,y).isSolid() + " | ");
			}
			System.out.println("");
		}
	}
}

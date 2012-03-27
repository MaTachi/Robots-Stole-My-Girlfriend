package rsmg.model;

import java.awt.Point;

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
	 * Get the Tile placed on the given game coordinate.
	 * @param x X coordinate in the model.
	 * @param y Y coordinate in the model.
	 * @return The Tile on the game coordinate.
	 */
	public Tile getTile(double x, double y) {
		return grid[(int)y/Constants.TILESIZE][(int)x/Constants.TILESIZE];
	}
	
	/**
	 * Get the Tile on a grid coordinate.
	 * @param x X coordinate in the grid.
	 * @param y y coordinate in the grid.
	 * @return The Tile on the given grid coordinate.
	 */
	public Tile getFromCoord(int x, int y) {
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
				if (grid[y][x] instanceof SpawnTile) {
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
		int rightX = getTilePosFromRealPos(object.getX()+object.getWidth()-0.00001);
		int topY = getTilePosFromRealPos(object.getY());
		int bottomY = getTilePosFromRealPos(object.getY()+object.getHeight()-0.00001);
		
		// Walk through all tiles that the object is lying over and check if any
		// of those are solid.
		for (int x = leftX; x <= rightX; x++) {
			for (int y = topY; y <= bottomY; y++) {
				if (grid[y][x].isSolid() == true)
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
			if (grid[y][leftX].isSolid() == true)
				return (leftX + 1) * Constants.TILESIZE - object.getX();
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
			if (grid[y][rightX].isSolid() == true)
				return object.getX() + object.getWidth() - rightX * Constants.TILESIZE;
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
			if (grid[bottomY][x].isSolid() == true)
				return object.getY() + object.getHeight() - bottomY * Constants.TILESIZE;
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
			if (grid[topY][x].isSolid() == true)
				return topY * Constants.TILESIZE - object.getY();
		}
		return 0;
	}

	/**
	 * Test to display grid in console
	 */
	public void showGrid() {
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				System.out.print(getTile(x,y).isSolid() + " | ");
			}
			System.out.println("");
		}
	}
}

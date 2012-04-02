package rsmg.model;

import java.awt.Point;
import java.util.ArrayList;

import rsmg.io.IO;

/**
 * Class representing a level. This Class is in charge of storing and updating
 * information about a level
 * 
 * @author Johan Gr�nvall, Johan Rign�s
 * 
 */
public class Level {

	private Character character;
	/**
	 * Keeps track of which level the user have reached
	 */
	// private int levelReached = 1;
	
	private ArrayList<Bullet> aBullets; 
	/**
	 * The grid layout of the level
	 */
	private TileGrid tGrid;

	/**
	 * Constructor of Level
	 */
	public Level() {

		IO io = new IO();
		aBullets = new ArrayList<Bullet>();  
		tGrid = new TileGrid(io.getLevel(1));
		spawnChar();
	}

	/**
	 * method for spawning the character spawns the character at spawn location,
	 * if no spawn location is found the character is spawned at 0,0
	 */
	private void spawnChar() {
		try {
			Point spawnPoint = tGrid.getSpawnPoint();
			character = new Character(spawnPoint.getX(), spawnPoint.getY(), aBullets);
		} catch (Exception e) { // TODO change to specific exception
			character = new Character(0, 0, aBullets);
		}
	}

	/**
	 * method which handles necessary updates to the levels state
	 * 
	 * @param delta
	 *            Time since last update in seconds.
	 */
	public void update(double delta) {
		outsideMapCheck();

		for(Bullet bullet : aBullets){
			bullet.move(delta);
		}
		
		// Update whether the character is in the air or standing on the ground.
		updateAirbourne();
		
		// Apply gravity to the character so he will fall down if he is in the air.
		character.applyGravity(delta);

		// Move the character.
		character.move(delta);
		character.updateFacing();

		// Check so the character isn't inside a solid tile, and if so, move
		// him outside it.
		applyNormalForce(character);

		
		// Reset the X velocity back to zero.
		character.setVelocityX(0);
	}

	/**
	 * Check if the object is flying (i.e. not standing on a solid tile).
	 * 
	 * @param obj
	 *            The InteractiveObject.
	 * @return true If there is no solid tile underneath specified object.
	 */
	private boolean isAirbourne(InteractiveObject obj) {
		double y = obj.getY() + obj.getHeight() + 0.00001;
		return !(tileIntersect(obj.getX(), y) || tileIntersect(
				obj.getX() + obj.getWidth() - 0.00001, y));
	}
	
	/**
	 * Update whether the character is in the air or standing on the ground.
	 */
	private void updateAirbourne() {
		character.setAirbourne(isAirbourne(character));
	}

	/**
	 * Check so the character isn't outside the level's boundaries.
	 */
	private void outsideMapCheck() {
		if (character.getX() < 0) {
			character.setX(0);
		}
		if (character.getY() < 0) {
			character.setY(0);
		}
	}

	/**
	 * Check if the object collides with any solid tiles. And if that is the
	 * case, move the object back outside the tile.
	 * 
	 * @param obj
	 *            An InteractiveObject
	 */
	private void applyNormalForce(InteractiveObject obj) {
		/**
		 * Check if the object intersects with the grid.
		 */
		if (tGrid.intersectsWith(obj)) {
			// Check if the the object came from the left
			if (cameFromLeft(obj)) {
				// Move the object back to the left
				moveLeft(obj);
				// Set the object's x velocity to zero
				obj.setVelocityX(0);
			}

			// Check if the object came from the right
			if (cameFromRight(obj)) {
				moveRight(obj);
				obj.setVelocityX(0);
			}

			/**
			 * Check if the object is still colliding with any solid tiles. Then
			 * he must have collided with the tile from above or below.
			 */
			if (tGrid.intersectsWith(obj)) {
				if (cameFromAbove(obj)) {
					moveUp(obj);
					obj.setVelocityY(0);
				}
				if (cameFromBelow(obj)) {
					moveDown(obj);
					obj.setVelocityY(0);
				}
			}
		}
	}

	private boolean cameFromAbove(InteractiveObject obj) {
		return obj.getPY() + obj.getHeight() - 0.00001 <= tGrid
				.getTilePosFromRealPos(obj.getY() + obj.getHeight())
				* Constants.TILESIZE;
	}

	private boolean cameFromBelow(InteractiveObject obj) {
		return obj.getPY() >= (tGrid.getTilePosFromRealPos(obj.getY()) + 1)
				* Constants.TILESIZE;
	}

	private boolean cameFromLeft(InteractiveObject obj) {
		return obj.getPX() + obj.getWidth() - 0.00001 <= tGrid
				.getTilePosFromRealPos(obj.getX() + obj.getWidth())
				* Constants.TILESIZE;
	}

	private boolean cameFromRight(InteractiveObject obj) {
		return obj.getPX() >= (tGrid.getTilePosFromRealPos(obj.getX()) + 1)
				* Constants.TILESIZE;
	}

	private void moveUp(InteractiveObject obj) {
		double i = tGrid.bottomSideIntersection(obj);
		obj.setY(Math.floor(obj.getY() - i));
	}

	private void moveDown(InteractiveObject obj) {
		double i = tGrid.topSideIntersection(obj);
		obj.setY(Math.floor(obj.getY() + i));
	}

	private void moveLeft(InteractiveObject obj) {
		double i = tGrid.rightSideIntersection(obj);
		obj.setX(Math.round(obj.getX() - i));
	}

	private void moveRight(InteractiveObject obj) {
		double i = tGrid.leftSideIntersection(obj);
		obj.setX(Math.round(obj.getX() + i));
	}

	/**
	 * 
	 * @param obj
	 * @param tile
	 * @return true coordinates are inside one of the
	 */
	private boolean tileIntersect(double x, double y) {
		return tGrid.getTile(x, y).isSolid();
	}

	/**
	 * Pause the game by stopping tick() in Controller. Pause should be from GUI
	 * to Controller. Not in Model:Level
	 */
	public void pause() {
		// controll.pause();
	}

	/**
	 * Are called when a level is completed. Increase level the user have
	 * completed and navigate to LevelMenu
	 */
	public void completedLevel() {
		// levelReached++;
		// Change state to LevelMenu where Level will be reloaded again
		// controll.goToLevelMenu();
	}

	/**
	 * public int getLevelReached() { return levelReached; }
	 * 
	 * public void setLevelReached(int level) { levelReached = level; }
	 */

	/**
	 * Returns the tile grid.
	 * 
	 * @return The tile grid.
	 */
	public TileGrid getTileGrid() {
		return tGrid;
	}

	/**
	 * Returns the character.
	 * 
	 * @return The character.
	 */
	public Character getCharacter() {
		return character;
	}
	public ArrayList<Bullet> getABulletList(){
		return aBullets;
	}
}
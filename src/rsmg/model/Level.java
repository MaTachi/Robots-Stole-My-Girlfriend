package rsmg.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import rsmg.model.ai.Ai;
import rsmg.model.object.InteractiveObject;
import rsmg.model.object.bullet.Bullet;
import rsmg.model.object.bullet.Explosion;
import rsmg.model.object.item.Item;
import rsmg.model.object.unit.Enemy;
import rsmg.model.object.unit.PCharacter;

/**
 * Class representing a level. This Class is in charge of storing and updating
 * information about a level
 * 
 * @author Johan Gr�nvall, Johan Rign�s, Daniel Jonsson
 * 
 */
public class Level {

	/**
	 * Reference to the character that the user controls.
	 */
	private PCharacter character;
	
	/**
	 * List where friendly bullets from guns are stored.
	 */
	private List<Bullet> alliedBullets;
	
	/**
	 * List where enemy bullets are stored
	 */
	private List<Bullet> enemyBulletList;
	
	/**
	 *  List where all the items are stored
	 */
	private Collection<Item> items;
	
	/**
	 * List where references to all living enemies in the level are stored.
	 */
	private Collection<Ai> enemies;
	
	/**
	 * The grid layout of the level. (I.e. the environment.)
	 */
	private TileGrid tileGrid;
	
	/**
	 * Stores if the character has won the level.
	 */
	private boolean hasWon;
	
	/**
	 * Stores if the player has lost the level.
	 */
	private boolean hasLost;
	
	/**
	 * Creates a level.
	 * @param tileGrid The tile grid that the level shall use.
	 * @param items The items that should be in the level.
	 * @param enemies The enemies in the level.
	 */
	public Level(TileGrid tileGrid, List<Item> items, List<Ai> aiList, List<Bullet> EnemyBullets) {
		this.tileGrid = tileGrid;
		this.items = items;
		this.enemies = aiList;
		enemyBulletList = EnemyBullets;
		alliedBullets = new ArrayList<Bullet>();
		spawnChar();
	}

	/**
	 * Method for spawning the character.
	 * If a spawning point in the tile grid can't be found, the character will be
	 * spawned at 0, 0.
	 */
	private void spawnChar() {
		try {
			Point spawnPoint = tileGrid.getSpawnPoint();
			character = new PCharacter(spawnPoint.getX(), spawnPoint.getY(), alliedBullets);
		} catch (Exception NullPointerException) {
			character = new PCharacter(0, 0, alliedBullets);
		}
	}

	/**
	 * This method handles all updating that should occur between every game frame.
	 * @param delta Time since last update in seconds.
	 */
	public void update(double delta) {
		updateCharacter(delta);
 		updateBullets(delta);
 		updateEnemies(delta);
 		// Checks if the items are picked-up
		updateItems();
	}
	
	/**
	 * Move the character, apply gravity, check for collisions etc.
	 * @param delta Time sine last update.
	 */
	private void updateCharacter(double delta) {
		
		// Update whether the character is in the air or standing on the ground.
		character.setAirborne(isAirbourne(character));
		// Apply gravity to the character so he will fall down if he is in the air.
		character.applyGravity(delta);
		
		// Move the character.
		character.move(delta);

		// Check if the character has reached the end tile.
		checkVictory();
		
		// Check if the player has died, and then mark the level as lost.
		checkDeath();
		
		// update what direction the character is facing towards
		character.updateFacing();
		// Check so the character isn't inside a solid tile, and if so, move
		// him outside it.
		applyNormalForce(character);
		character.setVelocityX(0);
		character.updateImmortality();
		
	}

	/**
	 * Update the enemies. Moves them, checks for collisions, checks if any
	 * enemy has died etc.
	 * @param delta Time sine last update.
	 */
	private void updateEnemies(double delta) {
		for (Iterator<Ai> i = enemies.iterator(); i.hasNext(); ) {
			//update the ai
			Ai ai = i.next();
			ai.update(delta, character.getX(), character.getY());
			
			Enemy enemy = ai.getEnemy();
			
			if (enemy.isDead()) {
				i.remove();
				continue;
			}
			
			if(!enemy.isFlyingUnit()){
				enemy.applyGravity(delta);
			}
			
			enemy.move(delta);
			applyNormalForce(enemy);
			enemy.updateVulnerability();
			// see if enemy has collided with the character and act appropriately
			if (enemy.hasCollidedWith(character)) {
				character.collide(enemy);
				enemy.collide(character);
			}

			//see if enemy has collided with any bullets and act appropriately
			List<Bullet> newBullets = new ArrayList<Bullet>();
			List<Bullet> expiredBullets = new ArrayList<Bullet>();
			for (Iterator<Bullet> j = alliedBullets.iterator(); j.hasNext(); ) {
				Bullet bullet = j.next();

				if (enemy.hasCollidedWith(bullet)) {
					enemy.collide(bullet);
					bullet.collide(enemy);

					// this shouldn't be levels responsibility, but I do not
					// know where to put it otherwise

					if (bullet.getName() == ObjectName.ROCKETR || bullet.getName() == ObjectName.ROCKETL){
						newBullets.add(new Explosion(bullet));
					}
					if(bullet.getName() != ObjectName.EXPLOSION){
						expiredBullets.add(bullet);
					}
				}
			}
			alliedBullets.removeAll(expiredBullets);
			alliedBullets.addAll(newBullets);
		}
	}

	/**
	 * Update all bullets in the level.
	 * @param delta Time sine last update.
	 */
	private void updateBullets(double delta) {
		for (int i = 0; i < alliedBullets.size(); i++) {
			Bullet bullet = alliedBullets.get(i);
			
			bullet.move(delta);
			bullet.update(delta);
			
			if (tileGrid.intersectsWith(bullet)) {
				
				if (bullet.getName() == ObjectName.ROCKETR  || bullet.getName() == ObjectName.ROCKETL)
					alliedBullets.add(new Explosion(bullet));
				
				//if the bullet is an explosion, do not remove it unless its past its duration
				//otherwise, remove the bullet
				if (!(bullet.getName() == ObjectName.EXPLOSION)){
					alliedBullets.remove(i);
				}
			}
			if (bullet.getName() == ObjectName.EXPLOSION){
				if(((Explosion)bullet).getAge() > Constants.EXPLOSIONDURATION){
					alliedBullets.remove(i);
				}
			}
			
		}
	}

	/**
	 * Remove item if it is picked-up.
	 */
	public void updateItems() {
		for (Iterator<Item> i = items.iterator(); i.hasNext(); ) {
			Item item = i.next();
			if (character.hasCollidedWith(item)) {
				i.remove();
				character.collide(item);
			}
		}
	}

	/**
	 * Check if the object is flying (i.e. not standing on a solid tile).
	 * @param obj The InteractiveObject.
	 * @return true If there is no solid tile underneath specified object.
	 */
	private boolean isAirbourne(InteractiveObject obj) {
		double y = obj.getY() + obj.getHeight() + 0.00001;
		return !(tileGrid.tileIntersect(obj.getX(), y) ||
				tileGrid.tileIntersect(obj.getX() + obj.getWidth() - 0.00001, y));
	}

	/**
	 * Checks if the character has reached the end tile.
	 */
	private void checkVictory() {
		hasWon = tileGrid.intersectsWithEndTile(character);
	}

	/**
	 * Checks if the character has died, and then mark the level as lost.
	 */
	private void checkDeath() {
		hasLost = character.getHealth() < 1;
	}

	/**
	 * Returns whether or not the level has been completed.
	 * @return If the level has been completed.
	 */
	public boolean hasWon() {
		return hasWon;
	}

	/**
	 * Returns whether or not the user has lost the level.
	 * @return If the player has lost the level.
	 */
	public boolean hasLost() {
		return hasLost;
	}

	/**
	 * Check if the object collides with any solid tiles. And if that is the
	 * case, move the object back outside the tile.
	 * @param obj An InteractiveObject
	 */
	private void applyNormalForce(InteractiveObject obj) {
		/**
		 * Check if the object intersects with the grid.
		 */
		if (tileGrid.intersectsWith(obj)) {
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
			if (tileGrid.intersectsWith(obj)) {
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
		return obj.getPY() + obj.getHeight() - 0.00001 <= tileGrid
				.getTilePosFromRealPos(obj.getY() + obj.getHeight())
				* Constants.TILESIZE;
	}

	private boolean cameFromBelow(InteractiveObject obj) {
		return obj.getPY() >= (tileGrid.getTilePosFromRealPos(obj.getY()) + 1)
				* Constants.TILESIZE;
	}

	private boolean cameFromLeft(InteractiveObject obj) {
		return obj.getPX() + obj.getWidth() - 0.00001 <= tileGrid
				.getTilePosFromRealPos(obj.getX() + obj.getWidth())
				* Constants.TILESIZE;
	}

	private boolean cameFromRight(InteractiveObject obj) {
		return obj.getPX() >= (tileGrid.getTilePosFromRealPos(obj.getX()) + 1)
				* Constants.TILESIZE;
	}

	private void moveUp(InteractiveObject obj) {
		double i = tileGrid.bottomSideIntersection(obj);
		obj.setY(Math.floor(obj.getY() - i));
	}

	private void moveDown(InteractiveObject obj) {
		double i = tileGrid.topSideIntersection(obj);
		obj.setY(Math.floor(obj.getY() + i));
	}

	private void moveLeft(InteractiveObject obj) {
		double i = tileGrid.rightSideIntersection(obj);
		obj.setX(Math.round(obj.getX() - i));
	}

	private void moveRight(InteractiveObject obj) {
		double i = tileGrid.leftSideIntersection(obj);
		obj.setX(Math.round(obj.getX() + i));
	}

	/**
	 * Returns the tile grid.
	 * @return The tile grid.
	 */
	public TileGrid getTileGrid() {
		return tileGrid;
	}

	/**
	 * Returns the character.
	 * @return The character.
	 */
	public PCharacter getCharacter() {
		return character;
	}
	
	/**
	 * Returns the list of allied bullets.
	 * @return The list of bullets.
	 */
	public Collection<Bullet> getAlliedBulletList() {
		return alliedBullets;
	}
	/**
	 * returns the list of enemy bullets
	 * @return the list of enemy bullets
	 */
	public Collection<Bullet> getEnemyBulletList() {
		return enemyBulletList;
	}

	/**
	 * Returns the list of alive enemies.
	 * @return The list of alive enemies.
	 */
	public Collection<Enemy> getEnemies() {
		Collection<Enemy> enemySet = new HashSet<Enemy>();
		for(Ai ai : enemies ){
			enemySet.add(ai.getEnemy());
		}
		
		return enemySet;
	}

	/**
	 * Returns the list of all items currently in the level.
	 * @return The list of all items currently in the level.
	 */
	public Collection<Item> getItemList() {
		return items;
	}
}
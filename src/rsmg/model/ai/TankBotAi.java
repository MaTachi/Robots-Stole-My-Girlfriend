package rsmg.model.ai;

import rsmg.model.object.unit.Enemy;
import rsmg.model.object.unit.Tankbot;
/**
 * Class in charge of control an idling Enemy
 * @author Johan Gr�nvall
 * 
 */
public class TankBotAi implements Ai {
	private Tankbot enemy;
	private static int AGGRORANGE = 200;
	private double cooldown;
	
	/**
	 * boolean whether the enemy should be attacking or not
	 */
	private boolean aggresive = false;
	
	/**
	 * Creates a TankBotAi with default values
	 * @param enemy Reference to the enemy it should control.
	 */
	public TankBotAi(Tankbot enemy) {
		this.enemy = enemy;
	}

	/**
	 * Update the state and actions for the TankBot
	 */
	@Override
	public void update(double delta, double playerX, double playerY) {
		
		double xDiff = playerX - enemy.getX();
		
		if(xDiff > 0 && xDiff < AGGRORANGE) {
			aggresive = true;
			enemy.setFacing(true);
			
		} else if(xDiff < 0 && xDiff*(-1) < AGGRORANGE) {
			enemy.setFacing(false);
			aggresive = true;
		}else {
			idle();
			aggresive = false;
		}
		
		if (aggresive && shouldAttack(delta)) {
			enemy.shoot();
		}
	}
	
	/**
	 * Telling the TankBot to be still
	 */
	private void idle() {
		enemy.setVelocityX(0);
	}
	
	/**
	 * Tell when the TankBot should attack
	 * @return true if it should attack, otherwise false
	 */
	private boolean shouldAttack(double delta) {
		cooldown += delta;
		// Every 0.8 second it gets the chance to shoot.
		if (cooldown > 0.8) {
			cooldown = 0;
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the TankBot using this AI
	 */
	@Override
	public Enemy getEnemy() {
		return enemy;
	}
}

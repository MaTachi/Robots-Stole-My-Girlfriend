package rsmg.model;


/**
 * Class for representing the playable Character
 * @author Johan Gr�nvall
 *
 */
public class Character extends InteractiveObject {
	int health;
	//Weapon currentWeapon;
	public Character(double x, double y) {
		super(x, y, (double)Constants.CHARACTERWIDTH, (double)Constants.CHARACTERHEIGHT);
		health =  Constants.CHARACTERHEALTH;
	}
	
	@Override
	public void collide(InteractiveObject obj) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Adds a vertical vector to the character sending him up in the air
	 */
	public void jump(){
		this.addVelocity(0, -Constants.JUMPSTRENGTH);
	}
	
	/**
	 * Adds a horizontal vector to the character sending him westwards
	 */
	public void moveLeft(){
		this.getVelocity().setX(-Constants.CHARACTERSPEED);
	}
	
	/**
	 * Adds a horizontal vector to the character sending him eastwards
	 */
	public void moveRight(){
		this.getVelocity().setX(Constants.CHARACTERSPEED);
	}

}
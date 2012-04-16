package rsmg.model;

import java.util.ArrayList;

import rsmg.util.Vector2d;

public class LaserPistol implements IWeapon{
	private LivingObject wielder;
	private ArrayList<Bullet> bulletList;
	private boolean shot;
	private int clipSize;
	
	public LaserPistol(LivingObject wielder, ArrayList<Bullet> bulletList) {
		this.wielder = wielder;
		this.bulletList = bulletList;
		this.shot = false;
	}
	
	@Override
	public void shoot() {
		int bulletWidth = 5;
		int bulletHeight = 3;
		int bulletDamage = 10;
		int bulletSpeed = 500;
		Vector2d bulletVelocity = new Vector2d();
		int offsetX;
		int offsetY;

		if (wielder.isFacingRight()){
			bulletVelocity.setX(bulletSpeed);
			offsetX = 25;
			offsetY = 5;
		}else{
			bulletVelocity.setX(-bulletSpeed);
			offsetX = -5;
			offsetY = 5;
		}
		
		bulletList.add(new Bullet(wielder.getX()+offsetX, wielder.getY()+offsetY, bulletWidth, bulletHeight, 1, bulletVelocity, bulletDamage));
		
		shot = true;
	}

	@Override
	public long getCooldown() {
		return 300;
	}
	
	public int getClipSize(){
		return clipSize;
	}
	
	public long getReloadTime(){
		return 300;
	}
	
	@Override
	public boolean shot() {
		if (shot) {
			shot = !shot;
			return true;
		}
		return false;
	}
}
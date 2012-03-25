package rsmg.model;
import rsmg.util.Vector2d;

/**
 * Class which represents an object that can interact with the main character
 * and has the possibility of being moved around.  
 * @author Johan Gr�nvall
 *
 */
public abstract class InteractiveObject {
	
	private double x;
	private double y;
	private double height;
	private double width;
	
	/**
	 * Store the velocity as a 2d vector.
	 */
	private Vector2d velocity;
	
	public InteractiveObject(double x, double y) {
		this(x, y, 0, 0);
	}
	public InteractiveObject(double x, double y, double width, double height ) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.velocity = new Vector2d();
	}
	
	/**
	 * Get the velocity vector.
	 * @return The velocity vector.
	 */
	public Vector2d getVelocity() {
		return velocity;
	}
	
	public void addVelocity(Vector2d vector) {
		velocity.add(vector);
	}
	
	public void addVelocity(double x, double y) {
		velocity.add(x, y);
	}

	public double getHeight() {
		return height;
	}

	public double getWidth() {
		return width;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	/**
	 * Changes the position of the InteractiveObject based on its current Vector.
	 * a vector with the strength of 1 is equivalent to a speed of 1pixel/s
	 * @param delta, time since last update
	 */
	public void move(double delta) {
		x += getVelocity().getX() * delta;
		y += getVelocity().getY() * delta;
	}

	/**
	 * applies a gravity vector to this InteractiveObject 
	 * @param delta
	 */
	public void applyGravity(double delta) {
		addVelocity(0, Constants.GRAVITYSTRENGTH*delta);
	}
	
	/**
	 * Checks if the two objects have collided with each other.
	 * @param obj
	 * @return true if the two InteractiveObjects have collided, otherwise it returns false
	 */
	public boolean hasCollidedWith(InteractiveObject obj){
		if (obj == null) {
			return false;
		} else {
//			if (verticalCollision(obj)) {
//				return horizontalCollision(obj);
//			}
			return (((this.getX() - obj.getX() < (obj.getWidth()) && (this.getX() > obj.getX()) &&
					((this.getY() - obj.getY()) < obj.getHeight()) && (this.getY() > obj.getY()) ) ||
					((obj.getX() - this.getX() < this.getWidth()) && (obj.getX() > this.getX()) &&
						(obj.getY() - this.getY() < this.getHeight()) && (obj.getY() > this.getY()))));
		}
	}
	
//	private boolean verticalCollision(InteractiveObject obj) {
//		return (getX() - obj.getX() < obj.getWidth() && getX() > obj.getX()) ||
//			   (obj.getX() - getX() < getWidth() && obj.getX() > getX());
//	}
//	
//	private boolean horizontalCollision(InteractiveObject obj) {
//		return (getY() - obj.getY() < obj.getHeight() && getY() < obj.getY()) ||
//			   (obj.getY() - getY() < getHeight()) && (obj.getY() < getY());
//	}
	
	/**
	 * class for specifying what happens when this objects collides with another InteractiveObject
	 */
	public abstract void collide(InteractiveObject obj);
	
}

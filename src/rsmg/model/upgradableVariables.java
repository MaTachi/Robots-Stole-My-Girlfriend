package rsmg.model;
/**
 * Class representing variables that are possible to upgrade
 * @author Johan Gr�nvall
 *
 */
public enum upgradableVariables {
	;
	private static int UPGRADEDCHARSPEED;
	
	public static int charSpeed = 100;
	
	public static void upgradeSpeed() {
		charSpeed = UPGRADEDCHARSPEED;
	}
}

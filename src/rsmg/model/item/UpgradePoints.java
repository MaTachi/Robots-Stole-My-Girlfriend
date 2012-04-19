package rsmg.model.item;

import rsmg.model.ObjectName;

public class UpgradePoints extends Item {
	
	/**
	 * @param x horizontal coordinate for where the Item location
	 * @param y vertical coordinate for where the Item location
	 * @param width width of the item
	 * @param height height of the item
	 */
	public UpgradePoints(double x, double y, double width, double height) {
		super(x, y, width, height, ObjectName.UPGRADE_POINT);
	}
}

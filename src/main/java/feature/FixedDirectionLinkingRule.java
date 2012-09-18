package feature;

import math.Rotation;
import math.Vector;
import validator.DValidator;

/**
 * Two features are linked, if the size of the connecting area meets specified
 * conditions.
 * 
 * The connecting area is approximated as follows:
 * 
 * 1) the features are rotated, so that the desired linking angle is 0°
 * 
 * 2) the axis aligned bounding boxes of the two features are created
 * 
 * 3) an axis aligned rectangle (connecting area), that connects the two
 * bounding boxes, is constructed. The height of this rectangle is the
 * overlapping range of the bounding boxes along the y-axis. The width of the
 * rectangle is the distance of the bounding boxes along the x-axis.
 * 
 * @author PilgerstorferP
 * 
 */
public class FixedDirectionLinkingRule extends LinkingRule {
	private Rotation rotation;
	private DValidator width;
	private DValidator height;

	/**
	 * @param angle
	 *            the angle in radians that defines the linking direction
	 * @param width
	 *            defines the valid distances of two features in the specified
	 *            direction
	 * @param height
	 *            defines the valid amounts of overlapping in the specified
	 *            direction
	 */
	public FixedDirectionLinkingRule(double angle, DValidator width,
			DValidator height) {
		this.rotation = new Rotation(angle);
		this.width = width;
		this.height = height;
	}

	@Override
	public boolean link(Feature f0, Feature f1) {
		// create the bounding boxes of the two features
		Vector min0;
		Vector max0;
		Vector min1;
		Vector max1;
		{
			Vector center = f0.getCenter().center(f1.getCenter());

			Vector[] bounds = Vector.bounds(rotation.rotate(f0.getCorners(),
					center));
			min0 = bounds[0];
			max0 = bounds[1];

			bounds = Vector.bounds(rotation.rotate(f1.getCorners(), center));
			min1 = bounds[0];
			max1 = bounds[1];
		}

		// calculate the width of the connecting rectangle
		double w;
		if (max0.x <= min1.x || max1.x <= min0.x) {
			w = Math.max(min0.x, min1.x) - Math.min(max0.x, max1.x);
		} else {
			// the x-values of the bounding boxes are overlapping
			w = 0;
		}

		if (!width.isValid(w)) {
			return false;
		}

		// calculate the height of the connecting rectangle
		double h;
		if (max0.y <= min1.y || max1.y <= min0.y) {
			// the y-values of the bounding boxes are non overlapping
			h = 0;
		} else {
			h = Math.min(max0.y, max1.y) - Math.max(min0.y, min1.y);
		}

		if (!height.isValid(h)) {
			return false;
		}

		return true;
	}
}

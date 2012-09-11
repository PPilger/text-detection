package feature;

import validator.IntValidator;
import math.Rotation;
import math.Vector;

public class FixedDirectionLinkingRule extends LinkingRule {
	// parameters
	private Rotation rotation;
	private IntValidator width;
	private IntValidator height;

	public FixedDirectionLinkingRule(double angle, IntValidator width,
			IntValidator height) {
		this.rotation = new Rotation(angle);
		this.width = width;
		this.height = height;
	}

	@Override
	public boolean link(Feature f0, Feature f1) {
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

		int w;
		if (max0.x <= min1.x || max1.x <= min0.x) {
			w = (int) Math.round(Math.max(min0.x, min1.x)
					- Math.min(max0.x, max1.x));
		} else {
			w = 0;
		}

		if (!width.isValid(w)) {
			return false;
		}

		int h;
		if (max0.y <= min1.y || max1.y <= min0.y) {
			h = 0;
		} else {
			h = (int) Math.round(Math.min(max0.y, max1.y)
					- Math.max(min0.y, min1.y));
		}

		if (!height.isValid(h)) {
			return false;
		}

		return true;
	}
}

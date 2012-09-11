package feature;

import validator.IntValidator;
import math.Vector;

public class PositionFeatureRule implements FeatureRule {
	private IntValidator x;
	private IntValidator y;
	
	public PositionFeatureRule(IntValidator x, IntValidator y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean isValid(Feature feature) {
		Vector pos = feature.getCenter();
		return x.isValid((int) Math.round(pos.x)) && y.isValid((int) Math.round(pos.y));
	}

}

package feature;

import math.Vector;
import validator.IValidator;

public class PositionFeatureRule implements FeatureRule {
	private IValidator x;
	private IValidator y;

	public PositionFeatureRule(IValidator x, IValidator y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean isValid(Feature feature) {
		Vector pos = feature.getCenter();
		return x.isValid((int) Math.round(pos.x))
				&& y.isValid((int) Math.round(pos.y));
	}
}

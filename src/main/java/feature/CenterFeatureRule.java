package feature;

import math.Vector;
import validator.IValidator;

public class CenterFeatureRule implements FeatureRule {
	private IValidator x;
	private IValidator y;

	public CenterFeatureRule(IValidator x, IValidator y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean isValid(Feature feature) {
		Vector center = feature.getCenter();
		return x.isValid((int) Math.round(center.x))
				&& y.isValid((int) Math.round(center.y));
	}
}

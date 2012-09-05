package feature;

import math.Vector2D;
import miscellanous.Validator;

public class PositionFeatureRule implements FeatureRule {
	private Validator<Integer> x;
	private Validator<Integer> y;
	
	public PositionFeatureRule(Validator<Integer> x, Validator<Integer> y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean isValid(Feature feature) {
		Vector2D pos = feature.position();
		return x.isValid((int) Math.round(pos.x)) && y.isValid((int) Math.round(pos.y));
	}

}

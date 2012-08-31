package feature;

import math.Validator;

public class AngleFeatureRule implements FeatureRule {
	private Validator<Double> angle;
	
	public AngleFeatureRule(Validator<Double> angle) {
		this.angle = angle;
	}

	@Override
	public boolean isValid(Feature feature) {
		return angle.isValid(feature.angle().getRadians());
	}
}

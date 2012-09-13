package feature;

import validator.DValidator;

public class AngleFeatureRule implements FeatureRule {
	private DValidator angle;
	
	public AngleFeatureRule(DValidator angle) {
		this.angle = angle;
	}

	@Override
	public boolean isValid(Feature feature) {
		return angle.isValid(feature.getAngle().getRadians());
	}
}

package feature;

import validator.DoubleValidator;

public class AngleFeatureRule implements FeatureRule {
	private DoubleValidator angle;
	
	public AngleFeatureRule(DoubleValidator angle) {
		this.angle = angle;
	}

	@Override
	public boolean isValid(Feature feature) {
		return angle.isValid(feature.getAngle().getRadians());
	}
}

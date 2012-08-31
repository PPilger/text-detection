package feature;

import math.Validator;

public class AreaFeatureRule implements FeatureRule {
	private Validator<Double> area;
	
	public AreaFeatureRule(Validator<Double> area) {
		this.area = area;
	}

	@Override
	public boolean isValid(Feature feature) {
		return area.isValid(feature.area());
	}
}

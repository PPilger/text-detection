package feature;

import math.Validator;

public class SizeFeatureRule implements FeatureRule {
	private Validator<Double> width;
	private Validator<Double> height;
	
	public SizeFeatureRule(Validator<Double> width, Validator<Double> height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public boolean isValid(Feature feature) {
		return width.isValid(feature.width()) && height.isValid(feature.height());
	}
	
}

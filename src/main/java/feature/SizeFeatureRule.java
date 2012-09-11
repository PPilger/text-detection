package feature;

import validator.DoubleValidator;

public class SizeFeatureRule implements FeatureRule {
	private DoubleValidator width;
	private DoubleValidator height;
	
	public SizeFeatureRule(DoubleValidator width, DoubleValidator height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public boolean isValid(Feature feature) {
		return width.isValid(feature.getWidth()) && height.isValid(feature.getHeight());
	}
	
}

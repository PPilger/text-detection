package feature;

import validator.DValidator;

public class SizeFeatureRule implements FeatureRule {
	private DValidator width;
	private DValidator height;

	public SizeFeatureRule(DValidator width, DValidator height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public boolean isValid(Feature feature) {
		return width.isValid(feature.getWidth())
				&& height.isValid(feature.getHeight());
	}
}

package feature;

import validator.DoubleValidator;

public class AreaFeatureRule implements FeatureRule {
	private DoubleValidator area;
	
	public AreaFeatureRule(DoubleValidator area) {
		this.area = area;
	}

	@Override
	public boolean isValid(Feature feature) {
		return area.isValid(feature.getArea());
	}
}

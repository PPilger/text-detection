package feature;

import validator.DValidator;

public class AreaFeatureRule implements FeatureRule {
	private DValidator area;
	
	public AreaFeatureRule(DValidator area) {
		this.area = area;
	}

	@Override
	public boolean isValid(Feature feature) {
		return area.isValid(feature.getArea());
	}
}

package feature;

import java.util.*;

import validator.DoubleValidator;


public class AreaGrowthLinkingRule extends LinkingRule {
	private DoubleValidator areaGrowth;

	public AreaGrowthLinkingRule(DoubleValidator areaGrowth) {
		this.areaGrowth = areaGrowth;
	}

	@Override
	public boolean link(Feature f0, Feature f1) {
		LinkedFeature lf = LinkedFeature.create(Arrays.asList(f0, f1));
		double growth = lf.getArea()-f0.getArea()-f1.getArea();
		return areaGrowth.isValid(growth);
	}
}

package feature;

import java.util.*;

import validator.DValidator;


public class AreaGrowthLinkingRule extends LinkingRule {
	private DValidator areaGrowth;

	public AreaGrowthLinkingRule(DValidator areaGrowth) {
		this.areaGrowth = areaGrowth;
	}

	@Override
	public boolean link(Feature f0, Feature f1) {
		LinkedFeature lf = LinkedFeature.create(Arrays.asList(f0, f1));
		double growth = lf.getArea()-f0.getArea()-f1.getArea();
		return areaGrowth.isValid(growth);
	}
}

package feature;

import java.util.*;

import miscellanous.Validator;

public class AreaGrowthLinkingRule extends LinkingRule {
	private Validator<Double> areaGrowth;

	public AreaGrowthLinkingRule(Validator<Double> areaGrowth) {
		this.areaGrowth = areaGrowth;
	}

	@Override
	public boolean link(Feature f0, Feature f1) {
		LinkedFeature lf = LinkedFeature.create(Arrays.asList(f0, f1));
		double growth = lf.getArea()-f0.getArea()-f1.getArea();
		return areaGrowth.isValid(growth);
	}
}

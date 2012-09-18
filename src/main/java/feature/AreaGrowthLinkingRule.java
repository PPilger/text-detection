package feature;

import java.util.Arrays;

import validator.DValidator;

/**
 * Only permits links between features where area growth is valid. The area
 * growth is the difference between the area of both features summed up and the
 * area of the LinkedFeature created from them.
 * 
 * @author PilgerstorferP
 * 
 */
public class AreaGrowthLinkingRule extends LinkingRule {
	private DValidator areaGrowth;

	public AreaGrowthLinkingRule(DValidator areaGrowth) {
		this.areaGrowth = areaGrowth;
	}

	@Override
	public boolean link(Feature f0, Feature f1) {
		LinkedFeature lf = LinkedFeature.create(Arrays.asList(f0, f1));
		double growth = lf.getArea() - f0.getArea() - f1.getArea();
		return areaGrowth.isValid(growth);
	}
}

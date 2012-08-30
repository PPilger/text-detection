package feature;
import java.util.*;

import math.Interval;

public class AreaBasedLinkingRule extends LinkingRule {
	private Interval<Double> areaGrowth;

	public AreaBasedLinkingRule(Interval<Double> areaGrowth) {
		this.areaGrowth = areaGrowth;
	}

	@Override
	public boolean link(Feature f0, Feature f1) {
		LinkedFeature lf = LinkedFeature.create(Arrays.asList(f0, f1));
		double growth = lf.area()-f0.area()-f1.area();
		return areaGrowth.isValid(growth);
	}
}

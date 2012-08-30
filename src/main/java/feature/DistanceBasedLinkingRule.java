package feature;

import math.Interval;

public class DistanceBasedLinkingRule extends LinkingRule {
	private Interval<Double> distance;

	public DistanceBasedLinkingRule(Interval<Double> distance) {
		this.distance = distance;
	}

	@Override
	public boolean link(Feature f0, Feature f1) {
		double dist = f0.distance(f1);
		return distance.isValid(dist);
	}
}

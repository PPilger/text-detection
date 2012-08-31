package feature;

import math.Validator;

public class DistanceBasedLinkingRule extends LinkingRule {
	private Validator<Double> distance;

	public DistanceBasedLinkingRule(Validator<Double> distance) {
		this.distance = distance;
	}

	@Override
	public boolean link(Feature f0, Feature f1) {
		double dist = f0.distance(f1);
		return distance.isValid(dist);
	}
}

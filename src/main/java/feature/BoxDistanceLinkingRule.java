package feature;

import miscellanous.Validator;

public class BoxDistanceLinkingRule extends LinkingRule {
	private Validator<Double> distance;

	public BoxDistanceLinkingRule(Validator<Double> distance) {
		this.distance = distance;
	}

	@Override
	public boolean link(Feature f0, Feature f1) {
		double dist = f0.distance(f1);
		return distance.isValid(dist);
	}
}

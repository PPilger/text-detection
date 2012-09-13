package feature;

import validator.DValidator;

public class BoxDistanceLinkingRule extends LinkingRule {
	private DValidator distance;

	public BoxDistanceLinkingRule(DValidator distance) {
		this.distance = distance;
	}

	@Override
	public boolean link(Feature f0, Feature f1) {
		double dist = f0.distance(f1);
		return distance.isValid(dist);
	}
}

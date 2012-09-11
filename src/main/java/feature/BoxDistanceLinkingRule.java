package feature;

import validator.DoubleValidator;

public class BoxDistanceLinkingRule extends LinkingRule {
	private DoubleValidator distance;

	public BoxDistanceLinkingRule(DoubleValidator distance) {
		this.distance = distance;
	}

	@Override
	public boolean link(Feature f0, Feature f1) {
		double dist = f0.distance(f1);
		return distance.isValid(dist);
	}
}

package feature;

import validator.DValidator;

/**
 * Only permits links between features that have a valid box to box distance.
 * 
 * @author PilgerstorferP
 * 
 */
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

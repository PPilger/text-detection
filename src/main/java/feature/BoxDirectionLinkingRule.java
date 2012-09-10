package feature;

import math.Angle180;
import math.Vector;
import miscellanous.Validator;

public class BoxDirectionLinkingRule extends LinkingRule {
	private Validator<Double> difference;

	public BoxDirectionLinkingRule(Validator<Double> difference) {
		this.difference = difference;
	}

	@Override
	public boolean link(Feature f0, Feature f1) {
		Vector p0 = f0.getCenter();
		Vector p1 = f1.getCenter();
		Angle180 angle = new Angle180(p0, p1);

		double dangle;
		if (f0.getWidth() >= f1.getWidth()) {
			dangle = f0.getAngle().difference(angle).getRadians();
		} else {
			dangle = f1.getAngle().difference(angle).getRadians();
		}
		return difference.isValid(dangle);
	}
}

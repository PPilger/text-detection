package feature;

import math.Angle180;
import math.Vector2D;
import miscellanous.Validator;

public class BoxDirectionLinkingRule extends LinkingRule {
	private Validator<Double> difference;

	public BoxDirectionLinkingRule(Validator<Double> difference) {
		this.difference = difference;
	}

	@Override
	public boolean link(Feature f0, Feature f1) {
		Vector2D p0 = f0.position();
		Vector2D p1 = f1.position();
		Angle180 angle = new Angle180(p0, p1);

		double dangle;
		if (f0.width() >= f1.width()) {
			dangle = f0.angle().difference(angle).getRadians();
		} else {
			dangle = f1.angle().difference(angle).getRadians();
		}
		return difference.isValid(dangle);
	}
}

package feature;

import validator.DoubleValidator;

public class CenterDistanceLinkingRule  extends LinkingRule {
		private DoubleValidator distance;

		public CenterDistanceLinkingRule(DoubleValidator distance) {
			this.distance = distance;
		}

		@Override
		public boolean link(Feature f0, Feature f1) {
			double dist = f0.getCenter().distance(f1.getCenter());
			return distance.isValid(dist);
		}
}

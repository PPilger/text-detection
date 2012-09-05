package feature;

import miscellanous.Validator;

public class CenterDistanceLinkingRule  extends LinkingRule {
		private Validator<Double> distance;

		public CenterDistanceLinkingRule(Validator<Double> distance) {
			this.distance = distance;
		}

		@Override
		public boolean link(Feature f0, Feature f1) {
			double dist = f0.position().distance(f1.position());
			return distance.isValid(dist);
		}
}

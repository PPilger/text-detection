package feature;

import validator.DValidator;

public class RankingFeatureRule implements FeatureRule {
	private DValidator ranking;
	
	public RankingFeatureRule(DValidator ranking) {
		this.ranking = ranking;
	}

	@Override
	public boolean isValid(Feature feature) {
		return ranking.isValid(feature.getRating());
	}
}

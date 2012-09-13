package feature;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.javacv.cpp.opencv_core.IplImage;


public abstract class FeatureDetector {
	private List<FeatureRule> featureRules;

	public FeatureDetector() {
		this.featureRules = new ArrayList<FeatureRule>();
	}

	public void addRule(FeatureRule featureRule) {
		this.featureRules.add(featureRule);
	}
	
	public List<FeatureRule> getRules() {
		return featureRules;
	}

	public abstract int findFeatures(IplImage img, FeatureSet features);
}

import java.util.List;

import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class DistanceBasedLinkingRuleFactory implements LinkingRuleFactory {
	private double maxDistance;

	public DistanceBasedLinkingRuleFactory(double maxDistance) {
		this.maxDistance = maxDistance;
	}

	@Override
	public LinkingRule create(List<Feature> features, IplImage img) {
		return new DistanceBasedLinkingRule(maxDistance);
	}
}

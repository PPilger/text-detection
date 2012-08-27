import java.util.List;

import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class AreaBasedLinkingRuleFactory implements LinkingRuleFactory {
	private double maxAreaGrowth;

	public AreaBasedLinkingRuleFactory(double maxAreaGrowth) {
		this.maxAreaGrowth = maxAreaGrowth;
	}

	@Override
	public LinkingRule create(List<Feature> features, IplImage img) {
		return new AreaBasedLinkingRule(maxAreaGrowth);
	}
}

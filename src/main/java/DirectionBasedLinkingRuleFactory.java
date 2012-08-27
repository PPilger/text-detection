import java.util.List;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class DirectionBasedLinkingRuleFactory implements LinkingRuleFactory {
	private int filterSize;
	private int dilateSize;
	private int numAngles;
	private int lineWidth;
	private double minLinkRating;
	private double minFeatureRating;

	public DirectionBasedLinkingRuleFactory(int filterSize, int dilateSize,
			int numAngles, int lineWidth, double minLinkRating,
			double minFeatureRating) {
		this.filterSize = filterSize;
		this.dilateSize = dilateSize;
		this.numAngles = numAngles;
		this.lineWidth = lineWidth;
		this.minLinkRating = minLinkRating;
		this.minFeatureRating = minFeatureRating;
	}

	@Override
	public LinkingRule create(List<Feature> features, IplImage img) {
		return new DirectionBasedLinkingRule(features, img, filterSize,
				dilateSize, numAngles, lineWidth, minLinkRating,
				minFeatureRating);
	}

}

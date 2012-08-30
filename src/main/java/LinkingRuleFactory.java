import com.googlecode.javacv.cpp.opencv_core.IplImage;


public interface LinkingRuleFactory {
	public LinkingRule create(FeatureSet features, IplImage img);
}

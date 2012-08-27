import java.util.List;

import com.googlecode.javacv.cpp.opencv_core.IplImage;


public interface LinkingRuleFactory {
	public LinkingRule create(List<Feature> features, IplImage img);
}

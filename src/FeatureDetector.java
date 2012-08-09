import java.util.List;

import com.googlecode.javacv.cpp.opencv_core.IplImage;


public interface FeatureDetector {
	public List<Feature> findFeatures(IplImage img);
}

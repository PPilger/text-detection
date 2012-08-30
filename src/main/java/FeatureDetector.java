
import com.googlecode.javacv.cpp.opencv_core.IplImage;


public interface FeatureDetector {
	public FeatureSet findFeatures(IplImage img);
}

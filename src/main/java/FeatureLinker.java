import java.util.List;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public interface FeatureLinker {
	public FeatureSet linkFeatures(List<Feature> features, IplImage img);
}

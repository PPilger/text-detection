import com.googlecode.javacv.cpp.opencv_core.IplImage;


public abstract class LinkingRule {
	public void initialize(FeatureSet features, IplImage img) {
		
	}

	public abstract boolean link(Feature f0, Feature f1);
}

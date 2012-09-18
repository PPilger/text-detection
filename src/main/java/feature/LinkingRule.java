package feature;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * A LinkingRule is used to determine if two features can be linked together.
 * @author PilgerstorferP
 *
 */
public abstract class LinkingRule {
	public void initialize(FeatureSet features, IplImage img) {
	}

	public abstract boolean link(Feature f0, Feature f1);
}

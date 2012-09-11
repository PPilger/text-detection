package feature;

import validator.DoubleMaximum;

import com.googlecode.javacv.cpp.opencv_core.IplImage;


public interface FeatureDetector {
	public int findFeatures(IplImage img, FeatureSet features);
}

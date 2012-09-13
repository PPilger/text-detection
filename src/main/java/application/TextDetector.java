package application;

import image.Image;

import java.util.List;

import feature.FeatureSet;

public interface TextDetector {
	public String getName();

	public List<Image> getImage();

	public List<FeatureSet> getFeatures();

	public void imageProcessing();

	public void featureDetection();

	public void featureLinking();

	public void featureFiltering();

	public void featureMerging();
}

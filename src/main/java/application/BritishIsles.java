package application;

import feature.AreaGrowthLinkingRule;
import feature.BestDirectionFeatureLinker;
import feature.BoxDistanceLinkingRule;
import feature.ContourFeatureDetector;
import feature.FeatureDetector;
import feature.FeatureLinker;
import feature.FeatureSet;
import feature.SizeFeatureRule;
import image.BackgroundProcessor;
import image.ChromaticityProcessor;
import image.CloseProcessor;
import image.Image;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import validator.DInterval;
import validator.DMaximum;
import validator.DMinimum;
import validator.IMinimum;
import validator.Valid;

public class BritishIsles implements TextDetector {
	private Image image;
	private FeatureSet features;

	public BritishIsles() {
		image = new Image("samples" + File.separator + "British Isles S2.jpg");
		features = new FeatureSet(30, image.getWidth(), image.getHeight());
	}

	@Override
	public String getName() {
		return "BritishIsles";
	}

	@Override
	public List<Image> getImage() {
		return Arrays.asList(image);
	}

	@Override
	public List<FeatureSet> getFeatures() {
		return Arrays.asList(features);
	}

	@Override
	public void imageProcessing() {
		image.process(new BackgroundProcessor(51, new IMinimum(29)));
		image.process(new ChromaticityProcessor(DInterval.around(1.26, 0.21),
				DInterval.around(0.93, 0.14), DInterval.around(0.86, 0.08)));
		image.process(new CloseProcessor(3));
	}

	@Override
	public void featureDetection() {
		FeatureDetector detector = new ContourFeatureDetector(new IMinimum(10));
		detector.addRule(new SizeFeatureRule(new Valid(), new DMinimum(3)));

		detector.findFeatures(image.getImg(), features);
	}

	@Override
	public void featureLinking() {
		FeatureLinker linker = new BestDirectionFeatureLinker(new DMaximum(10),
				32);
		linker.addRule(new AreaGrowthLinkingRule(new DMaximum(800)));
		linker.addRule(new BoxDistanceLinkingRule(new DMaximum(16)));

		features = linker.link(features, image.getImg());
	}

	@Override
	public void featureFiltering() {
	}

	@Override
	public void featureMerging() {
	}
}

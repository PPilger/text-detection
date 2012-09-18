package application;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import validator.DInterval;
import validator.DMaximum;
import validator.DMinimum;
import validator.IInterval;
import validator.IMinimum;
import validator.Valid;

import feature.AreaFeatureRule;
import feature.BestDirectionFeatureLinker;
import feature.BoxDistanceLinkingRule;
import feature.ContourFeatureDetector;
import feature.FeatureDetector;
import feature.FeatureLinker;
import feature.FeatureSet;
import feature.FixedDirectionLinkingRule;
import feature.RankingFeatureRule;
import feature.SizeFeatureRule;
import image.Image;
import image.BackgroundProcessor;

public class Mooskirchen implements TextDetector {
	private Image image;
	private FeatureSet features;

	public Mooskirchen() {
		image = new Image("samples" + File.separator
				+ "Mooskirchen_Grazer_Feld.jpg");
		features = new FeatureSet(81, image.getWidth(), image.getHeight());
	}

	@Override
	public String getName() {
		return "Mooskirchen";
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
		image.process(new BackgroundProcessor(101, new IMinimum(50)));

	}

	@Override
	public void featureDetection() {
		FeatureDetector detector = new ContourFeatureDetector(new IInterval(40,
				1000000));

		detector.addRule(new AreaFeatureRule(new DInterval(100, 5000)));
		detector.addRule(new SizeFeatureRule(new Valid(), new DMinimum(5)));

		detector.findFeatures(image.getImg(), features);
	}

	@Override
	public void featureLinking() {
		FeatureLinker linker = new BestDirectionFeatureLinker(new DMaximum(30),
				1, new DInterval(0, 0));

		linker.addRule(new BoxDistanceLinkingRule(new DMaximum(81)));
		linker.addRule(new FixedDirectionLinkingRule(0, new Valid(),
				new DMinimum(19.5)));

		features = linker.link(features, image.getImg());
	}

	@Override
	public void featureFiltering() {
		features.remove(new AreaFeatureRule(new DMaximum(3000)));
		features.remove(new SizeFeatureRule(new DMaximum(30), new DMaximum(30)));
		features.remove(new RankingFeatureRule(new DMaximum(1.1)));
	}

	@Override
	public void featureMerging() {
	}
}

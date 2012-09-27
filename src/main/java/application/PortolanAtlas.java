package application;

import feature.AreaFeatureRule;
import feature.BestDirectionFeatureLinker;
import feature.CenterDistanceLinkingRule;
import feature.ContourFeatureDetector;
import feature.FeatureDetector;
import feature.FeatureLinker;
import feature.FeatureSet;
import feature.RankingFeatureRule;
import feature.SizeFeatureRule;
import image.BackgroundProcessor;
import image.BinaryProcessor;
import image.ChromaticityProcessor;
import image.CloseProcessor;
import image.EraseProcessor;
import image.Image;
import image.LineSegmentsProcessor;
import image.ThicknessProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import validator.DInterval;
import validator.DMaximum;
import validator.DMinimum;
import validator.IInterval;
import validator.IMaximum;
import validator.IMinimum;
import validator.Valid;

public class PortolanAtlas implements TextDetector {
	private Image smallImage;
	private Image bigImage;
	private FeatureSet smallFeatures;
	private FeatureSet bigFeatures;

	public PortolanAtlas() {
		smallImage = new Image("samples" + File.separator
				+ "PORTOLAN_ATLAS.jpg");
		bigImage = new Image(smallImage.getColor());
		smallFeatures = new FeatureSet(25, smallImage.getWidth(),
				smallImage.getHeight());
		bigFeatures = new FeatureSet(70, smallImage.getWidth(),
				smallImage.getHeight());
	}

	@Override
	public String getName() {
		return "PortolanAtlas";
	}

	@Override
	public List<Image> getImage() {
		return Arrays.asList(smallImage, bigImage);
	}

	@Override
	public List<FeatureSet> getFeatures() {
		List<FeatureSet> featureSets = new ArrayList<FeatureSet>();

		featureSets.add(smallFeatures);
		if (bigFeatures != null) {
			featureSets.add(bigFeatures);
		}

		return featureSets;
	}

	@Override
	public void imageProcessing() {
		Image lines = new Image(smallImage.getGray());
		{
			lines.process(new BackgroundProcessor(21, new IMinimum(29)));
			lines.process(new CloseProcessor(3));
			lines.process(new ThicknessProcessor(5, 1));
			lines.process(new LineSegmentsProcessor(110, new DMinimum(256),
					new DMaximum(64)));
		}

		{
			smallImage.process(new BackgroundProcessor(21, new IMinimum(29)));
			smallImage
					.process(new ChromaticityProcessor(
							new DInterval(1.01, 1.57),
							new DInterval(0.70, 1.02),
							new DInterval(0.46, 0.94)));
			smallImage.process(new EraseProcessor(lines.getImg()));
		}

		{
			bigImage.process(new BinaryProcessor(new IMaximum(140)));
			bigImage.process(new CloseProcessor(3));
		}

	}

	@Override
	public void featureDetection() {
		{
			FeatureDetector detector = new ContourFeatureDetector(
					new IInterval(5, 1000000));

			detector.addRule(new AreaFeatureRule(new DInterval(1, 2500)));
			detector.addRule(new SizeFeatureRule(new DMaximum(30), new Valid()));

			detector.findFeatures(smallImage.getImg(), smallFeatures);
		}

		{
			FeatureDetector detector = new ContourFeatureDetector(
					new IInterval(30, 240));

			detector.addRule(new AreaFeatureRule(new DInterval(70, 1800)));
			detector.addRule(new SizeFeatureRule(new DMinimum(16), new Valid()));

			detector.findFeatures(bigImage.getImg(), bigFeatures);
		}
	}

	@Override
	public void featureLinking() {
		{
			FeatureLinker linker = new BestDirectionFeatureLinker(new DMaximum(
					8), 16);

			linker.addRule(new CenterDistanceLinkingRule(new DMaximum(20)));

			smallFeatures = linker.link(smallFeatures, smallImage.getImg());
		}

		{
			FeatureLinker linker = new BestDirectionFeatureLinker(new DMaximum(
					25), 1);

			linker.addRule(new CenterDistanceLinkingRule(new DInterval(40, 70)));

			bigFeatures = linker.link(bigFeatures, bigImage.getImg());
		}
	}

	@Override
	public void featureFiltering() {
		smallFeatures.keep(new SizeFeatureRule(new DMinimum(30),
				new DMinimum(8)));
		smallFeatures.keep(new RankingFeatureRule(new DMinimum(1)));

		bigFeatures.keep(new SizeFeatureRule(new DMinimum(100),
				new DMinimum(16)));

	}

	@Override
	public void featureMerging() {
		smallFeatures.merge(bigFeatures, new DMaximum(0.5));
		bigFeatures = null;
	}
}

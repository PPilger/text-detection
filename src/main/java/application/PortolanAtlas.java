package application;

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

import feature.AreaFeatureRule;
import feature.AreaGrowthLinkingRule;
import feature.BestDirectionFeatureLinker;
import feature.BoxDistanceLinkingRule;
import feature.CenterDistanceLinkingRule;
import feature.ContourFeatureDetector;
import feature.FeatureDetector;
import feature.FeatureLinker;
import feature.FeatureSet;
import feature.SizeFeatureRule;
import image.BigObjectEraseProcessor;
import image.CloseProcessor;
import image.DilateProcessor;
import image.EraseProcessor;
import image.FirstDerivateEraseProcessor;
import image.Image;
import image.ImageDisplay;
import image.InvertProcessor;
import image.LineSegmentsProcessor;
import image.ObstacleRemoveProcessor;
import image.SecondDerivateProcessor;
import image.PerimeterProcessor;
import image.ThicknessProcessor;
import image.BinaryProcessor;
import image.DensityProcessor;

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
		if (bigFeatures == null) {
			featureSets.add(bigFeatures);
		}
		
		return featureSets;
	}

	@Override
	public void imageProcessing() {
		ImageDisplay display = new ImageDisplay("output1", 1200, 800);

		Image lines = new Image(smallImage.getGray());
		{

			lines.process(new BinaryProcessor(new IMaximum(165)));
			// Image.write(lines.getImg(), "lines3.jpg");
			lines.process(new BigObjectEraseProcessor(4));
			// Image.write(lines.getImg(), "lines2.jpg");
			lines.process(new PerimeterProcessor(new IMaximum(19)));
			lines.process(new LineSegmentsProcessor(90, 256, 64));// 40,50,800
		}

		{
			smallImage.process(new BinaryProcessor(new IMaximum(165)));

			smallImage.process(new CloseProcessor(3));
			smallImage.process(new BigObjectEraseProcessor(11));
			smallImage.process(new PerimeterProcessor(new IMaximum(9)));

			smallImage.process(new ThicknessProcessor(new IMaximum(7)));

			smallImage.process(new EraseProcessor(lines.getImg()));

			smallImage.process(new FirstDerivateEraseProcessor(120, 9));
			smallImage.process(new SecondDerivateProcessor(3, new IMinimum(130), 9));

			smallImage.process(new DensityProcessor(11, 17, new IMinimum(50)));
			smallImage.process(new ObstacleRemoveProcessor(3, 3));

			smallImage.process(new DilateProcessor(3));
			smallImage.process(new CloseProcessor(3));
		}

		{
			bigImage.process(new BinaryProcessor(new IMaximum(140)));
			bigImage.process(new CloseProcessor());
		}

	}

	@Override
	public void featureDetection() {
		{
			FeatureDetector detector = new ContourFeatureDetector(
					new IInterval(15, 1000000));

			detector.addRule(new AreaFeatureRule(new DInterval(1, 5000)));

			detector.findFeatures(smallImage.getImg(), smallFeatures);
		}

		{
			FeatureDetector detector = new ContourFeatureDetector(
					new IInterval(30, 240));
			detector.addRule(new AreaFeatureRule(new DInterval(1, 5000)));

			detector.findFeatures(bigImage.getImg(), bigFeatures);

			bigFeatures.dontRemove(new SizeFeatureRule(new DMinimum(16),
					new Valid()));
			bigFeatures
					.dontRemove(new AreaFeatureRule(new DInterval(70, 1800)));
		}
	}

	@Override
	public void featureLinking() {
		{
			FeatureLinker linker = new BestDirectionFeatureLinker(new DMaximum(
					10), 16);

			linker.addRule(new BoxDistanceLinkingRule(new DMaximum(22)));
			linker.addRule(new AreaGrowthLinkingRule(new DMaximum(400)));

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
		smallFeatures.dontRemove(new SizeFeatureRule(new DMinimum(30),
				new DMinimum(8)));

		bigFeatures.dontRemove(new SizeFeatureRule(new DMinimum(100),
				new DMinimum(16)));

	}

	@Override
	public void featureMerging() {
		smallFeatures.merge(bigFeatures);
		bigFeatures = null;
	}
}

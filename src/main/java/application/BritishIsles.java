package application;

import static com.googlecode.javacv.cpp.opencv_core.cvNot;
import static com.googlecode.javacv.cpp.opencv_core.cvSet;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import validator.DInterval;
import validator.DMaximum;
import validator.DMinimum;
import validator.IInterval;
import validator.IMinimum;
import validator.Valid;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;

import feature.*;
import image.*;

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
		ImageDisplay display = new ImageDisplay("output", 1200, 800);

		/*Image lines = new Image(image.getGray());
		{
			lines.process(new ThresholdProcessor(210));
			lines.process(new InvertProcessor());
			lines.process(new CloseProcessor(5));
			lines.process(new ThicknessProcessor(1, 3));
			lines.process(new LineSegmentsProcessor(50, 100, 50));
		}*/

		{
			image.process(new RemoveBackgroundProcessor(51, 30));
			
			image.process(new ChromacityProcessor(DInterval.around(1.26, 0.21),
					DInterval.around(0.93, 0.14), DInterval.around(0.86, 0.08)));
			image.process(new CloseProcessor(3));

			//cvNot(image.getImg(), image.getImg());
			//cvSet(image.getColor(), CvScalar.ZERO, image.getImg());
			display.show(image.getImg());

//			image.process(new ThresholdProcessor(190));// 207
//			image.process(new InvertProcessor());
//			image.process(new ColorEraseProcessor(0, 100, 0, 50, 0, 50, 10,
//					false));
//			image.process(new ThicknessProcessor(1, 5));
//			image.process(new EraseProcessor(lines.getImg()));
//			image.process(new DilateProcessor(3));
//			image.process(new CloseProcessor(3));

		}
	}

	@Override
	public void featureDetection() {
		FeatureDetector detector = new ContourFeatureDetector(new IMinimum(10)); //20
		detector.addRule(new SizeFeatureRule(new Valid(), new DMinimum(3)));
		//detector.addRule(new AreaFeatureRule(new DInterval(100, 5000)));

		detector.findFeatures(image.getImg(), features);
	}

	@Override
	public void featureLinking() {
		FeatureLinker linker = new BestDirectionFeatureLinker(new DMaximum(10), 16);//new FeatureLinker();
		linker.addRule(new AreaGrowthLinkingRule(new DMaximum(800)));//800
		linker.addRule(new BoxDistanceLinkingRule(new DMaximum(20)));

		features = linker.link(features, image.getImg());
	}

	@Override
	public void featureFiltering() {
		//features.remove(new SizeFeatureRule(new DMaximum(40), new DMaximum(20)));
	}

	@Override
	public void featureMerging() {
	}
}

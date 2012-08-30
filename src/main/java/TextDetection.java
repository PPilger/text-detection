import feature.AreaBasedLinkingRule;
import feature.ContourBasedFeatureDetector;
import feature.DirectionBasedLinkingRule;
import feature.DistanceBasedLinkingRule;
import feature.FeatureDetector;
import feature.FeatureLinker;
import feature.FeatureSet;
import image.BigObjectEraseProcessor;
import image.CloseProcessor;
import image.ColorEraseProcessor;
import image.DilateProcessor;
import image.EraseProcessor;
import image.FirstDerivateEraseProcessor;
import image.Image;
import image.ImageDisplay;
import image.InvertProcessor;
import image.LineSegmentsProcessor;
import image.ObstacleRemoveProcessor;
import image.RemoveLinesProcessor;
import image.SecondDerivateEraseProcessor;
import image.SmallObjectErasorProcessor;
import image.ThicknessProcessor;
import image.ThresholdProcessor;
import image.VarianceProcessor;

import java.io.File;
import java.util.List;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;

import static com.googlecode.javacv.cpp.opencv_photo.*;

public class TextDetection {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		//britishIsles();
		//portolanAtlas();
		mooskirchen();
	}

	public static void mooskirchen() {
		Image image = new Image("samples" + File.separator
				+ "Mooskirchen_Grazer_Feld_S.jpg");

		ImageDisplay display = new ImageDisplay("output", 1200, 800);
		ImageDisplay display2 = new ImageDisplay("output2", 1200, 800);
		ImageDisplay display3 = new ImageDisplay("output3", 1200, 800);

		image.process(new ThresholdProcessor(75));// 75
		image.process(new InvertProcessor());
		image.process(new SmallObjectErasorProcessor(35));
		image.process(new ThicknessProcessor(2, 100));
		image.process(new DilateProcessor(3));

		display.show(image.getImg());

		FeatureSet features;
		{
			FeatureDetector detector = new ContourBasedFeatureDetector(35,
					1000000, 100, 5000);

			features = detector.findFeatures(image.getImg());
			System.out.println("number of features detected: "
					+ features.size());
			features.draw(image.getResult(), CvScalar.BLACK);
			display.show(image.getResult());
		}

		{
			FeatureLinker linker = new FeatureLinker();
			linker.addRule(new AreaBasedLinkingRule(500));

			features = linker.link(features, image.getImg());
			System.out.println("number of features after linking: "
					+ features.size());
			features.draw(image.getResult(), CvScalar.GREEN);
		}
		display.show(image.getResult());

		features.save("Mooskirchen Features.js");
		image.save("Mooskirchen.png");
	}

	public static void portolanAtlas() {
		Image image = new Image("samples" + File.separator
				+ "PORTOLAN_ATLAS_S.jpg");
		ImageDisplay display = new ImageDisplay("output1", 1200, 800);
		ImageDisplay display2 = new ImageDisplay("output2", 1200, 800);

		Image lines = new Image(image.getGray());
		{

			lines.process(new ThresholdProcessor(165));
			lines.process(new InvertProcessor());
			lines.process(new BigObjectEraseProcessor(3));
			lines.process(new SmallObjectErasorProcessor(20));
			lines.process(new LineSegmentsProcessor(40, 50, 800));

			/*
			 * lines.process(new DilateProcessor(3));
			 * display.show(image.getColor()); cvInpaint(image.getColor(),
			 * lines.getImg(), image.getColor(), 20, CV_INPAINT_NS);
			 * display2.show(image.getColor());
			 */
			// display.show(lines.getImg(), image.getGray());
		}

		{
			image.process(new ThresholdProcessor(165));
			image.process(new InvertProcessor());

			// display.show(image.getImg());
			image.process(new CloseProcessor(3));
			image.process(new BigObjectEraseProcessor(11));
			// display.show(image.getImg());
			image.process(new SmallObjectErasorProcessor(10));
			// display.show(image.getImg());
			image.process(new ThicknessProcessor(1, 7));

			image.process(new EraseProcessor(lines.getImg()));

			// img.process(new RGRatioDisplayProcessor("1", 1200, 800, 0.5));
			// img.process(new RBRatioDisplayProcessor("1", 1200, 800, 0.5));
			// img.process(new GBRatioDisplayProcessor("1", 1200, 800, 0.5));

			// img.process(new RGBRatioEraseProcessor(0.9, 3, 1, 3, 1, 3, 2,
			// true));
			// img.process(new RGBRatioEraseProcessor(0.9, 3, 0.8, 3, 0.95, 3,
			// 2,
			// true));

			image.process(new FirstDerivateEraseProcessor(120, 9));
			image.process(new SecondDerivateEraseProcessor(130, 9));

			// image.process(new CloseProcessor(5));
			// display.show(image.getImg());

			/*
			 * Image bla = new Image(image.getImg()); { bla.process(new
			 * InvertProcessor()); for(int i = 0; i < 4; i++) {
			 * display2.show(bla.getImg()); bla.process(new
			 * ObstacleRemoveProcessor(3, 7, 25)); } bla.process(new
			 * InvertProcessor());
			 * 
			 * bla.process(new OpenProcessor(3)); } image.process(new
			 * MaskProcessor(bla.getImg()));
			 * 
			 * for(int i = 0; i < 4; i++) { display2.show(image.getImg());
			 * image.process(new ObstacleRemoveProcessor(3, 3, 25)); }
			 */

			image.process(new VarianceProcessor(11, 50));
			// display2.show(image.getImg());
			image.process(new ObstacleRemoveProcessor(3, 3));
			// display2.show(image.getImg());

			image.process(new DilateProcessor(3));
			image.process(new CloseProcessor(3));

			display.show(image.getImg());

			// image.process(new DilateProcessor(3));
			// display.show(image.getColor());
			// cvInpaint(image.getColor(), image.getImg(), image.getColor(), 20,
			// CV_INPAINT_TELEA);
			// display2.show(image.getColor());
		}

		FeatureDetector detector = new ContourBasedFeatureDetector(15, 1000000,
				1, 5000);
		FeatureLinker linker = new FeatureLinker();
		linker.addRule(new DistanceBasedLinkingRule(20));
		linker.addRule(new DirectionBasedLinkingRule(51, 1, 8, 1,
				0.3, 0.3));// 51

		FeatureSet features;

		features = detector.findFeatures(image.getImg());
		System.out.println("number of features detected: " + features.size());
		features.draw(image.getResult(), CvScalar.BLACK);
		display.show(image.getResult());

		features = linker.link(features, image.getImg());
		System.out.println("number of features after linking: "
				+ features.size());
		features.draw(image.getResult(), CvScalar.GREEN);
		display.show(image.getResult());

		features.save("Portolan Atlas Features.js");
		image.save("Portolan Atlas.jpg");
	}

	public static void britishIsles() {
		Image image = new Image("samples" + File.separator
				+ "British Isles.png");

		image.process(new ThresholdProcessor(207));
		image.process(new InvertProcessor());
		image.process(new ColorEraseProcessor(0, 100, 0, 50, 0, 50, 10, false));
		image.process(new ThicknessProcessor(1, 5));
		image.process(new RemoveLinesProcessor(60));
		image.process(new DilateProcessor(3));
		image.process(new CloseProcessor(3));

		FeatureDetector detector = new ContourBasedFeatureDetector(20, 1000000,
				100, 5000);
		FeatureLinker linker = new FeatureLinker();
		linker.addRule(new AreaBasedLinkingRule(1000));

		FeatureSet features;

		features = detector.findFeatures(image.getImg());
		System.out.println("number of features detected: " + features.size());
		features.draw(image.getResult(), CvScalar.BLACK);

		features = linker.link(features, image.getImg());
		System.out.println("number of features after linking: "
				+ features.size());
		features.draw(image.getResult(), CvScalar.GREEN);

		ImageDisplay display = new ImageDisplay("output", 1200, 800);
		display.show(image.getResult());

		features.save("British Isles Features.js");
		image.save("British Isles.png");
	}
}

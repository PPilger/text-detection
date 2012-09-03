package application;

import feature.*;
import image.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import math.*;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;

import static com.googlecode.javacv.cpp.opencv_photo.*;

public class TextDetection {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		// britishIsles();
		// portolanAtlas();
		mooskirchen();

		System.out.println(counter);
	}

	private static Stack<Long> timestamps = new Stack<Long>();

	public static void start() {
		timestamps.push(System.currentTimeMillis());
	}

	public static void stop(String title) {
		long t1 = System.currentTimeMillis();
		long t0 = timestamps.pop();

		long diff = t1 - t0;

		System.out.format(title + ": %d min, %d s, %d ms", diff / 1000 / 60,
				(diff / 1000) % 60, diff % 1000);
		System.out.println();
	}

	private static Map<String, Integer> counter = new HashMap<String, Integer>();

	public static void count(String key) {
		int value = 1;

		if (counter.containsKey(key)) {
			value = counter.get(key) + 1;
		}

		counter.put(key, value);
	}

	public static void mooskirchen() {
		Image image = new Image("samples" + File.separator
				+ "Mooskirchen_Grazer_Feld.jpg");

		ImageDisplay display = new ImageDisplay("output", 1200, 800);

		start();
		image.process(new ThresholdProcessor(75));// 75
		image.process(new InvertProcessor());
		image.process(new SmallObjectErasorProcessor(35));
		image.process(new ThicknessProcessor(2, 100));
		image.process(new DilateProcessor(3));
		stop("processing");

		// display.show(image.getImg());

		start();
		FeatureSet features;
		{
			FeatureRule rule;
			rule = new AreaFeatureRule(new Interval<Double>(100., 5000.));

			FeatureDetector detector = new ContourBasedFeatureDetector(
					new Interval<Integer>(35, 1000000),
					new Maximum<Double>(81.), rule);

			features = detector.findFeatures(image.getImg());
			System.out.println("number of features detected: "
					+ features.size());
		}
		stop("detection");

		features.draw(image.getColor(), CvScalar.BLACK);
		// display.show(image.getColor());

		start();
		{
			FeatureLinker linker = new FeatureLinker();
			linker.addRule(new DistanceBasedLinkingRule(
					new Maximum<Double>(81.)));

			linker.addRule(new FixedDirectionLinkingRule(0,
					new Valid<Integer>(), new Minimum<Integer>(20)));

			features = linker.link(features, image.getImg());
			System.out.println("number of features after linking: "
					+ features.size());
		}
		stop("linking");

		features.remove(new AreaFeatureRule(new Maximum<Double>(3000.)));
		features.remove(new SizeFeatureRule(new Maximum<Double>(30.),
				new Maximum<Double>(30.)));
		System.out.println("number of features after removing: "
				+ features.size());

		features.draw(image.getColor(), CvScalar.GREEN);
		display.show(image.getColor());

		features.write("Mooskirchen Features.js");
		Image.write(image.getColor(), "Mooskirchen.jpg");
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

		FeatureDetector detector = new ContourBasedFeatureDetector(
				new Interval<Integer>(15, 1000000), new Maximum<Double>(20.),
				new AreaFeatureRule(new Interval<Double>(1., 5000.)));
		FeatureLinker linker = new FeatureLinker();
		linker.addRule(new DistanceBasedLinkingRule(new Maximum<Double>(20.)));
		linker.addRule(new DirectionBasedLinkingRule(51, 1, 8, 1,
				new Minimum<Double>(.3)));// 51

		FeatureSet features;

		features = detector.findFeatures(image.getImg());
		System.out.println("number of features detected: " + features.size());
		features.draw(image.getColor(), CvScalar.BLACK);
		display.show(image.getColor());

		features = linker.link(features, image.getImg());
		System.out.println("number of features after linking: "
				+ features.size());
		features.draw(image.getColor(), CvScalar.GREEN);
		display.show(image.getColor());

		features.write("Portolan Atlas Features.js");
		Image.write(image.getColor(), "Portolan Atlas.jpg");
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

		FeatureDetector detector = new ContourBasedFeatureDetector(
				new Interval<Integer>(20, 1000000), new Maximum<Double>(200.),
				new AreaFeatureRule(new Interval<Double>(100., 5000.)));
		FeatureLinker linker = new FeatureLinker();
		linker.addRule(new AreaBasedLinkingRule(new Maximum<Double>(1000.)));

		FeatureSet features;

		features = detector.findFeatures(image.getImg());
		System.out.println("number of features detected: " + features.size());
		features.draw(image.getColor(), CvScalar.BLACK);

		features = linker.link(features, image.getImg());
		System.out.println("number of features after linking: "
				+ features.size());
		features.draw(image.getColor(), CvScalar.GREEN);

		ImageDisplay display = new ImageDisplay("output", 1200, 800);
		display.show(image.getColor());

		features.write("British Isles Features.js");
		Image.write(image.getColor(), "British Isles.png");
	}
}

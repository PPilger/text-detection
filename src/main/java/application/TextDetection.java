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
		portolanAtlas();
		// mooskirchen();

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
		FeatureSet features = new FeatureSet(81, image.getWidth(),
				image.getHeight());
		{
			FeatureRule rule;
			rule = new AreaFeatureRule(new Interval<Double>(100., 5000.));

			FeatureDetector detector = new ContourBasedFeatureDetector(
					new Interval<Integer>(35, 1000000), rule);

			int num = detector.findFeatures(image.getImg(), features);
			System.out.println("number of features detected: " + num);
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

			image.process(new CloseProcessor(3));
			image.process(new BigObjectEraseProcessor(11));
			image.process(new SmallObjectErasorProcessor(10));

			image.process(new ThicknessProcessor(1, 7));

			image.process(new EraseProcessor(lines.getImg()));

			image.process(new FirstDerivateEraseProcessor(120, 9));
			image.process(new SecondDerivateEraseProcessor(130, 9));

			image.process(new VarianceProcessor(11, 50));
			image.process(new ObstacleRemoveProcessor(3, 3));

			image.process(new DilateProcessor(3));
			image.process(new CloseProcessor(3));

			display.show(image.getImg());

			// image.process(new DilateProcessor(3));
			// display.show(image.getColor());
			// cvInpaint(image.getColor(), image.getImg(), image.getColor(), 20,
			// CV_INPAINT_TELEA);
			// display2.show(image.getColor());
		}

		FeatureSet features = new FeatureSet(20, image.getWidth(),
				image.getHeight());

		{
			FeatureRule rule;
			rule = new AreaFeatureRule(new Interval<Double>(1., 5000.));

			FeatureDetector detector = new ContourBasedFeatureDetector(
					new Interval<Integer>(15, 1000000), rule);

			int num = detector.findFeatures(image.getImg(), features);
			System.out.println("number of features detected: " + num);
		}
		//features.dontRemove(new LocationFeatureRule(new Interval<Integer>(780,
		//		800), new Interval<Integer>(650, 680)));

		features.draw(image.getColor(), CvScalar.BLACK);
		display.show(image.getColor());

		start();
		{
			FeatureLinker linker = new FeatureLinker();

			linker.addRule(new DistanceBasedLinkingRule(
					new Maximum<Double>(20.)));
			linker.addRule(new AreaGrowthLinkingRule(new Maximum<Double>(400.)));
			linker.addRule(new DirectionBasedLinkingRule(51, 1, 8, 1,
					new Minimum<Double>(.3), new Valid<Integer>(), new Minimum<Integer>(3)));

			features = linker.link(features, image.getImg());
			System.out.println("number of features after linking: "
					+ features.size());
		}
		stop("linking");
		
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

		FeatureSet features = new FeatureSet(200, image.getWidth(),
				image.getHeight());

		{
			FeatureRule rule;
			rule = new AreaFeatureRule(new Interval<Double>(100., 5000.));

			FeatureDetector detector = new ContourBasedFeatureDetector(
					new Interval<Integer>(20, 1000000), rule);

			int num = detector.findFeatures(image.getImg(), features);
			System.out.println("number of features detected: " + num);
		}
		features.draw(image.getColor(), CvScalar.BLACK);

		{
			FeatureLinker linker = new FeatureLinker();

			linker.addRule(new AreaGrowthLinkingRule(new Maximum<Double>(1000.)));

			features = linker.link(features, image.getImg());
			System.out.println("number of features after linking: "
					+ features.size());
		}
		features.draw(image.getColor(), CvScalar.GREEN);

		ImageDisplay display = new ImageDisplay("output", 1200, 800);
		display.show(image.getColor());

		features.write("British Isles Features.js");
		Image.write(image.getColor(), "British Isles.png");
	}
}

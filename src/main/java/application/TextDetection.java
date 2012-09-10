package application;

import feature.*;
import image.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import math.*;
import miscellanous.Interval;
import miscellanous.Maximum;
import miscellanous.Minimum;
import miscellanous.Valid;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;

import static com.googlecode.javacv.cpp.opencv_photo.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

public class TextDetection {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		britishIsles();
		//portolanAtlas();
		//mooskirchen();

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

			FeatureDetector detector = new ContourFeatureDetector(
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
			linker.addRule(new BoxDistanceLinkingRule(new Maximum<Double>(81.)));

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
				+ "PORTOLAN_ATLAS.jpg");
		ImageDisplay display = new ImageDisplay("output1", 1200, 800);
		ImageDisplay display2 = new ImageDisplay("output2", 1200, 800);

		Image lines = new Image(image.getGray());
		{

			lines.process(new ThresholdProcessor(165));
			lines.process(new InvertProcessor());
			//Image.write(lines.getImg(), "lines3.jpg");
			lines.process(new BigObjectEraseProcessor(4));
			//Image.write(lines.getImg(), "lines2.jpg");
			lines.process(new SmallObjectErasorProcessor(20));
			Image.write(lines.getImg(), "lines1.jpg");
			lines.process(new LineSegmentsProcessor(90, 256, 64));//40,50,800

			/*
			 * lines.process(new DilateProcessor(3));
			 * display.show(image.getColor()); cvInpaint(image.getColor(),
			 * lines.getImg(), image.getColor(), 20, CV_INPAINT_NS);
			 * display2.show(image.getColor());
			 */
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

		Image bigImage = new Image(image.getColor());
		{
			bigImage.process(new ThresholdProcessor(140));
			bigImage.process(new InvertProcessor());
			bigImage.process(new CloseProcessor());
		}
		
		//detect small text
		FeatureSet smallFeatures;
		{
			smallFeatures = new FeatureSet(25, image.getWidth(),
					image.getHeight());
			
			{
				FeatureRule rule;
				rule = new AreaFeatureRule(new Interval<Double>(1., 5000.));

				FeatureDetector detector = new ContourFeatureDetector(
						new Interval<Integer>(15, 1000000), rule);

				int num = detector.findFeatures(image.getImg(), smallFeatures);
				System.out.println("number of features detected: " + num);
			}

			{
				FeatureLinker linker = new BestDirectionFeatureLinker(16,
						new Maximum<Double>(10.));

				linker.addRule(new BoxDistanceLinkingRule(new Maximum<Double>(
						22.)));
				linker.addRule(new AreaGrowthLinkingRule(new Maximum<Double>(
						400.)));

				smallFeatures = linker.link(smallFeatures, image.getImg());

				System.out.println("number of features after linking: "
						+ smallFeatures.size());
			}
			smallFeatures.dontRemove(new SizeFeatureRule(new Minimum<Double>(30.),
					new Minimum<Double>(8.)));
		}

		//detect big text
		FeatureSet bigFeatures;
		{
			bigFeatures = new FeatureSet(70, image.getWidth(), image.getHeight());

			{
				FeatureRule rule;
				rule = new AreaFeatureRule(new Interval<Double>(1., 5000.));

				FeatureDetector detector = new ContourFeatureDetector(
						new Interval<Integer>(30, 240), rule);

				int num = detector.findFeatures(bigImage.getImg(), bigFeatures);
				System.out.println("number of features detected: " + num);
			}
			bigFeatures.dontRemove(new SizeFeatureRule(new Minimum<Double>(16.),
					new Valid<Double>()));
			bigFeatures.dontRemove(new AreaFeatureRule(new Interval<Double>(70.,
					1800.)));

			{
				FeatureLinker linker = new BestDirectionFeatureLinker(1,
						new Maximum<Double>(25.));

				linker.addRule(new CenterDistanceLinkingRule(
						new Interval<Double>(40., 70.)));

				bigFeatures = linker.link(bigFeatures, bigImage.getImg());

				System.out.println("number of features after linking: "
						+ bigFeatures.size());
			}
			bigFeatures.dontRemove(new SizeFeatureRule(new Minimum<Double>(100.),
					new Minimum<Double>(16.)));
		}
		
		//merge big and small features
		FeatureSet features = smallFeatures;

		features.merge(bigFeatures);
		System.out.println("number of features after merging: "
				+ features.size());
		
		features.draw(image.getColor(), CvScalar.BLUE);
		display.show(image.getColor());

		features.write("Portolan Atlas Features.js");
		Image.write(image.getColor(), "Portolan Atlas.jpg");
	}

	public static void britishIsles() {
		Image image = new Image("samples" + File.separator
				+ "British Isles S.jpg");

		ImageDisplay display = new ImageDisplay("output", 1200, 800);
		
		image.process(new ThresholdProcessor(190));//207
		image.process(new InvertProcessor());
		image.process(new ColorEraseProcessor(0, 100, 0, 50, 0, 50, 10, false));
		image.process(new ThicknessProcessor(1, 5));
		//image.process(new RemoveLinesProcessor(60));
		image.process(new DilateProcessor(3));
		image.process(new CloseProcessor(3));

		FeatureSet features = new FeatureSet(30, image.getWidth(),
				image.getHeight());

		{
			FeatureRule rule;
			rule = new AreaFeatureRule(new Interval<Double>(100., 5000.));

			FeatureDetector detector = new ContourFeatureDetector(
					new Interval<Integer>(20, 1000000), rule);

			int num = detector.findFeatures(image.getImg(), features);
			System.out.println("number of features detected: " + num);
		}

		{
			FeatureLinker linker = new FeatureLinker();

			linker.addRule(new AreaGrowthLinkingRule(new Maximum<Double>(1000.)));
			linker.addRule(new BoxDistanceLinkingRule(new Maximum<Double>(10.)));

			features = linker.link(features, image.getImg());
			System.out.println("number of features after linking: "
					+ features.size());
		}
		features.remove(new SizeFeatureRule(new Maximum<Double>(40.), new Maximum<Double>(20.)));
		System.out.println("number of features after removing: "
				+ features.size());

		for(Feature f : features) {
			f.write(image.getColor(), "British Isles", 10);
		}
		
		features.draw(image.getColor(), CvScalar.GREEN);
		display.show(image.getColor());

		features.write("British Isles Features.js");
		Image.write(image.getColor(), "British Isles.jpg");
		
	}
}

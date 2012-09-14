package application;

import feature.*;
import image.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.googlecode.javacv.cpp.opencv_core.CvScalar;

import static com.googlecode.javacv.cpp.opencv_photo.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

public class TextDetection {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		TextDetector detector = new BritishIsles();
		//TextDetector detector = new PortolanAtlas();

		//TextDetector detector = new Mooskirchen();

		start();
		{
			start();
			detector.imageProcessing();
			stop("image processing");

			start();
			detector.featureDetection();
			stop("feature detection");
			for (FeatureSet features : detector.getFeatures()) {
				System.out.println("number of features detected: "
						+ features.size());
			}

			start();
			detector.featureLinking();
			stop("feature linking");
			for (FeatureSet features : detector.getFeatures()) {
				System.out.println("number of features after linking: "
						+ features.size());
			}

			start();
			detector.featureFiltering();
			stop("feature filtering");
			for (FeatureSet features : detector.getFeatures()) {
				System.out.println("number of features after filtering: "
						+ features.size());
			}
			
			detector.featureMerging();
		}
		stop("total");

		// writeFeatures(detector);
		writeJSON(detector);
		writeImage(detector);
		// writeInpaint(detector);
		// displayImage(detector);

		System.out.println(counter);
	}

	private static void writeFeatures(TextDetector detector) {
		Image image = detector.getImage().get(0);
		FeatureSet features = detector.getFeatures().get(0);

		System.out.println("writing feature images");
		features.write(image.getColor(), detector.getName(), 10);
	}

	private static void writeJSON(TextDetector detector) {
		FeatureSet features = detector.getFeatures().get(0);

		System.out.println("writing JSON file");
		features.writeJSON(detector.getName() + "Features.js");
	}

	private static void writeImage(TextDetector detector) {
		Image image = detector.getImage().get(0);
		FeatureSet features = detector.getFeatures().get(0);

		ImageDisplay display = new ImageDisplay("features", 1200, 800);

		System.out.println("writing image");
		
		IplImage img = image.getColor().clone();
		features.draw(img, CvScalar.GREEN);
		Image.write(img, detector.getName() + ".jpg");
	}

	private static void writeInpaint(TextDetector detector) {
		Image image = detector.getImage().get(0);
		FeatureSet features = detector.getFeatures().get(0);
		
		System.out.println("writing inpainted image");

		IplImage orig = image.getColor().clone();
		IplImage mask = image.getTemp();

		cvSetZero(mask);
		features.fill(mask, CvScalar.WHITE);
		new DilateProcessor(5).process(mask, null);

		cvInpaint(orig, mask, orig, 2, CV_INPAINT_NS);

		Image.write(orig, detector.getName() + "Inpainted.jpg");
	}

	private static void displayImage(TextDetector detector) {
		Image image = detector.getImage().get(0);
		FeatureSet features = detector.getFeatures().get(0);

		ImageDisplay display = new ImageDisplay("features", 1200, 800);

		IplImage img = image.getColor().clone();
		features.draw(img, CvScalar.GREEN);
		display.show(img);
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
}

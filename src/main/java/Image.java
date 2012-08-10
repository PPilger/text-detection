import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

import java.util.*;

public class Image {
	IplImage img;
	List<ImageProcessor> processors;

	public Image(String filename) {
		this.img = cvLoadImage(filename);
		this.processors = new ArrayList<ImageProcessor>();
	}

	public Image(IplImage img) {
		this.img = img;
		this.processors = new ArrayList<ImageProcessor>();
	}

	public void show() {
		show(this.toString());
	}

	public void show(String title) {
		cvShowImage(title, img);
		cvWaitKey();
	}

	public void save(String filename) {
		cvSaveImage(filename, img);
	}

	public void addProcessor(ImageProcessor processor) {
		processors.add(processor);
	}

	public Image threshold(int lower, int upper) {
		IplImage temp = IplImage.create(img.cvSize(), IPL_DEPTH_8U, 1);
		cvCvtColor(img, temp, CV_BGR2GRAY);

		cvThreshold(temp, temp, lower, upper, CV_THRESH_BINARY);

		return new Image(temp);
	}

	public Image findCharacters(FeatureDetector detector, FeatureLinker linker) {
		IplImage temp = IplImage.create(img.cvSize(), IPL_DEPTH_8U, 1);
		cvCvtColor(img, temp, CV_BGR2GRAY);
		Image tempImg = new Image(temp);

		// pre-processing
		for (ImageProcessor processor : processors) {
			processor.process(temp);
		}

		/*cvSetZero(temp);
		Feature f0 = new Feature(380, 567, 20, 4, 1.5707963267948966);
		Feature f1 = new Feature(372.5, 411.5, 19, 11, 1.5707963267948966);
		f0.draw(temp, CvScalar.WHITE);
		f1.draw(temp, CvScalar.GRAY);
		
		String title = "" + f0.distance(f1);

		tempImg.show(title);*/

		// retrieve features of the image
		List<Feature> features = detector.findFeatures(temp);

		System.out.println(features.size());

		for (Feature f : features) {
			f.draw(img, CvScalar.BLACK);
		}
		show();

		List<LinkedFeature> linkedFeatures = linker.linkFeatures(features, img);

		System.out.println(linkedFeatures.size());

		// merge components
		for (LinkedFeature lf : linkedFeatures) {
			lf.draw(img, CvScalar.GREEN);
		}

		show();

		return tempImg;
	}
}

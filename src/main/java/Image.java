import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

import java.util.*;

public class Image {
	private IplImage img;
	private List<ImageProcessor> processors;
	private boolean showProcessedImage = false;
	private boolean showDetectedFeatures = false;
	private boolean showLinkedFeatures = false;

	public Image(String filename) {
		this.img = cvLoadImage(filename);
		this.processors = new ArrayList<ImageProcessor>();
	}

	public Image(IplImage img) {
		this.img = img;
		this.processors = new ArrayList<ImageProcessor>();
	}

	public IplImage iplImage() {
		return img;
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

	public void setImageDisplay(boolean showProcessedImage,
			boolean showDetectedFeatures, boolean showLinkedFeatures) {
		this.showProcessedImage = showProcessedImage;
		this.showDetectedFeatures = showDetectedFeatures;
		this.showLinkedFeatures = showLinkedFeatures;
	}

	public List<LinkedFeature> findText(FeatureDetector detector, FeatureLinker linker) {
		IplImage temp = IplImage.create(img.cvSize(), IPL_DEPTH_8U, 1);
		Image tempImg = new Image(temp);

		// pre-processing
		for (ImageProcessor processor : processors) {
			processor.process(temp, img);
		}

		if (showProcessedImage) {
			tempImg.show();
		}

		// detect features in the image
		List<Feature> features = detector.findFeatures(temp);
		System.out.println("number of features detected: " + features.size());

		for (Feature f : features) {
			f.draw(img, CvScalar.BLACK);
			f.box().draw(img, CvScalar.BLACK);
		}

		if (showDetectedFeatures) {
			show();
		}

		// link features together
		List<LinkedFeature> linkedFeatures = linker.linkFeatures(features, img);

		System.out.println("number of features after linking: "
				+ linkedFeatures.size());

		for (LinkedFeature lf : linkedFeatures) {
			lf.draw(img, CvScalar.GREEN);
		}

		if (showLinkedFeatures) {
			show();
		}

		return linkedFeatures;
	}
}

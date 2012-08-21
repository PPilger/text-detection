import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

import java.util.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Image {
	private ImageCollection images;
	private ImageDisplay detectedFeaturesDisplay;
	private ImageDisplay linkedFeaturesDisplay;

	public Image(String filename) {
		IplImage img = cvLoadImage(filename);
		this.images = new ImageCollection(img);
	}

	public Image(IplImage img) {
		this.images = new ImageCollection(img);
	}
	
	public ImageCollection getImageCollection() {
		return images;
	}

	public void save(String filename) {
		cvSaveImage(filename, images.getProcessed());
	}

	public void process(ImageProcessor processor) {
		processor.process(images);
	}

	public void setImageDisplay(ImageDisplay detectedFeaturesDisplay,
			ImageDisplay linkedFeaturesDisplay) {
		this.detectedFeaturesDisplay = detectedFeaturesDisplay;
		this.linkedFeaturesDisplay = linkedFeaturesDisplay;
	}

	public List<LinkedFeature> findText(FeatureDetector detector,
			FeatureLinker linker) {
		IplImage img = images.getProcessed();
		
		// detect features in the image
		List<Feature> features;
		{
			features = detector.findFeatures(img);

			System.out.println("number of features detected: "
					+ features.size());

			for (Feature f : features) {
				f.draw(img, CvScalar.BLACK);
				f.box().draw(img, CvScalar.BLACK);
			}

			if (detectedFeaturesDisplay != null) {
				detectedFeaturesDisplay.show(img);
			}
		}

		// link features together
		List<LinkedFeature> linkedFeatures;
		{
			linkedFeatures = linker.linkFeatures(features, img);

			System.out.println("number of features after linking: "
					+ linkedFeatures.size());

			for (LinkedFeature lf : linkedFeatures) {
				lf.draw(img, CvScalar.GREEN);
			}

			if (linkedFeaturesDisplay != null) {
				linkedFeaturesDisplay.show(img);
			}
		}

		return linkedFeatures;
	}
}

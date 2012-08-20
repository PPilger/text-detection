import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

import java.util.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Image {
	private IplImage img;
	private List<ImageProcessor> processors;
	private ImageDisplay processedImageDisplay;
	private ImageDisplay detectedFeaturesDisplay;
	private ImageDisplay linkedFeaturesDisplay;

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

	public void save(String filename) {
		cvSaveImage(filename, img);
	}

	public void addProcessor(ImageProcessor processor) {
		processors.add(processor);
	}

	public void setImageDisplay(ImageDisplay processedImageDisplay,
			ImageDisplay detectedFeaturesDisplay,
			ImageDisplay linkedFeaturesDisplay) {
		this.processedImageDisplay = processedImageDisplay;
		this.detectedFeaturesDisplay = detectedFeaturesDisplay;
		this.linkedFeaturesDisplay = linkedFeaturesDisplay;
	}

	public List<LinkedFeature> findText(FeatureDetector detector,
			FeatureLinker linker) {
		ImageCollection images = new ImageCollection(img);

		// do pre-processing
		IplImage processed;
		{
			processed = images.getProcessed();

			for (ImageProcessor processor : processors) {
				processor.process(images);
			}

			if (processedImageDisplay != null) {
				processedImageDisplay.show(processed);
			}
		}

		// detect features in the image
		List<Feature> features;
		{
			features = detector.findFeatures(processed);

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

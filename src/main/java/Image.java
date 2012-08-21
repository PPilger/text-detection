import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_core.*;

import java.util.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Image {
	/*private IplImage color;
	private IplImage gray;
	private IplImage img;
	private IplImage temp;
	private IplImage red;
	private IplImage green;
	private IplImage blue;
	private IplImage rgRatio;
	private IplImage rbRatio;
	private IplImage gbRatio;*/
	
	private ImageCollection images;
	private ImageDisplay detectedFeaturesDisplay;
	private ImageDisplay linkedFeaturesDisplay;

	public Image(String filename) {
		IplImage img = cvLoadImage(filename);
		this.images = new ImageCollection(img);
	}

	public Image(IplImage img) {
		this.images = new ImageCollection(img);
		/*this.color = img;
		
		this.gray = IplImage.create(color.cvSize(), IPL_DEPTH_8U, 1);
		cvCvtColor(color, gray, CV_BGR2GRAY);
		
		this.img = gray.clone();
		this.temp = gray.clone();
		
		images.put("color", color);
		images.put("gray", gray);
		images.put("processed", cvCloneImage(gray));
		images.put("temp", cvCloneImage(gray));
		
		IplImage red = cvCloneImage(gray);
		IplImage green = cvCloneImage(gray);
		IplImage blue = cvCloneImage(gray);
		
		cvSplit(color, blue, green, red, null);

		images.put("red", red);
		images.put("green", green);
		images.put("blue", blue);

		IplImage rgRatio = IplImage.create(gray.cvSize(), IPL_DEPTH_32F, 1);
		IplImage rbRatio = cvCloneImage(rgRatio);
		IplImage gbRatio = cvCloneImage(rgRatio);

		cvDiv(red, green, rgRatio, 1);
		cvDiv(red, blue, rbRatio, 1);
		cvDiv(green, blue, gbRatio, 1);

		images.put("rgRatio", rgRatio);
		images.put("rbRatio", rbRatio);
		images.put("gbRatio", gbRatio);*/
	}
	
	public ImageCollection getImageCollection() {
		return images;
	}

	public void save(String filename) {
		cvSaveImage(filename, images.getResult());
	}

	public void process(ImageProcessor processor) {
		processor.process(images);
	}

	public void setImageDisplay(ImageDisplay detectedFeaturesDisplay,
			ImageDisplay linkedFeaturesDisplay) {
		this.detectedFeaturesDisplay = detectedFeaturesDisplay;
		this.linkedFeaturesDisplay = linkedFeaturesDisplay;
	}

	public FeatureSet findText(FeatureDetector detector,
			FeatureLinker linker) {
		IplImage input = images.getProcessed();
		IplImage result = images.getResult();
		
		
		// detect features in the image
		List<Feature> features;
		{
			features = detector.findFeatures(input);

			System.out.println("number of features detected: "
					+ features.size());

			for (Feature f : features) {
				f.draw(result, CvScalar.BLACK);
				f.box().draw(result, CvScalar.BLACK);
			}

			if (detectedFeaturesDisplay != null) {
				detectedFeaturesDisplay.show(result);
			}
		}

		// link features together
		FeatureSet featureSet;
		{
			featureSet = linker.linkFeatures(features, result);

			System.out.println("number of features after linking: "
					+ featureSet.size());

			featureSet.draw(result, CvScalar.GREEN);

			if (linkedFeaturesDisplay != null) {
				linkedFeaturesDisplay.show(result);
			}
		}

		return featureSet;
	}
}

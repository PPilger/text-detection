import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class ColorThresholdProcessor implements ImageProcessor {
	private int rThreshold;
	private int gThreshold;
	private int bThreshold;

	public ColorThresholdProcessor(int rThreshold,
			int gThreshold, int bThreshold) {
		this.rThreshold = rThreshold;
		this.gThreshold = gThreshold;
		this.bThreshold = bThreshold;
	}

	@Override
	public void process(IplImage img, IplImage colorImg) {
		IplImage rImg = IplImage.create(cvSize(img.width(), img.height()), 8,
				1);
		IplImage gImg = IplImage.create(cvSize(img.width(), img.height()), 8,
				1);
		IplImage bImg = IplImage.create(cvSize(img.width(), img.height()), 8,
				1);
		
		cvSplit(colorImg, rImg, gImg, bImg, null);
		
		cvThreshold(rImg, rImg, rThreshold, 255, CV_THRESH_BINARY);
		cvThreshold(gImg, gImg, gThreshold, 255, CV_THRESH_BINARY);
		cvThreshold(bImg, bImg, bThreshold, 255, CV_THRESH_BINARY);
		
		cvSetZero(img);
		cvSet(img, CvScalar.WHITE, rImg);
		cvSet(img, CvScalar.WHITE, gImg);
		cvSet(img, CvScalar.WHITE, bImg);
	}

}

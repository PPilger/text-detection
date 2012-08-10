import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

public class ColorRangeProcessor implements ImageProcessor {
	private IplImage mask;

	public ColorRangeProcessor(IplImage colorImg, int rMin, int rMax, int gMin, int gMax, int bMin, int bMax, boolean and) {
			IplImage rImg = IplImage.create(cvSize(colorImg.width(), colorImg.height()), 8,
					1);
			IplImage gImg = IplImage.create(cvSize(colorImg.width(), colorImg.height()), 8,
					1);
			IplImage bImg = IplImage.create(cvSize(colorImg.width(), colorImg.height()), 8,
					1);
			this.mask = IplImage.create(
					cvSize(colorImg.width(), colorImg.height()), 8, 1);
			cvSplit(colorImg, rImg, gImg, bImg, null);
			cvThreshold(rImg, rImg, rMin, 255, CV_THRESH_TOZERO);
			cvThreshold(rImg, rImg, rMax, 255, CV_THRESH_TOZERO_INV);
			cvThreshold(gImg, gImg, gMin, 255, CV_THRESH_TOZERO);
			cvThreshold(gImg, gImg, gMax, 255, CV_THRESH_TOZERO_INV);
			cvThreshold(bImg, bImg, bMin, 255, CV_THRESH_TOZERO);
			cvThreshold(bImg, bImg, bMax, 255, CV_THRESH_TOZERO_INV);
			
			if(and) {
				cvAnd(rImg, gImg, mask, null);
				cvAnd(mask, bImg, mask, null);
			} else {
				cvOr(rImg, gImg, mask, null);
				cvOr(mask, bImg, mask, null);
			}
		}

	@Override
	public void process(IplImage img) {
		cvSetZero(img);
		cvSet(img, CvScalar.WHITE, mask);
	}
}

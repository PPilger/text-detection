import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.cpp.opencv_core.*;

public class ColorEraseProcessor implements ImageProcessor {
	private IplImage erase;

	public ColorEraseProcessor(IplImage colorImg, int rMin, int rMax, int gMin,
			int gMax, int bMin, int bMax, int border) {
		IplImage rImg = IplImage.create(
				cvSize(colorImg.width(), colorImg.height()), 8, 1);
		IplImage gImg = IplImage.create(
				cvSize(colorImg.width(), colorImg.height()), 8, 1);
		IplImage bImg = IplImage.create(
				cvSize(colorImg.width(), colorImg.height()), 8, 1);
		this.erase = IplImage.create(
				cvSize(colorImg.width(), colorImg.height()), 8, 1);
		cvSplit(colorImg, rImg, gImg, bImg, null);
		cvThreshold(rImg, rImg, rMin, 255, CV_THRESH_TOZERO);
		cvThreshold(rImg, rImg, rMax, 255, CV_THRESH_TOZERO_INV);
		cvThreshold(gImg, gImg, gMin, 255, CV_THRESH_TOZERO);
		cvThreshold(gImg, gImg, gMax, 255, CV_THRESH_TOZERO_INV);
		cvThreshold(bImg, bImg, bMin, 255, CV_THRESH_TOZERO);
		cvThreshold(bImg, bImg, bMax, 255, CV_THRESH_TOZERO_INV);
		cvSet(erase, CvScalar.WHITE, rImg);
		cvSet(erase, CvScalar.WHITE, gImg);
		cvSet(erase, CvScalar.WHITE, bImg);
		new DilateProcessor(border).process(erase);
	}

	@Override
	public void process(IplImage img) {
		cvSet(img, CvScalar.BLACK, erase);
	}

}

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class ColorEraseProcessor implements ImageProcessor {
	private int rMin;
	private int rMax;
	private int gMin;
	private int gMax;
	private int bMin;
	private int bMax;
	private int border;
	private boolean and;

	public ColorEraseProcessor(int rMin, int rMax, int gMin, int gMax,
			int bMin, int bMax, int border, boolean and) {
		this.rMin = rMin;
		this.rMax = rMax;
		this.gMin = gMin;
		this.gMax = gMax;
		this.bMin = bMin;
		this.bMax = bMax;
		this.border = border;
		this.and = and;
	}

	@Override
	public void process(IplImage img, IplImage colorImg, IplImage temp) {
		IplImage rImg = IplImage
				.create(cvSize(img.width(), img.height()), 8, 1);
		IplImage gImg = IplImage
				.create(cvSize(img.width(), img.height()), 8, 1);
		IplImage bImg = IplImage
				.create(cvSize(img.width(), img.height()), 8, 1);

		cvSplit(colorImg, rImg, gImg, bImg, null);

		cvThreshold(rImg, rImg, rMin, 255, CV_THRESH_TOZERO);
		cvThreshold(rImg, rImg, rMax, 255, CV_THRESH_TOZERO_INV);
		cvThreshold(gImg, gImg, gMin, 255, CV_THRESH_TOZERO);
		cvThreshold(gImg, gImg, gMax, 255, CV_THRESH_TOZERO_INV);
		cvThreshold(bImg, bImg, bMin, 255, CV_THRESH_TOZERO);
		cvThreshold(bImg, bImg, bMax, 255, CV_THRESH_TOZERO_INV);

		cvSetZero(temp);
		
		if(and) {
			cvAnd(rImg, gImg, temp, null);
			cvAnd(temp, bImg, temp, null);
		} else {
			cvOr(rImg, gImg, temp, null);
			cvOr(temp, bImg, temp, null);
		}
		cvSet(temp, CvScalar.WHITE, temp);
		
		new DilateProcessor(border).process(temp, null, null);

		cvSet(img, CvScalar.BLACK, temp);
	}

}

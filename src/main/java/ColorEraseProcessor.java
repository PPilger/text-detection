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

	public ColorEraseProcessor(int rMin, int rMax, int gMin, int gMax,
			int bMin, int bMax, int border) {
		this.rMin = rMin;
		this.rMax = rMax;
		this.gMin = gMin;
		this.gMax = gMax;
		this.bMin = bMin;
		this.bMax = bMax;
		this.border = border;
	}

	@Override
	public void process(IplImage img, IplImage colorImg) {
		IplImage rImg = IplImage
				.create(cvSize(img.width(), img.height()), 8, 1);
		IplImage gImg = IplImage
				.create(cvSize(img.width(), img.height()), 8, 1);
		IplImage bImg = IplImage
				.create(cvSize(img.width(), img.height()), 8, 1);
		IplImage erase = IplImage.create(cvSize(img.width(), img.height()), 8,
				1);

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

		new DilateProcessor(border).process(erase, null);

		cvSet(img, CvScalar.BLACK, erase);
	}

}

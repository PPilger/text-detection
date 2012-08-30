package image;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.IplConvKernel;

public class VarianceProcessor extends SimpleImageProcessor {
	private int size;
	private int min;

	public VarianceProcessor(int size, int min) {
		this.size = size;
		this.min = min;
	}

	@Override
	public void process(IplImage img, IplImage temp) {
		// CvMat kernel = CvMat.create(size, size, CV_32FC1);
		// cvSetZero(kernel);
		// cvAddS(kernel, cvScalarAll(1 / 255.0), kernel, null);
		// cvFilter2D(img, temp, kernel, cvPoint(-1, -1));

		cvSmooth(img, temp, CV_GAUSSIAN, size, size, 0, 0);

		IplImage dil = temp.clone();
		IplConvKernel strel = cvCreateStructuringElementEx(size, size,
				size / 2, size / 2, CV_SHAPE_RECT, null);
		cvDilate(temp, dil, strel, 1);
		cvCmp(temp, dil, temp, CV_CMP_EQ);
		cvAnd(temp, dil, temp, null);
		

		strel = cvCreateStructuringElementEx(21, 21,10, 10, CV_SHAPE_RECT,
				null);
		cvDilate(temp, temp, strel, 1);
		//cvErode(temp, temp, strel, 1);

		 //new DilateProcessor(size).process(temp, null);
		 cvMul(img, temp, temp, 1 / 255.0);
		cvCmpS(temp, min, img, CV_CMP_GE);

		// cvSmooth(img, temp, CV_GAUSSIAN, size, size, 0, 0);
		// cvMul(img, temp, temp, 1/255.0);
		// cvCmpS(temp, min, img, CV_CMP_GE);
	}
}

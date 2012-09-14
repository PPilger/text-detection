package image;

import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import validator.IValidator;

/**
 * Removes objects with an invalid perimeter.
 * 
 * @author PilgerstorferP
 * 
 */
public class PerimeterProcessor extends SimpleImageProcessor {
	private IValidator perimeter;

	public PerimeterProcessor(IValidator perimeter) {
		super();
		this.perimeter = perimeter;
	}

	@Override
	public void process(IplImage img, IplImage temp) {
		CvMemStorage mem = cvCreateMemStorage(0);
		CvSeq contour = new CvSeq();

		cvConvert(img, temp);
		cvFindContours(temp, mem, contour, sizeof(CvContour.class),
				CV_RETR_LIST, CV_CHAIN_APPROX_NONE);

		for (; contour != null; contour = contour.h_next()) {
			if (perimeter.isValid(contour.total())) {
				// remove object
				cvDrawContours(img, contour, CvScalar.BLACK, CvScalar.BLACK, 0,
						CV_FILLED, 8);
			}
		}
	}

}

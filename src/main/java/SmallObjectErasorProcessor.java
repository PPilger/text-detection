import static com.googlecode.javacpp.Loader.sizeof;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class SmallObjectErasorProcessor extends SimpleImageProcessor {
	private int minSize;

	public SmallObjectErasorProcessor(int minSize) {
		super();
		this.minSize = minSize;
	}

	@Override
	public void process(IplImage img, IplImage temp) {
		CvMemStorage mem = CvMemStorage.create();
		CvSeq contour = new CvSeq();
		
		cvConvert(img, temp);
		cvFindContours(temp, mem, contour, sizeof(CvContour.class),
				CV_RETR_LIST, CV_CHAIN_APPROX_NONE);

		for (; contour != null; contour = contour.h_next()) {
			if (contour.total() < minSize) {
				//cvFloodFill(processed, new CvPoint(cvGetSeqElem(contour, 0)), CvScalar.GRAY, CvScalar.BLACK, CvScalar.BLACK, null, 4, null);
				cvDrawContours(img, contour, CvScalar.BLACK, CvScalar.BLACK, 0, CV_FILLED, 8);
				cvDrawContours(temp, contour, CvScalar.GRAY, CvScalar.GRAY, 0, CV_FILLED, 8);
			}
		}
	}
	
}

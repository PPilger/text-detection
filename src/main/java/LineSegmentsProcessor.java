import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class LineSegmentsProcessor extends SingleImageProcessor {
	private int threshold;
	private int minLength;
	private int maxGap;

	public LineSegmentsProcessor(int threshold, int minLength, int maxGap) {
		this.threshold = threshold;
		this.minLength = minLength;
		this.maxGap = maxGap;
	}

	@Override
	public void process(IplImage img, IplImage temp) {
		CvMemStorage mem = CvMemStorage.create();
		CvSeq lines = cvHoughLines2(img, mem, CV_HOUGH_PROBABILISTIC,
				0.5, Angle180.degToRad(0.5), threshold, minLength, maxGap);

		cvSetZero(img);
		
		for (int i = 0; i < lines.total(); i+=1) {
			CvPoint p0 = new CvPoint(cvGetSeqElem(lines, i));
			CvPoint p1 = new CvPoint(cvGetSeqElem(lines, i).position(1));

			cvDrawLine(img, p0, p1, CvScalar.WHITE, 2, 0, 0);
		}
	}
}

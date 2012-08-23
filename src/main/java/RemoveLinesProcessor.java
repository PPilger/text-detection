import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class RemoveLinesProcessor extends SimpleImageProcessor {
	private int threshold;

	public RemoveLinesProcessor(int threshold) {
		this.threshold = threshold;
	}

	@Override
	public void process(IplImage img, IplImage temp) {
		cvSetZero(temp);

		CvMemStorage mem = cvCreateMemStorage(0);
		CvSeq lines = cvHoughLines2(img, mem, CV_HOUGH_STANDARD, 0.5,
				Angle180.degToRad(0.5), threshold, 0, 0);

		for (int i = 0; i < lines.total(); i++) {
			CvPoint2D32f polar = new CvPoint2D32f(cvGetSeqElem(lines, i));

			Line2D line = new Line2D(polar.x(), polar.y());
			line.draw(img, CvScalar.BLACK);
			line.draw(temp, CvScalar.WHITE);
		}
	}

}

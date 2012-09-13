package image;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

import java.util.ArrayList;
import java.util.List;

import math.Angle180;
import math.InfiniteLine;

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
				Math.toRadians(0.5), threshold, 0, 0);

		for (int i = 0; i < lines.total(); i++) {
			CvPoint2D32f polar = new CvPoint2D32f(cvGetSeqElem(lines, i));

			InfiniteLine line = new InfiniteLine(polar.x(), polar.y());
			line.draw(img, CvScalar.BLACK);
			line.draw(temp, CvScalar.WHITE);
		}
	}

}

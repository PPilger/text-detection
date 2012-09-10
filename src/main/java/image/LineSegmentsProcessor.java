package image;

import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import math.Angle180;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class LineSegmentsProcessor extends SimpleImageProcessor {
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
		CvMemStorage mem = cvCreateMemStorage(0);
		int width = 512;
		int height = 512;

		int rows = (int) Math.ceil(img.height() *1.5 / (double) height);
		int cols = (int) Math.ceil(img.width() *1.5 / (double) width);

		temp = img.clone();
		cvSetZero(img);
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int xOffset = j * width / 2;
				int yOffset = i * height / 2;

				if (img.width() < xOffset + width) {
					yOffset = img.width() - width;
				}

				if (img.height() < yOffset + height) {
					yOffset = img.height() - height;
				}

				CvRect rect = cvRect(xOffset, yOffset, width, height);
				cvSetImageROI(img, rect);
				cvSetImageROI(temp, rect);
				
				CvSeq lines = cvHoughLines2(temp, mem,
						CV_HOUGH_PROBABILISTIC, .5, Angle180.degToRad(.5),
						threshold, minLength, maxGap);

				for (int k = 0; k < lines.total(); k += 1) {
					CvPoint p0 = new CvPoint(cvGetSeqElem(lines, k));
					CvPoint p1 = new CvPoint(cvGetSeqElem(lines, k).position(1));
					cvDrawLine(img, p0, p1, CvScalar.WHITE, 2, 0, 0);
				}
			}
		}

		cvResetImageROI(img);
		cvResetImageROI(temp);

	}
}

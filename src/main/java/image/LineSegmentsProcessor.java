package image;

import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvDrawLine;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvRect;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvResetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvSetZero;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_HOUGH_PROBABILISTIC;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvHoughLines2;
import validator.DMaximum;
import validator.DMinimum;

import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * Detects line segments. Use together with EraseProcessor to remove lines from
 * an image.
 * 
 * @author PilgerstorferP
 * 
 */
public class LineSegmentsProcessor extends SimpleImageProcessor {
	private int threshold;
	private DMinimum length;
	private DMaximum gap;

	/**
	 * @param houghThreshold
	 *            the threshold used by hough transform to detect lines
	 * @param length
	 *            the minimum line length
	 * @param gap
	 *            the maximum gap between line fragments
	 */
	public LineSegmentsProcessor(int houghThreshold, DMinimum length,
			DMaximum gap) {
		this.threshold = houghThreshold;
		this.length = length;
		this.gap = gap;
	}

	@Override
	public void process(IplImage img, IplImage temp) {
		CvMemStorage mem = cvCreateMemStorage(0);

		// define width and height of each processing block
		int width = 512;
		int height = 512;

		// calculate number of rows and cols of processing blocks
		int rows = (int) Math.ceil(img.height() * 1.5 / (double) height);
		int cols = (int) Math.ceil(img.width() * 1.5 / (double) width);

		temp = img.clone();
		cvSetZero(img);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				// calculate the offset of the current processing block
				int xOffset = j * width / 2;
				int yOffset = i * height / 2;

				if (img.width() < xOffset + width) {
					yOffset = img.width() - width;
				}

				if (img.height() < yOffset + height) {
					yOffset = img.height() - height;
				}

				// set the image region of interest
				CvRect rect = cvRect(xOffset, yOffset, width, height);
				cvSetImageROI(img, rect);
				cvSetImageROI(temp, rect);

				// use hough transform to detect line segments
				cvClearMemStorage(mem);
				CvSeq lines = cvHoughLines2(temp, mem, CV_HOUGH_PROBABILISTIC,
						.5, Math.toRadians(.5), threshold, length.getMin(),
						gap.getMax());

				// draw detected lines
				for (int k = 0; k < lines.total(); k += 1) {
					CvPoint p0 = new CvPoint(cvGetSeqElem(lines, k));
					CvPoint p1 = new CvPoint(cvGetSeqElem(lines, k).position(1));

					cvDrawLine(img, p0, p1, CvScalar.WHITE, 2, 0, 0);
				}
			}
		}

		cvResetImageROI(img);
		cvResetImageROI(temp);

		cvReleaseMemStorage(mem);
	}
}

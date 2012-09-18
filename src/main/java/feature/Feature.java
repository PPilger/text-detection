package feature;

import static com.googlecode.javacv.cpp.opencv_core.CV_32FC1;
import static com.googlecode.javacv.cpp.opencv_core.CV_8UC1;
import static com.googlecode.javacv.cpp.opencv_core.CV_8UC3;
import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMat;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMatHeader;
import static com.googlecode.javacv.cpp.opencv_core.cvDrawCircle;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSubRect;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint2D32f;
import static com.googlecode.javacv.cpp.opencv_core.cvRect;
import static com.googlecode.javacv.cpp.opencv_core.cvResetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvSetImageROI;
import static com.googlecode.javacv.cpp.opencv_core.cvSetZero;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_imgproc.cv2DRotationMatrix;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvWarpAffine;
import image.Image;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

import math.Box;
import math.Vector;

import com.googlecode.javacv.cpp.opencv_core.CvArr;
import com.googlecode.javacv.cpp.opencv_core.CvBox2D;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 * A Feature defines an object in an image. It is represented by the bounding
 * box.
 * 
 * @author PilgerstorferP
 * 
 */
public abstract class Feature extends Box implements Comparable<Feature> {
	// every feature gets a unique id for JSON and image output
	private int id;
	public static int counter = 0;

	public Feature(CvBox2D box) {
		super(box);
		this.id = counter++;
	}

	public int[] getConvexHull() {
		return Vector.asIntArray(getCorners());
	}

	/**
	 * The feature is drawn as a rectangle with a point in the center.
	 */
	public void draw(CvArr img, CvScalar color) {
		Vector center = getCenter();
		CvPoint cvCenter = cvPoint((int) Math.round(center.x),
				(int) Math.round(center.y));

		cvDrawCircle(img, cvCenter, 1, color, 2, 0, 0);
		super.draw(img, color);
	}

	public abstract void fill(CvArr img, CvScalar color);

	/**
	 * The rating is defined by the sub-classes. It has to be positive. A higher
	 * rating equals a better feature.
	 * 
	 * @return
	 */
	public abstract double getRating();

	/**
	 * Returns the Feature in the following format:
	 * 
	 * {"id: " id, "angle: " angle, "corners: " [corner0, corner1, corner2,
	 * corner3], "buffered: " [buffered0, buffered0, buffered0, buffered0]}
	 * 
	 * buffered are the corners of the rectangle that is created by increasing
	 * width and height by the amount of <code>border</code>
	 * 
	 * @param border
	 * @return
	 */
	public String toJSON(int border) {
		Vector[] corners = getCorners();
		Vector[] buffered = makeBorder(border).getCorners();

		return String
				.format(Locale.US,
						"{\"id\": %d, \"angle\": %.2f, \"corners\": %s, \"buffered\": %s}",
						id, getAngle().getDegrees(), Arrays.toString(corners),
						Arrays.toString(buffered));
	}

	public void write(IplImage source, String folder) {
		write(source, folder, 0);
	}

	/**
	 * Writes the feature image into a file. The feature image is a rectangular
	 * cut-out of <code>source</code> (the bounding rectangle expanded by the
	 * border is cut out).
	 * 
	 * @param source
	 * @param folder
	 * @param border
	 */
	public void write(IplImage source, String folder, int border) {
		// calculate position and size of the interesting area around the
		// feature
		int x;
		int y;
		int size;
		{
			Vector min = getMin();
			Vector max = getMax();
			x = (int) Math.round(min.x) - border;
			y = (int) Math.round(min.y) - border;

			double w = max.x - min.x;
			double h = max.y - min.y;

			if (w >= h) {
				size = (int) Math.round(w) + 2 * border;
				y -= (int) Math.round((w - h) / 2);
			} else {
				size = (int) Math.round(h) + 2 * border;
				x -= (int) Math.round((h - w) / 2);
			}
		}

		// create a quadratic sub-image with the feature in the center
		IplImage sub;
		{
			int tx = x;
			int ty = y;
			int twidth = size;
			int theight = size;

			// edge treatment
			if (tx < 0) {
				twidth += tx;
				tx = 0;
			}
			if (source.width() < tx + twidth) {
				twidth = source.width() - tx;
			}
			if (ty < 0) {
				theight += ty;
				ty = 0;
			}
			if (source.height() < ty + theight) {
				theight = source.height() - ty;
			}

			// cut out the part from the source image
			CvMat mat = cvCreateMatHeader(theight, twidth,
					source.nChannels() == 1 ? CV_8UC1 : CV_8UC3);
			CvRect rect = cvRect(tx, ty, twidth, theight);
			cvGetSubRect(source, mat, rect);

			// create a quadratic image with the feature-center in the middle
			// everything outside the valid range of the source image is left
			// black
			sub = cvCreateImage(cvSize(size, size), source.depth(),
					source.nChannels());
			cvSetZero(sub);
			cvSetImageROI(sub, cvRect(tx - x, ty - y, twidth, theight));
			cvCopy(mat, sub);
			cvResetImageROI(sub);
		}

		// rotate the sub-image
		{
			CvMat matrix = cvCreateMat(2, 3, CV_32FC1);
			cv2DRotationMatrix(
					cvPoint2D32f(sub.width() / 2., sub.height() / 2.),
					getAngle().getDegrees(), 1, matrix);
			cvWarpAffine(sub, sub, matrix);
		}

		// cut out the feature of the rotated image
		IplImage output;
		{
			output = sub;

			int width = (int) Math.round(getWidth()) + 2 * border;
			int height = (int) Math.round(getHeight()) + 2 * border;
			int dw = (size - width) / 2;
			int dh = (size - height) / 2;

			CvRect rect = cvRect(dw, dh, width, height);
			cvSetImageROI(output, rect);
		}

		Image.write(output, folder + File.separatorChar + "Label_" + id
				+ ".jpg");
	}

	@Override
	public int compareTo(Feature other) {
		double r0 = getRating();
		double r1 = other.getRating();

		if (r0 < r1) {
			return 1;
		} else if (r0 > r1) {
			return -1;
		} else {
			return 0;
		}
	}
}

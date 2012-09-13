package feature;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import image.Image;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;

import math.Box;
import math.Vector;

public abstract class Feature extends Box implements Comparable<Feature> {
	private int id;
	public static int counter = 0;

	public Feature(CvBox2D box) {
		super(box);
		this.id = counter++;
	}

	public Feature(double xcenter, double ycenter, double width, double height,
			double angle) {
		super(xcenter, ycenter, width, height, angle);
	}

	public int[] getICorners() {
		return Vector.asIntArray(getCorners());
	}

	public void draw(CvArr img, CvScalar color) {
		cvDrawCircle(img, getCvCenter(), 1, color, 2, 0, 0);
		super.draw(img, color);
	}

	public abstract void fill(CvArr img, CvScalar color);
	
	public abstract double getRating();

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
		
		if(r0 < r1){
			return 1;
		} else if(r0 > r1) {
			return -1;
		} else {
			return 0;
		}
	}
}

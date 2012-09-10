package feature;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import image.Image;
import image.ImageDisplay;

import java.io.File;
import java.util.Locale;

import math.Box;
import math.Vector;

public abstract class Feature extends Box {
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

	public String toJSON() {
		Vector[] corners = getCorners();
		String corner0 = corners[0].toJSON();
		String corner1 = corners[1].toJSON();
		String corner2 = corners[2].toJSON();
		String corner3 = corners[3].toJSON();

		return String.format(Locale.US,
				"{\"angle\": %.2f, \"corners\": [%s, %s, %s, %s]}", getAngle()
						.getDegrees(), corner0, corner1, corner2, corner3);
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

		Image.write(output, folder + File.separatorChar + "Label " + id + ".jpg");
	}
}

package feature;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import image.DilateProcessor;
import image.Image;

import java.util.*;

import math.Angle180;
import math.Box2D;
import math.Rotation2D;
import math.Vector2D;
import miscellanous.Validator;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class BestDirectionLinkingRule extends LinkingRule {
	// parameters
	private int filterSize;
	private int dilateSize;
	private int numAngles;
	private int lineWidth;
	private Validator<Double> featureRating;
	private Validator<Integer> width;
	private Validator<Integer> height;

	private HashMap<Feature, Histogram> hists;

	private IplImage direction;
	private IplImage mask;

	public BestDirectionLinkingRule(int filterSize, int dilateSize,
			int numAngles, int lineWidth, Validator<Double> featureRating,
			Validator<Integer> width, Validator<Integer> height) {
		this.filterSize = filterSize;
		this.dilateSize = dilateSize;
		this.numAngles = numAngles;
		this.lineWidth = lineWidth;
		this.featureRating = featureRating;
		this.width = width;
		this.height = height;
	}

	@Override
	public void initialize(FeatureSet features, IplImage img) {
		IplImage input;
		IplImage max;
		IplImage temp32f;

		// initialize images
		{
			int width = img.width();
			int height = img.height();

			input = IplImage.create(width, height, IPL_DEPTH_32F, 1);
			cvSet(input, CvScalar.ONE);
			cvMul(input, img, input, 1 / 255.0);

			direction = IplImage.create(width, height, IPL_DEPTH_8U, 1);
			cvSetZero(direction);

			max = IplImage.create(width, height, IPL_DEPTH_32F, 1);
			cvSetZero(max);

			temp32f = IplImage.create(width, height, IPL_DEPTH_32F, 1);
			mask = IplImage.create(width, height, IPL_DEPTH_8U, 1);
		}

		// create filter kernels
		CvMat[] filters;
		{
			filters = new CvMat[numAngles];

			double r = filterSize / 2;
			int c = (int) Math.round(r);
			for (int i = 0; i < numAngles; i++) {
				double angle = i / (double) numAngles * Math.PI;
				double sin = Math.sin(angle);
				double cos = Math.cos(angle);

				int x = (int) Math.round(cos * r);
				int y = (int) Math.round(sin * r);

				filters[i] = CvMat.create(filterSize, filterSize, CV_32FC1);
				cvSetZero(filters[i]);

				cvDrawLine(filters[i], cvPoint(c - x, c - y),
						cvPoint(c + x, c + y), CvScalar.ONE, lineWidth, 8, 0);

				double sum = cvSum(filters[i]).blue();
				cvScale(filters[i], filters[i], 1 / sum, 0);
			}
		}

		// get the best angle for each pixel
		// the best angle is the one with the greatest value after filtering
		// the filters are stored in the array filters
		{
			IplImage filtered = temp32f;

			for (int i = 0; i < numAngles; i++) {
				CvMat filter = filters[i];

				cvFilter2D(input, filtered, filter, cvPoint(-1, -1));

				cvCmp(max, filtered, mask, CV_CMP_LT);
				cvSet(direction, cvScalarAll(i), mask);
				cvMax(max, filtered, max);
			}
		}

		// Dilate angles to privilege better angles
		{
			IplImage temp = direction.clone();
			IplImage dil = temp32f;
			IplImage sum = temp32f.clone();
			IplImage maxSum = sum.clone();
			cvSetZero(maxSum);

			DilateProcessor processor = new DilateProcessor(dilateSize);

			cvSetZero(direction);
			for (int i = 0; i < numAngles; i++) {
				cvCmpS(temp, i, mask, CV_CMP_EQ);
				cvSetZero(dil);
				cvSet(dil, CvScalar.ONE, mask);

				cvConvertScale(mask, sum, 1 / 255., 0);
				cvSmooth(sum, sum, CV_BLUR, dilateSize);

				cvMul(sum, dil, dil, 1);

				processor.process(dil, null);
				// cvDilate(dil, dil, strels[i], 1);

				cvCmp(maxSum, dil, mask, CV_CMP_LE);
				cvSet(direction, cvScalarAll(i), mask);
				cvMax(maxSum, dil, maxSum);
			}

			// cvShowImage("max", maxDil);
			// cvWaitKey();
		}

		// debug output
		// outputDirections(img, direction, mask, temp32f, numAngles);

		hists = new HashMap<Feature, Histogram>();
		for (Feature feature : features) {
			// create a mask of the feature
			cvSetZero(mask);
			feature.fill(mask, CvScalar.WHITE);

			// set sub-images of direction and mask
			CvMat subDirection;
			CvMat subMask;
			{
				Box2D box = feature.box();
				CvRect rect = Image.clip(img, box.min, box.max);

				int width = rect.width();
				int height = rect.height();

				subDirection = CvMat.createHeader(height, width, CV_8UC1);
				subMask = CvMat.createHeader(height, width, CV_8UC1);

				cvGetSubRect(direction, subDirection, rect);
				cvGetSubRect(mask, subMask, rect);
			}

			// set histogram
			Histogram hist = new Histogram(subDirection, subMask, numAngles);

			hists.put(feature, hist);
		}
	}

	@Override
	public boolean link(Feature f0, Feature f1) {
		Histogram hist0 = hists.get(f0);
		Histogram hist1 = hists.get(f1);

		double angle = 0;
		int idx = 0;

		// revoke links where the features don't have the same direction
		{
			double max = 0;

			/*int a = hist0.max();
			int b = hist1.max();
			if (hist0.get(a) < hist1.get(b)) {
				idx = b;
				max = hist1.get(b);
			} else {
				idx = a;
				max = hist0.get(a);
			}*/

			for (int i = 0; i < hist0.size(); i++) {
				//double val = (hist0.get(i) + hist1.get(i)) / 2;
				double val = Math.min(hist0.get(i), hist1.get(i));
				if (max < val) {
					idx = i;
					max = val;
				}
			}

			if (!featureRating.isValid(max)) {
				//angle = new Angle180(f0.position(), f1.position()).getRadians();
				//idx = (int) Math.round(angle / Math.PI * hist0.size());
				return false;
			}

			angle = idx * Math.PI / hist0.size();
		}

		//
		{
			Rotation2D matrix = new Rotation2D(-angle);
			Rotation2D inv = new Rotation2D(angle);

			Vector2D min0;
			Vector2D max0;
			Vector2D min1;
			Vector2D max1;

			Vector2D center;
			{
				center = f0.position().center(f1.position());

				Box2D box = f0.box();
				Vector2D[] bounds = Vector2D.bounds(matrix.rotate(box.corners,
						center));
				min0 = bounds[0];
				max0 = bounds[1];

				box = f1.box();
				bounds = Vector2D.bounds(matrix.rotate(box.corners, center));
				min1 = bounds[0];
				max1 = bounds[1];
			}

			double x;
			int w;
			if (max0.x <= min1.x || max1.x <= min0.x) {
				x = Math.min(max0.x, max1.x);
				w = (int) Math.round(Math.max(min0.x, min1.x) - x);
			} else {
				x = 0;
				w = 0;
			}

			if (!width.isValid(w)) {
				return false;
			}

			double y;
			int h;
			if (max0.y <= min1.y || max1.y <= min0.y) {
				y = 0;
				h = 0;
			} else {
				y = Math.max(min0.y, min1.y);
				h = (int) Math.round(Math.min(max0.y, max1.y) - y);
			}

			if (!height.isValid(h)) {
				return false;
			}

			// if one feature touches the other one, they are joined
			if (w == 0 || h == 0) {
				return true;
			}

			int[] points = new int[8];
			{
				int xi = (int) Math.round(x);
				int yi = (int) Math.round(y);
				int xwi = xi + w;
				int yhi = yi + h;
				points[0] = xi;
				points[1] = yi;
				points[2] = xi;
				points[3] = yhi;
				points[4] = xwi;
				points[5] = yhi;
				points[6] = xwi;
				points[7] = yi;
			}

			inv.rotate(points, center);

			cvSetZero(mask);
			cvFillConvexPoly(mask, points, 4, CvScalar.WHITE, 8, 0);

			Vector2D[] bounds = Vector2D.bounds(f0.box().min, f0.box().max,
					f1.box().min, f1.box().max);
			CvRect rect = Image.clip(mask, bounds[0], bounds[1]);

			CvMat subDirection = CvMat.createHeader(rect.height(),
					rect.width(), CV_8UC1);
			CvMat subMask = CvMat.createHeader(rect.height(), rect.width(),
					CV_8UC1);
			cvGetSubRect(direction, subDirection, rect);
			cvGetSubRect(mask, subMask, rect);

			Histogram hist = new Histogram(subDirection, subMask, hist0.size());
			int max = hist.max();
			if (Math.abs(idx - max) > 1) {
				return false;
			}
		}

		return true;
	}

	private void outputDirections(IplImage img, IplImage direction,
			IplImage mask, IplImage temp32f, int numAngles) {
		int scale = 5;
		int width = img.width() * scale;
		int height = img.height() * scale;
		IplImage imgL = IplImage.create(img.width() * scale, img.height()
				* scale, IPL_DEPTH_8U, 1);
		IplImage directionL = IplImage.create(img.width() * scale, img.height()
				* scale, IPL_DEPTH_8U, 1);
		IplImage maskL = IplImage.create(img.width() * scale, img.height()
				* scale, IPL_DEPTH_8U, 1);
		IplImage angle = IplImage.create(img.width() * scale, img.height()
				* scale, IPL_DEPTH_32F, 1);
		cvResize(img, imgL, CV_INTER_NN);
		cvResize(direction, directionL, CV_INTER_NN);
		cvResize(mask, maskL, CV_INTER_NN);
		/*
		 * int width = 200*scale; int height = 120*scale; int px = 800, py =
		 * 600; //int px = 850, py = 650; //int px = 620, py = 580; CvRect roi =
		 * cvRect(px*scale, py*scale, width, height); cvSetImageROI(imgL, roi);
		 * cvSetImageROI(directionL, roi); cvSetImageROI(maskL, roi);
		 * cvSetImageROI(angle, roi);
		 */
		img = imgL;
		direction = directionL;
		mask = maskL;

		/*
		 * IplImage angle = temp32f;
		 */
		int size3 = 5;
		int border3 = size3 / 2 + 1;

		img = img.clone();

		// get angle-map
		cvSetZero(angle);
		for (int i = 0; i < numAngles; i++) {
			double a = i / (double) numAngles * Math.PI;

			cvCmpS(direction, i, mask, CV_CMP_EQ);
			cvSet(angle, cvScalarAll(a), mask);
		}

		// average angles over the displayed area
		// not always correct (180° == 0°)
		// cvSmooth(angle, angle, CV_BLUR, size3);

		// draw angles
		for (int y = border3; y < height - border3; y += size3) {
			for (int x = border3; x < width - border3; x += size3) {
				double a = cvGetReal2D(angle, y, x);
				CvPoint p0 = cvPoint(
						x - (int) Math.round(Math.cos(a) * border3), y
								- (int) Math.round(Math.sin(a) * border3));
				CvPoint p1 = cvPoint(
						x + (int) Math.round(Math.cos(a) * border3), y
								+ (int) Math.round(Math.sin(a) * border3));
				cvDrawLine(img, p0, p1, CvScalar.GRAY, 1, 0, 0);
			}
		}

		cvSaveImage("direction.png", img);
		// cvShowImage("img", img);
		cvWaitKey();
	}
}
